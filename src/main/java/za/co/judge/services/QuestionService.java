package za.co.judge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.judge.domain.Question;
import za.co.judge.domain.Test;
import za.co.judge.repositories.QuestionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    public List<Test> getTestSetForQuestion(String questionName){
        return (List<Test>) questionRepository.getTestsForQuestion(questionName);
    }

    public List<Question> getAllQuestions() {
        return (List<Question>) questionRepository.findAll(1);
    }

    public Question getQuestion(String name) {
        return questionRepository.getQuestion(name);
    }

    public List<Test> getQuestionTestSet(String name, int limit) {
        return questionRepository.getTestSet(name, limit).stream().peek(test -> test.setOutput(null)).collect(Collectors.toList());
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }
}
