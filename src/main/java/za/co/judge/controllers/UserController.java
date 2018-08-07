package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.judge.domain.Team;
import za.co.judge.services.AdminService;
import za.co.judge.services.TeamService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TeamService teamService;
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<Team> login(@RequestBody Team team) {
        String name = team.getName();
        String password = team.getPassword();
        String teamJwtToken = teamService.authenticateTeam(name, password);
        String adminJwtToken = adminService.authenticate(name, password);
        if(teamJwtToken == null && adminJwtToken == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(teamJwtToken == null){
            return ResponseEntity.status(HttpStatus.OK).header("Set-Cookie", "bearer_token=" + adminJwtToken).build();
        }
        return ResponseEntity.status(HttpStatus.OK).header("Set-Cookie", "bearer_token=" + teamJwtToken).build();
    }
}
