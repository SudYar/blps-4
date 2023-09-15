package sudyar.blps.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderingRequest {

    private String address;

    private String description;

    private Integer price;

    public static String getInfo(){
        return "Поля: String address, String description, Int price";
    }
}
