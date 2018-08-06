package za.co.judge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.judge.domain.Question;
import za.co.judge.domain.Submission;
import za.co.judge.domain.SubmissionResponse;
import za.co.judge.domain.Test;
import za.co.judge.repositories.SubmissionRepository;
import za.co.judge.repositories.TeamRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private TeamRepository teamRepository;

    public Submission startSubmissionForQuestion(Question question, List<Test> testSet) {
        Submission submission = new Submission();
        submission.setTestSet(testSet);
        submission.setQuestion(question);
        return submissionRepository.save(submission);
    }

    public List<Optional<Submission>> getAttemptedSubmissionsForTeam(String name) {
        List<Long> submissionIds = getAllSubmissionIdsForTeam(name);
        return submissionIds
                .stream()
                .map(id -> submissionRepository.findById(id, 1))
                .filter(submission -> submission.isPresent() && submission.get().getSuccessful() != null)
                .collect(Collectors.toList());
    }

    public Submission checkSubmission(MultipartFile file, String teamName, Long submissionTime) throws IOException {
        SubmissionResponse submissionResponse = new SubmissionResponse();
        String submissionId;
        try(Scanner fileReader = new Scanner(file.getInputStream())) {
            submissionId = fileReader.nextLine();
        }

        Submission submission = getSubmissionBykey(submissionId);
        copyProperties(submission, submissionResponse);


        Boolean linkedToTeam = submissionLinkedToTeam(submission, teamName);
        if(!linkedToTeam){
            //TODO add log here of possibly malicious behaviour
            return new SubmissionResponse("This test set is not linked to your team. this has been recorded");
        }

        Boolean submissionAlreadyPassedTests = submission.getSuccessful();
        if(submissionAlreadyPassedTests != null && submissionAlreadyPassedTests){
            submissionResponse.setMessage("You have already successfully passed this question - new attempts are not saved. please continue with the next question");
            return submissionResponse;
        }

        Boolean submissionExpired = submissionExpired(submission, submissionTime);

        if(submissionExpired){
            submissionResponse.setMessage("This test set has expired - please request a new set");
            return submissionResponse;
        }

        HashMap<String, String> userSubmission = getSubmittedTest(file);
        Boolean answersAreCorrect = answersAreCorrect(submission, userSubmission);

        if(!answersAreCorrect){
            submissionResponse.setMessage("Your submitted answers are not correct");
            return submissionResponse;
        }

        Submission updatedSubmission = updateSubmission(submission, submissionTime, true);
        copyProperties(updatedSubmission, submissionResponse);
        submissionResponse.setMessage("Congratulations - you have successfully completed this question");
        return submissionResponse;
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
