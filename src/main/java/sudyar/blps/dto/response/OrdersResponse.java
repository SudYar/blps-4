package sudyar.blps.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sudyar.blps.entity.Ordering;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersResponse {
    private List<Ordering> orders;

    @Override
    public String toString() {
        String str = "Orders:\n";
        for (int i = 0; i< orders.size(); i++){
            str += orders.get(i).toString() + "\n";
        }
        return str;
    }
}
