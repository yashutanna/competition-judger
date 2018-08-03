package za.co.judge.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@Data
@NodeEntity
public class Question extends BaseEntity{

    private String name;
    private Integer timeLimit;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Relationship(type = "TESTED_BY")
    private List<Test> testSet;

}
