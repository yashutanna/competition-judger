package za.co.judge.domain;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.LinkedList;
import java.util.List;

@Data
@NodeEntity
public class Team extends BaseEntity {

    private String name;
    private String university;
    private String password;

    @Relationship(type = "SUBMITTED")
    private List<Submission> submissions = new LinkedList<>();

    @Relationship(type = "TEAM_MEMBER")
    private List<Member> teamMembers = new LinkedList<>();
}
