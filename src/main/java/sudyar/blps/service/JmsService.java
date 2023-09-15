package sudyar.blps.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import javax.jms.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import sudyar.blps.entity.Notice;
import sudyar.blps.etc.Note;

@Service
@RequiredArgsConstructor
public class JmsService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue queue;

    public void sendNotice (Notice notice){

        Note note = new Note(notice.getToUser(), notice.getFromUser(), notice.getDescription());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String noteAsJson = mapper.writeValueAsString(note);

            jmsTemplate.convertAndSend(queue, noteAsJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
