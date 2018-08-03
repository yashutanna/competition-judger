package za.co.judge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.judge.domain.*;
import za.co.judge.repositories.SubmissionRepository;
import za.co.judge.repositories.TeamRepository;

import java.sql.Date;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    public Submission startSubmissionForQuestion(Question question, List<Test> testSet) {
        Submission submission = new Submission();
        submission.setTestSet(testSet);
        submission.setQuestion(question);
        submission.setRequestTime(Date.from(Instant.now()).getTime());
        return submissionRepository.save(submission);
    }
}
