package za.co.judge.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@EqualsAndHashCode()
@Data
public class TeamStanding {

    private String university;
    HashMap<String, Boolean> submissions = new HashMap<>();
    private Integer score;
}
