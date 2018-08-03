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

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TeamService teamService;

    @PostMapping("/login")
    public ResponseEntity<Team> addQuestion(@RequestBody Team team) {
        String name = team.getName();
        String password = team.getPassword();
        String jwtToken = teamService.authenticateTeam(name, password);
        if(jwtToken == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.status(HttpStatus.OK).header("Set-Cookie", "bearer_token=" + jwtToken).build();
    }
}
