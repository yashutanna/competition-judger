package za.co.judge.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import za.co.judge.domain.Member;
import za.co.judge.repositories.TeamRepository;

import java.util.List;

@RestController
public class RootController {

    @Autowired
    TeamRepository teamRepository;

    @GetMapping("/standings")
    public String index() {
        return "Standing will come from here";
    }
    
}
