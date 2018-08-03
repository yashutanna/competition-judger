package za.co.judge.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity
public class Team extends BaseEntity {

    private String name;
    private String university;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Relationship(type = "SUBMITTED")
    private List<Submission> submissions = new LinkedList<>();

    @Relationship(type = "TEAM_MEMBER")
    private List<Member> teamMembers = new LinkedList<>();
}
