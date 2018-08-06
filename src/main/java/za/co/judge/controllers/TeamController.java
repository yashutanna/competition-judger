package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.judge.domain.Member;
import za.co.judge.domain.Submission;
import za.co.judge.domain.Team;
import za.co.judge.services.SubmissionService;
import za.co.judge.services.TeamService;

import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;
    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/")
    public ResponseEntity<za.co.judge.domain.Team> addTeam(@RequestBody za.co.judge.domain.Team team) {
        return new ResponseEntity<>(teamService.save(team), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<za.co.judge.domain.Team> getTeam(@PathVariable("name") String name) {
        Optional<Team> possibleTeam = teamService.getTeam(name);
        return possibleTeam.map(team -> new ResponseEntity<>(team, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/{name}/members")
    public ResponseEntity<List<Member>> getTeamMembers(@PathVariable("name") String name){
        return new ResponseEntity<>((List<Member>) teamService.getTeamMembers(name), HttpStatus.OK);
    }

    @GetMapping("/{name}/submissions")
    public ResponseEntity<List<Optional<Submission>>> getTeamSubmissions(@PathVariable("name") String name){
        return new ResponseEntity<>(submissionService.getAttemptedSubmissionsForTeam(name), HttpStatus.OK);
    }

    @PostMapping(value = "/{name}/submissions")
    public Submission submit(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        Long submissionTime = Date.from(Instant.now()).getTime();
        return submissionService.checkSubmission(file, principal.getName(), submissionTime);
    }
    
}
