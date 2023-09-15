package sudyar.blps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sudyar.blps.dto.response.FeedbackResponse;
import sudyar.blps.entity.Feedback;
import sudyar.blps.repo.FeedbackRepository;

@Service
public class FeedbackService {

    @Autowired
    FeedbackRepository feedbackRepository;

    public FeedbackResponse getAllForExecutor(String login){
        return new FeedbackResponse(feedbackRepository.findAllByLoginExecutor(login));
    }
    public FeedbackResponse getAllForEmployer(String login){
        return new FeedbackResponse(feedbackRepository.findAllByLoginEmployer(login));
    }

    public void add(Feedback feedback){
        feedbackRepository.save(feedback);
    }

}
