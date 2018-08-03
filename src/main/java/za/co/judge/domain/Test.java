package za.co.judge.domain;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@Data
@NodeEntity
public class Test extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String input;
    private String output;
    @Relationship(type = "TESTED_BY", direction = Relationship.INCOMING)
    private Question question;
}
