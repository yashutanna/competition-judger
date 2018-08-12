package za.co.judge.repositories;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import za.co.judge.domain.Question;
import za.co.judge.domain.Test;

import java.util.Collection;
import java.util.List;

public interface QuestionRepository extends Neo4jRepository<Question, Long> {
    @Query("MATCH (m:Question)-[r:TESTED_BY]->(a:Test) WHERE toLower(m.name) CONTAINS toLower({name}) RETURN a")
    Collection<Test> getTestsForQuestion(@Param("name") String name);

    @Query("MATCH (m:Question) WHERE toLower(m.name) CONTAINS toLower({name}) RETURN m")
    Question getQuestion(@Param("name") String name);

    @Query("MATCH (m:Question)-[r:TESTED_BY]->(a:Test) WHERE toLower(m.name) CONTAINS toLower({name}) WITH a, rand() AS number RETURN a ORDER BY number LIMIT {limit}")
    List<Test> getTestSet(@Param("name") String name, @Param("limit") int limit);

    @Query("MATCH (m:Question)<-[r:FOR_QUESTION]-(a:Submission) WHERE toLower(m.name) CONTAINS toLower({name}) and a.successful = true RETURN COUNT(a)")
    Integer countNumberOfSubmissionsForQuestion(@Param("name") String name);
}
