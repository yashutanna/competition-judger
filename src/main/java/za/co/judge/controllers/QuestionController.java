package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.judge.domain.Question;
import za.co.judge.domain.Submission;
import za.co.judge.domain.Team;
import za.co.judge.domain.Test;
import za.co.judge.services.QuestionService;
import za.co.judge.services.SubmissionService;
import za.co.judge.services.TeamService;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private TeamService teamService;

    @PostMapping("/")
    public ResponseEntity<Question> addQuestion(@RequestBody Question question) {
        return new ResponseEntity<>(questionService.save(question), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return new ResponseEntity<>(questionService.getAllQuestions(), HttpStatus.OK);
    }


    @GetMapping("/{questionName}/small-set")
    public void getSmallTestSetDownload(@PathVariable("questionName") String questionName, Principal principal, HttpServletResponse response) throws Exception {
        Optional<Team> team = teamService.getTeam(principal.getName());
        assert team.isPresent();
        Submission submission = initializeSubmissionForQuestion(questionName, team.get(), 5);
        questionService.getTestSetFileForSubmission(submission, response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping("/{questionName}/large-set")
    public void getLargeTestSetDownload(@PathVariable("questionName") String questionName, Principal principal, HttpServletResponse response) throws Exception {
        Optional<Team> team = teamService.getTeam(principal.getName());
        assert team.isPresent();
        Submission submission = initializeSubmissionForQuestion(questionName, team.get(), 10);
        questionService.getTestSetFileForSubmission(submission, response.getOutputStream());
        response.flushBuffer();
    }

    private Submission initializeSubmissionForQuestion(String name, Team team, int setSize) {
        List<Test> questionTestSet = questionService.getQuestionTestSet(name, setSize);
        Submission submission = submissionService.startSubmissionForQuestion(questionService.getQuestion(name), questionTestSet);
        teamService.registerSubmission(team, submission);
        return submission;
    }
}
