package sudyar.blps.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sudyar.blps.entity.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {


    List<Feedback> findAllByLoginExecutor(String login);
    List<Feedback> findAllByLoginEmployer(String login);
}
