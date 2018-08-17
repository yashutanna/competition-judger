package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.judge.domain.Member;
import za.co.judge.domain.Team;
import za.co.judge.services.TeamService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("/")
    public ResponseEntity<Team> addTeam(@RequestBody Team team, HttpServletRequest request) {
        return new ResponseEntity<>(teamService.save(team), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Team> getTeam(@PathVariable("name") String name) throws Exception {
        Optional<Team> possibleTeam = teamService.getTeam(name);
        return possibleTeam.map(team -> new ResponseEntity<>(team, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/{name}/members")
    public ResponseEntity<List<Member>> getTeamMembers(@PathVariable("name") String name){
        return new ResponseEntity<>((List<Member>) teamService.getTeamMembers(name), HttpStatus.OK);
    }
}
