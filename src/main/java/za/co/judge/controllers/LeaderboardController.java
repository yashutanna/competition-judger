package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.judge.domain.TeamStanding;
import za.co.judge.services.LeaderboardService;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<TeamStanding> getLeaderBoard(){
        return leaderboardService.getLeaderBoard();
    }
}
