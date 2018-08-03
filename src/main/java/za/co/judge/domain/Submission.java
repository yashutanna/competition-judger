package za.co.judge.domain;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@Data
@NodeEntity
public class Submission extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Relationship(type = "FOR_QUESTION")
    private Question question;

    @Relationship(type = "TESTED_BY")
    private List<Test> testSet;

    private Long requestTime;
    private Long submissionTime;

    private Boolean successful;

    @Relationship(type = "SUBMITTED", direction = Relationship.INCOMING)
    private Team submittedBy;
}
