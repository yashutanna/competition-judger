package za.co.judge.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import za.co.judge.domain.Admin;

public interface AdminRepository extends Neo4jRepository<Admin, Long> {
    Admin findByNameAndPassword(String name, String password);
}
