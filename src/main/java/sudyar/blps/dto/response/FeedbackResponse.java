package sudyar.blps.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sudyar.blps.entity.Feedback;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {
    List<Feedback> feedbacks;
}
