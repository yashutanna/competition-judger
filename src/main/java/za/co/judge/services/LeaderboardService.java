package za.co.judge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.judge.domain.TeamStanding;
import za.co.judge.repositories.TeamRepository;

import java.util.LinkedList;
import java.util.List;

@Service
public class LeaderboardService {
    @Autowired
    private TeamRepository teamRepository;

    public List<TeamStanding> getLeaderBoard(){
        List<TeamStanding> standings = new LinkedList<>();
        teamRepository.findAll(2).forEach(team -> {
            TeamStanding standing = new TeamStanding();
            standing.setUniversity(team.getUniversity());
            team.getSubmissions().stream()
                .filter(submission -> submission.getSuccessful() != null)
                .forEach(teamSubmission -> standing.getSubmissions().put(teamSubmission.getQuestion().getName(), teamSubmission.getSuccessful()));
            standings.add(standing);
        });
        return standings;
    }
}
