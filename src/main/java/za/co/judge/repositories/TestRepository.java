package za.co.judge.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import za.co.judge.domain.Test;

import java.util.Optional;

public interface TestRepository extends Neo4jRepository<Test, Long> {
    Optional<Test> getByKey(String key);
}
