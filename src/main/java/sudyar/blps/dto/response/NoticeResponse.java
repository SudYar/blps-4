package sudyar.blps.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sudyar.blps.entity.Notice;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponse {
    private List<Notice> notices;
    @Override
    public String toString() {
        String str = "Notices:\n";
        for (int i = 0; i< notices.size(); i++){
            str += notices.get(i).toString() + "\n";
        }
        return str;
    }
}
