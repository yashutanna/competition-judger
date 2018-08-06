package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.judge.domain.*;
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

import static org.springframework.beans.BeanUtils.copyProperties;

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
    public void getSmallTestSetDownload(@PathVariable("questionName") String questionName, Principal principal, HttpServletResponse response) throws IOException {
        Optional<Team> team = teamService.getTeam(principal.getName());
        assert team.isPresent();
        Submission submission = initializeSubmissionForQuestion(questionName, team.get(), 5);
        questionService.getTestSetFileForSubmission(submission, response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping("/{questionName}/large-set")
    public void getLargeTestSetDownload(@PathVariable("questionName") String questionName, Principal principal, HttpServletResponse response) throws IOException {
        Optional<Team> team = teamService.getTeam(principal.getName());
        assert team.isPresent();
        Submission submission = initializeSubmissionForQuestion(questionName, team.get(), 10);
        questionService.getTestSetFileForSubmission(submission, response.getOutputStream());
        response.flushBuffer();
    }

    @PostMapping(value = "/submit")
    public Submission submit(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        Long submissionTime = Date.from(Instant.now()).getTime();
        SubmissionResponse submissionResponse = new SubmissionResponse();
        String submissionId;
        try(Scanner fileReader = new Scanner(file.getInputStream())) {
            submissionId = fileReader.nextLine();
        }

        Submission submission = submissionService.getSubmissionBykey(submissionId);
        copyProperties(submission, submissionResponse);

        Boolean submissionExpired = submissionService.submissionExpired(submission, submissionTime);

        if(submissionExpired){
            submissionResponse.setMessage("This test set has expired - please request a new set");
            return submissionResponse;
        }


        Boolean linkedToTeam = submissionService.submissionLinkedToTeam(submission, principal.getName());
        if(!linkedToTeam){
            submissionResponse.setMessage("This test set is not linked to your team. this has been recorded");
            //TODO add log here of possibly malicious behaviour
            return submissionResponse;
        }

        HashMap<String, String> userSubmission = submissionService.getSubmittedTest(file);
        Boolean answersAreCorrect = submissionService.answersAreCorrect(submission, userSubmission);

        if(!answersAreCorrect){
            submissionResponse.setMessage("Your submitted answers are not correct");
            return submissionResponse;
        }

        Submission updatedSubmission = submissionService.updateSubmission(submission, submissionTime, true);
        copyProperties(updatedSubmission, submissionResponse);
        submissionResponse.setMessage("Congratulations - you have successfully completed this question");
        return submissionResponse;
    }

    private Submission initializeSubmissionForQuestion(String name, Team team, int setSize) {
        List<Test> questionTestSet = questionService.getQuestionTestSet(name, setSize);
        Submission submission = submissionService.startSubmissionForQuestion(questionService.getQuestion(name), questionTestSet);
        teamService.registerSubmission(team, submission);
        return submission;
    }
}
