package za.co.judge.domain;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;

@Data
@NodeEntity
public class Member extends BaseEntity{

    private String name;
}
