package za.co.judge.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubmissionResponse extends Submission {

    private String message;


    public SubmissionResponse(String message) {
        super();
        this.message = message;
    }

    public SubmissionResponse() {
        super();
    }
}
