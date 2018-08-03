package za.co.judge.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    
    public String toString() {
        try {
            return Jackson2ObjectMapperBuilder.json().build().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
