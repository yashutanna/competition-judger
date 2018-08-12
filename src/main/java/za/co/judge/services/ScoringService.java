package za.co.judge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.judge.repositories.QuestionRepository;

@Service
public class ScoringService {
    @Autowired
    private QuestionRepository questionRepository;
}
