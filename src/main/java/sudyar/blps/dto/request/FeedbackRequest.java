package sudyar.blps.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedbackRequest {

    private int idOrder;

    private int starsForFeedback;
}
