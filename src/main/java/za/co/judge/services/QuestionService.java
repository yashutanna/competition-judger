package za.co.judge.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
import java.util.HashMap;
import java.util.List;

@Service
public class QuestionService {
    private QuestionRepository questionRepository;

    private HashMap<String, String> testSetSources;

    private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
        testSetSources = new HashMap<>();
        testSetSources.put("NaiveCommitments-part1", "http://52.157.232.213:3000/generate/part1");
        testSetSources.put("NaiveCommitments-part2", "http://52.157.232.213:3000/generate/part2");
    }

    public List<Question> getAllQuestions() {
        return (List<Question>) questionRepository.findAll(1);
    }

    public Question getQuestion(String name) {
        return questionRepository.getQuestion(name);
    }

    public List<Test> getQuestionTestSet(String name, int limit) {
        if(testSetSources.containsKey(name)){
            try {
                HttpResponse<String> response = Unirest.get(testSetSources.get(name))
                        .asString();
                Question question = objectMapper.readValue(response.getBody(), Question.class);
                Question dbQuestion = questionRepository.getQuestion(name);
                dbQuestion.getTestSet().addAll(question.getTestSet());
                Question savedQuestion = questionRepository.save(dbQuestion);
                return savedQuestion.getTestSet();
            } catch (UnirestException | IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(questionRepository.getTestSet(name, limit));
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public void getTestSetFileForSubmission(Submission submission, OutputStream os) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write(submission.getId().toString());
        readFromBufferedWriter(submission.getTestSet(), bw);
    }
    public void getTestSetFileForSubmission(List<Test> testSet, OutputStream os) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write("-999");
        readFromBufferedWriter(testSet, bw);
    }

    private void readFromBufferedWriter(List<Test> testSet, BufferedWriter bw) throws IOException {
        bw.newLine();
        testSet.forEach(test -> {
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

    public Boolean teamHasAlreadySuccessfullyAnsweredQuestion(String questionName, String teamName){
        return questionRepository.correctSubmissionExistsForQuestionByTeam(questionName, teamName) > 0;
    }
    public Integer countNumberOfSubmissionsForQuestion(String questionName){
        return questionRepository.countNumberOfSubmissionsForQuestion(questionName);
    }
}
