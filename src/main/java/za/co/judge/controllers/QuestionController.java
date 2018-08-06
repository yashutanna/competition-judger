package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.judge.domain.Question;
import za.co.judge.domain.Submission;
import za.co.judge.domain.Team;
import za.co.judge.domain.Test;
import za.co.judge.services.QuestionService;
import za.co.judge.services.SubmissionService;
import za.co.judge.services.TeamService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
    public ResponseEntity<Submission> getSmallTestSet(@PathVariable("questionName") String questionName, Principal principal){
        Optional<Team> team = teamService.getTeam(principal.getName());
        return team.map(team1 -> new ResponseEntity<>(initializeSubmissionForQuestion(questionName, team1, 5), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping("/{questionName}/small-set/download")
    public void getSmallTestSetDownload(@PathVariable("questionName") String questionName, Principal principal, HttpServletResponse response) throws IOException {
        Optional<Team> team = teamService.getTeam(principal.getName());
        assert team.isPresent();
        Submission submission = initializeSubmissionForQuestion(questionName, team.get(), 5);
        questionService.getTestSetFileForSubmission(submission, response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping("/{questionName}/large-set")
    public ResponseEntity<Submission> getLargeTestSet(@PathVariable("questionName") String questionName, Principal principal){
        Optional<Team> team = teamService.getTeam(principal.getName());
        return team.map(team1 -> new ResponseEntity<>(initializeSubmissionForQuestion(questionName, team1, 10), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping("/{questionName}/large-set/download")
    public void getLargeTestSetDownload(@PathVariable("questionName") String questionName, Principal principal, HttpServletResponse response) throws IOException {
        Optional<Team> team = teamService.getTeam(principal.getName());
        assert team.isPresent();
        Submission submission = initializeSubmissionForQuestion(questionName, team.get(), 10);
        questionService.getTestSetFileForSubmission(submission, response.getOutputStream());
        response.flushBuffer();
    }

    @PostMapping(value = "/submit")
    public Boolean submit(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        Long submissionTime = Date.from(Instant.now()).getTime();
        String submissionId;
        try(Scanner fileReader = new Scanner(file.getInputStream())) {
            submissionId = fileReader.nextLine();
        }
        Submission submission = submissionService.getSubmissionBykey(submissionId);
        Boolean submissionExpired = submissionService.submissionExpired(submission, submissionTime);
        HashMap<String, String> userSubmission = submissionService.getSubmittedTest(file);
        Boolean linkedToTeam = submissionService.submissionLinkedToTeam(submission, principal.getName());
        Boolean answersAreCorrect = submissionService.answersAreCorrect(submission, userSubmission);
        return !submissionExpired && linkedToTeam && answersAreCorrect;
    }

    private Submission initializeSubmissionForQuestion(String name, Team team, int setSize) {
        List<Test> questionTestSet = questionService.getQuestionTestSet(name, setSize);
        Submission submission = submissionService.startSubmissionForQuestion(questionService.getQuestion(name), questionTestSet);
        teamService.registerSubmission(team, submission);
        return submission;
    }
}
