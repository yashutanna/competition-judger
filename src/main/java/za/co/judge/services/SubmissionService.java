package za.co.judge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.judge.domain.Question;
import za.co.judge.domain.Submission;
import za.co.judge.domain.Test;
import za.co.judge.repositories.SubmissionRepository;
import za.co.judge.repositories.TeamRepository;

import java.util.List;
import java.util.Optional;
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

    public List<Optional<Submission>> getSubmissionsForTeam(String name) {
        List<Long> submissionIds = (List<Long>) teamRepository.getSubmissionIdsForTeam(name);
        return submissionIds.stream().map(id -> submissionRepository.findById(id, 1)).collect(Collectors.toList());
    }

}
