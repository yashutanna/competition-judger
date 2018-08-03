package za.co.judge.repositories;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import za.co.judge.domain.Member;
import za.co.judge.domain.Submission;
import za.co.judge.domain.Team;

import java.util.Collection;

public interface TeamRepository extends Neo4jRepository<Team, Long> {
    @Query("MATCH (m:Team)-[r:TEAM_MEMBER]->(a:Member) WHERE toLower(m.name) CONTAINS toLower({name}) RETURN a")
    Collection<Member> getTeamMembers(@Param("name") String name);

    @Query("MATCH (m:Team)-[r:SUBMITTED]->(a:Submission) WHERE toLower(m.name) CONTAINS toLower({name}) RETURN a")
    Collection<Submission> getSubmissionsForTeam(@Param("name") String name);

    @Query("MATCH (m:Team) WHERE toLower(m.name) CONTAINS toLower({name}) RETURN m")
    Team findTeamByName(@Param("name") String name);

    Team findTeamByNameAndPassword(String name, String password);
}
