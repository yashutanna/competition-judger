package za.co.judge.repositories;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import za.co.judge.domain.Submission;

import java.util.Collection;
import java.util.Optional;

public interface SubmissionRepository extends Neo4jRepository<Submission, Long> {
    @Query("MATCH (m:Team)-[r:SUBMITTED]->(a:Submission) WHERE toLower(m.name) CONTAINS toLower({teamName}) RETURN a")
    Collection<Submission> findByTeam(@Param("teamName") String teamName);
}
