package za.co.judge.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity
public class Test extends BaseEntity {

    private String input;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String output;
    private String key;

    public Test(){
        this.key = UUID.randomUUID().toString();
    }

    @Relationship(type = "TESTED_BY", direction = Relationship.INCOMING)
    private Question question;
}
