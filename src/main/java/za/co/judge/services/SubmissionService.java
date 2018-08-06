package za.co.judge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.judge.domain.Question;
import za.co.judge.domain.Submission;
import za.co.judge.domain.Test;
import za.co.judge.repositories.SubmissionRepository;
import za.co.judge.repositories.TeamRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

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

    public Submission getSubmissionBykey(String id){
        Optional<Submission> submission = submissionRepository.findById(Long.parseLong(id), 1);
        assert submission.isPresent();
        return submission.get();
    }

    private List<Long> getAllSubmissionIdsForTeam(String teamName) {
        return (List<Long>) teamRepository.getSubmissionIdsForTeam(teamName);
    }

    public List<Optional<Submission>> getAttemptedSubmissionsForTeam(String name) {
        List<Long> submissionIds = getAllSubmissionIdsForTeam(name);
        return submissionIds
                .stream()
                .map(id -> submissionRepository.findById(id, 1))
                .filter(submission -> submission.isPresent() && submission.get().getSuccessful() != null)
                .collect(Collectors.toList());
    }

    private List<Optional<Submission>> getAllSubmissionsForTeam(String name) {
        List<Long> submissionIds = getAllSubmissionIdsForTeam(name);
        return submissionIds
                .stream()
                .map(id -> submissionRepository.findById(id, 1))
                .collect(Collectors.toList());
    }

    public Boolean submissionLinkedToTeam(Submission submissionSpecimen, String teamName){
        return getAllSubmissionsForTeam(teamName).stream().anyMatch(submission1 -> submission1.isPresent() && submission1.get().getKey().equals(submissionSpecimen.getKey()));
    }

    public Boolean answersAreCorrect(Submission submissionSpecimen, HashMap<String, String> userSubmission) {
        return submissionSpecimen.getTestSet().stream().allMatch(testSpecimen -> {
            String userOutput = userSubmission.get(testSpecimen.getKey());
            if(userOutput == null)
                return false;
            String specimenOutput = testSpecimen.getOutput();
            return userOutput.equals(specimenOutput);
        });
    }

    public Boolean submissionExpired(Submission submissionSpecimen, Long submissionTime){
        Long expirationTime = submissionSpecimen.getExpirationTime();
        return submissionTime > expirationTime;
    }

    public HashMap<String, String> getSubmittedTest(MultipartFile file) throws IOException {
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

    public Submission updateSubmission(Submission submission, Long submissionTime, Boolean success) {
        submission.setSubmissionTime(submissionTime);
        submission.setSuccessful(success);
        return submissionRepository.save(submission);
    }
}
