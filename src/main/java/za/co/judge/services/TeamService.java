package za.co.judge.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import za.co.judge.domain.Member;
import za.co.judge.domain.Submission;
import za.co.judge.domain.Team;
import za.co.judge.repositories.TeamRepository;

import java.security.Key;
import java.util.Collection;
import java.util.Optional;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;
    @Value("${signingKey}")
    private String signingKey;

    public Optional<Team> getTeam(String name) {
        Long teamId = teamRepository.findTeamByName(name);
        return teamRepository.findById(teamId, 2);
    }

    public Collection<Member> getTeamMembers(String name) {
        return teamRepository.getTeamMembers(name);
    }

    public Team save(Team team) {
        return teamRepository.save(team);
    }

    public String authenticateTeam(String name, String password) {

        Team resolvedTeam = teamRepository.findTeamByNameAndPassword(name, password);
        if(resolvedTeam == null){
            return null;
        }
        Key key = Keys.hmacShaKeyFor(signingKey.getBytes());
        String jwt = Jwts.builder().setSubject(resolvedTeam.getName()).signWith(key).compact();
        assert Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody().getSubject().equals(resolvedTeam.getName());
        return jwt;
    }

    public Team registerSubmission(Team team, Submission submission) {
        team.getSubmissions().add(submission);
        return teamRepository.save(team);
    }
}
