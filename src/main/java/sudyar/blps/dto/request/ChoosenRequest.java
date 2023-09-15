package sudyar.blps.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChoosenRequest {

    private int idOrder;

    private String executor;
}
