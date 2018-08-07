package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import za.co.judge.domain.Submission;
import za.co.judge.services.SubmissionService;

import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.time.Instant;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @PostMapping(value = "")
    public Submission submit(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        Long submissionTime = Date.from(Instant.now()).getTime();
        return submissionService.checkSubmission(file, principal.getName(), submissionTime);
    }
    
}
