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

    @GetMapping("/{teamName}/small-set")
    public ResponseEntity<Submission> getSmallTestSet(@PathVariable("teamName") String teamName, Principal principal){
        Optional<Team> team = teamService.getTeam(principal.getName());
        return team.map(team1 -> new ResponseEntity<>(initializeSubmissionForQuestion(teamName, team1, 5), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping("/{teamName}/large-set")
    public ResponseEntity<Submission> getLargeTestSet(@PathVariable("teamName") String teamName, Principal principal){
        Optional<Team> team = teamService.getTeam(principal.getName());
        return team.map(team1 -> new ResponseEntity<>(initializeSubmissionForQuestion(teamName, team1, 10), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    private Submission initializeSubmissionForQuestion(String name, Team team, int setSize) {
        List<Test> questionTestSet = questionService.getQuestionTestSet(name, setSize);
        Submission submission = submissionService.startSubmissionForQuestion(questionService.getQuestion(name), questionTestSet);
        teamService.registerSubmission(team, submission);
        return submission;
    }
}
