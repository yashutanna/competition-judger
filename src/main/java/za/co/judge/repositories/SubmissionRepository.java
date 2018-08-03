package za.co.judge.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import za.co.judge.domain.Submission;

public interface SubmissionRepository extends Neo4jRepository<Submission, Long> {
}
