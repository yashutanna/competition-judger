package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.judge.domain.Member;
import za.co.judge.domain.Question;
import za.co.judge.domain.Submission;
import za.co.judge.domain.Test;
import za.co.judge.services.QuestionService;
import za.co.judge.services.SubmissionService;
import za.co.judge.services.TeamService;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/")
    public ResponseEntity<Question> addQuestion(@RequestBody Question question) {
        return new ResponseEntity<>(questionService.save(question), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return new ResponseEntity<>(questionService.getAllQuestions(), HttpStatus.OK);
    }

    @GetMapping("/{name}/small-set")
    public ResponseEntity<Submission> getSmallTestSet(@PathVariable("name") String name){
        List<Test> questionTestSet = questionService.getQuestionTestSet(name, 5);
        Submission submission = submissionService.startSubmissionForQuestion(questionService.getQuestion(name), questionTestSet);
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }

    @GetMapping("/{name}/large-set")
    public ResponseEntity<List<Test>> getLargeTestSet(@PathVariable("name") String name){
        return new ResponseEntity<>(questionService.getQuestionTestSet(name, 10), HttpStatus.OK);
    }
}
