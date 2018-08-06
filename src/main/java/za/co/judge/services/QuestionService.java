package za.co.judge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.judge.domain.Question;
import za.co.judge.domain.Submission;
import za.co.judge.domain.Test;
import za.co.judge.repositories.QuestionRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
        return new ArrayList<>(questionRepository.getTestSet(name, limit));
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public void getTestSetFileForSubmission(Submission submission, OutputStream os) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write(submission.getId().toString());
        bw.newLine();
        submission.getTestSet().forEach(test -> {
            try {
                String line = test.getKey() + "|" + test.getInput();
                bw.write(line);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.flush();
    }
}
