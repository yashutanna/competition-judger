package za.co.judge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.judge.domain.*;
import za.co.judge.repositories.SubmissionRepository;
import za.co.judge.repositories.TeamRepository;
import za.co.judge.repositories.TestRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class SubmissionService {
    private static final Integer MAX_POINTS_PER_QUESTION = 13;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ScoringService scoringService;
    @Autowired
    private QuestionService questionService;

    public Submission startSubmissionForQuestion(Question question, List<Test> testSet) {
        Submission submission = new Submission();
        submission.setTestSet(testSet);
        submission.setQuestion(question);
        return submissionRepository.save(submission);
    }

    public Submission checkSubmission(MultipartFile file, String teamName, Long submissionTime) throws IOException {
        SubmissionResponse submissionResponse = new SubmissionResponse();
        String submissionId;
        try(Scanner fileReader = new Scanner(file.getInputStream())) {
            submissionId = fileReader.nextLine();
        }
        if(submissionId.equals("-999")) {
            return checkSubmissionForSmallTestSet(file, submissionResponse);
        }
        return checkSubmissionForLargeTestSet(file, teamName, submissionTime, submissionResponse, submissionId);
    }

    private Submission checkSubmissionForSmallTestSet(MultipartFile file, SubmissionResponse submissionResponse) throws IOException {
        if (answersAreCorrect(file)){
            submissionResponse.setSuccessful(true);
            submissionResponse.setMessage("Your submitted answers are correct");
            return submissionResponse;
        }
        submissionResponse.setSuccessful(false);
        submissionResponse.setMessage("Your submitted answers are not correct");
        return submissionResponse;
    }

    private Submission checkSubmissionForLargeTestSet(MultipartFile file, String teamName, Long submissionTime, SubmissionResponse submissionResponse, String submissionId) throws IOException {
        Submission submission = getSubmissionBykey(submissionId);
        String questionName = submission.getQuestion().getName();
        copyProperties(submission, submissionResponse);

        if(!submissionLinkedToTeam(submission, teamName)){
            //TODO add log here of possibly malicious behaviour
            return new SubmissionResponse("This test set is not linked to your team. this has been recorded");
        }

        if (SubmissionAlreadyPassed(teamName, questionName)){
            submissionResponse.setMessage("You have already successfully passed this question - new attempts are not saved. please continue with the next question");
            return submissionResponse;
        }

        if (isNotNewestSubmission(teamName, submissionId)){
            //TODO add log here of possibly malicious behaviour
            submissionResponse.setMessage("You are attempting to submit an answer for an old test set - Please start with a new test set");
            return submissionResponse;
        }

        if (submissionExpired(submission, submissionTime)){
            submissionResponse.setMessage("This test set has expired - please request a new set");
            submissionResponse.setSuccessful(false);
            updateSubmission(submission, submissionTime, false);
            return submissionResponse;
        }

        if (!answersAreCorrect(file, submission)){
            submissionResponse.setMessage("Your submitted answers are not correct");
            submissionResponse.setSuccessful(false);
            updateSubmission(submission, submissionTime, false);
            return submissionResponse;
        }

        updateSubmission(submission, submissionTime, true);
        submissionResponse.setMessage("Congratulations - you have successfully completed this question");
        Integer allocatablePoints = MAX_POINTS_PER_QUESTION - questionService.countNumberOfSubmissionsForQuestion(questionName);
        Submission updatedSubmission = updateSubmission(submission, submissionTime, true);

        Long submittersId = teamRepository.findByName(teamName);
        Optional<Team> optionalTeam = teamRepository.findById(submittersId, 1);
        assert optionalTeam.isPresent();
        Team submitters = optionalTeam.get();
        submitters.setScore(submitters.getScore() + allocatablePoints);
        teamRepository.save(submitters);

        copyProperties(updatedSubmission, submissionResponse);
        return submissionResponse;
    }

    private boolean answersAreCorrect(MultipartFile file, Submission submission) throws IOException {
        HashMap<String, String> userSubmission = getSubmittedTest(file);
        return answersAreCorrect(submission, userSubmission);
    }
    private boolean answersAreCorrect(MultipartFile file) throws IOException {
        HashMap<String, String> userSubmission = getSubmittedTest(file);
        return answersAreCorrect(userSubmission);
    }

    private boolean isNotNewestSubmission(String teamName, String submissionId) {
        List<Submission> teamSubmissions = (List<Submission>) submissionRepository.findByTeam(teamName);
        return !teamSubmissions.get(0).getId().toString().equals(submissionId);
    }

    private boolean SubmissionAlreadyPassed(String teamName, String questionName) {
        Boolean submissionAlreadyPassedTests = questionService.teamHasAlreadySuccessfullyAnsweredQuestion(questionName, teamName);
        return submissionAlreadyPassedTests != null && submissionAlreadyPassedTests;
    }

    private Submission getSubmissionBykey(String id){
        Optional<Submission> submission = submissionRepository.findById(Long.parseLong(id), 1);
        assert submission.isPresent();
        return submission.get();
    }

    private List<Long> getAllSubmissionIdsForTeam(String teamName) {
        return (List<Long>) teamRepository.getSubmissionIdsForTeam(teamName);
    }

    private List<Optional<Submission>> getAllSubmissionsForTeam(String name) {
        List<Long> submissionIds = getAllSubmissionIdsForTeam(name);
        return submissionIds
                .stream()
                .map(id -> submissionRepository.findById(id, 1))
                .collect(Collectors.toList());
    }

    private Boolean submissionLinkedToTeam(Submission submissionSpecimen, String teamName){
        return getAllSubmissionsForTeam(teamName).stream().anyMatch(submission1 -> submission1.isPresent() && submission1.get().getKey().equals(submissionSpecimen.getKey()));
    }

    private Boolean answersAreCorrect(Submission submissionSpecimen, HashMap<String, String> userSubmission) {
        return submissionSpecimen.getTestSet().stream().allMatch(testSpecimen -> {
            String userOutput = userSubmission.get(testSpecimen.getKey());
            if(userOutput == null)
                return false;
            String specimenOutput = testSpecimen.getOutput();
            return userOutput.equals(specimenOutput);
        });
    }
    private Boolean answersAreCorrect(HashMap<String, String> userSubmission) {
        AtomicReference<Boolean> hasAnyIncorrect = new AtomicReference<>(false);
        userSubmission.forEach(((s, s2) -> {
            if(!testRepository.getByKey(s).get().getOutput().equals(s2)){
                hasAnyIncorrect.set(true);
            }
        }));
        return !hasAnyIncorrect.get();
    }

    private Boolean submissionExpired(Submission submissionSpecimen, Long submissionTime){
        Long expirationTime = submissionSpecimen.getExpirationTime();
        return submissionTime > expirationTime;
    }

    private HashMap<String, String> getSubmittedTest(MultipartFile file) throws IOException {
        HashMap<String, String> userSubmission = new HashMap<>();
        InputStream inputStream = file.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = bufferedReader.readLine()) != null)
        {
            if(line.length() == 0 || !line.contains("|")){
                continue;
            }
            StringTokenizer tokenizer = new StringTokenizer(line, "|");
            String id = tokenizer.nextToken();
            String output = tokenizer.nextToken();
            userSubmission.put(id, output);
        }
        return userSubmission;
    }

    private Submission updateSubmission(Submission submission, Long submissionTime, Boolean success) {
        submission.setSubmissionTime(submissionTime);
        submission.setSuccessful(success);
        return submissionRepository.save(submission);
    }
}
