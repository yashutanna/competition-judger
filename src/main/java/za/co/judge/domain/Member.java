package za.co.judge.domain;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@Data
@NodeEntity
public class Member extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
