package za.co.judge.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity
public class Admin extends BaseEntity {

    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
