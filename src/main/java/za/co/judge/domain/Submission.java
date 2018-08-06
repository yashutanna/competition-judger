package za.co.judge.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity
public class Submission extends BaseEntity {

    private Long requestTime;
    private Long expirationTime;
    private Long submissionTime;
    private Boolean successful;
    private String key;

    @Relationship(type = "FOR_QUESTION")
    private Question question;

    @Relationship(type = "TESTED_BY")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Test> testSet;

    public Submission() {
        this.requestTime = Date.from(Instant.now()).getTime();
        this.key = UUID.randomUUID().toString();
    }

    public void setQuestion(Question question){
        this.question = question;
        this.expirationTime = this.requestTime + (question.getTimeLimit() * 1000);
    }
}
