package za.co.judge.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEntity {
    public String toString() {
        try {
            return Jackson2ObjectMapperBuilder.json().build().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
