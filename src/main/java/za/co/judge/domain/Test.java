package za.co.judge.domain;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.UUID;

@Data
@NodeEntity
public class Test extends BaseEntity {

    private String input;
    private String output;
    private String key;

    public Test(){
        this.key = UUID.randomUUID().toString();
    }

    @Relationship(type = "TESTED_BY", direction = Relationship.INCOMING)
    private Question question;
}
