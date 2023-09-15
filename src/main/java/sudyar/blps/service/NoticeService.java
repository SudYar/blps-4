package sudyar.blps.service;

import bitronix.tm.BitronixTransactionManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sudyar.blps.dto.request.MessageRequest;
import sudyar.blps.dto.response.NoticeResponse;
import sudyar.blps.entity.Notice;
import sudyar.blps.entity.Ordering;
import sudyar.blps.entity.Status;
import sudyar.blps.repo.NoticeRepository;
import sudyar.blps.repo.OrderRepository;

import javax.transaction.SystemException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {


    private final BitronixTransactionManager bitronixTransactionManager;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private JmsService jmsService;

    @Autowired
    private OrderRepository orderRepository;

    public NoticeResponse getAll(String login){
        final var res = noticeRepository.findByToUser(login);
        return new NoticeResponse(res);
    }

    public NoticeResponse getAllFromUser(String login){
        final var res = noticeRepository.findByFromUser(login).stream().toList();
        return new NoticeResponse(res);
    }

    public boolean existChosenOrdering(String toUser, String fromUser, Ordering targetOrdering, Status status){
        return noticeRepository.existsByToUserAndFromUserAndTargetOrderingAndStatus(toUser, fromUser, targetOrdering, status);
    }

    public boolean existByStatusAndOrdering(Ordering ordering, Status status){
        return noticeRepository.existsByTargetOrderingAndStatus(ordering, status);
    }

    public List<Notice> getByStatusAndOrdering(Ordering ordering, Status status){
        return noticeRepository.findByTargetOrderingAndStatus(ordering, status);
    }

    public void createNotice(@NonNull MessageRequest messageRequest, String fromUser, Status status){
        String login = messageRequest.getToUser();
        Notice notice = new Notice();
        notice.setToUser(login);
        notice.setFromUser(fromUser);
        notice.setDescription(messageRequest.getDescription());
        notice.setTargetOrdering(orderRepository.getReferenceById(messageRequest.getIdTargetOrdering()));
        notice.setStatus(status);
        jmsService.sendNotice(notice);
        noticeRepository.save(notice);
    }
    public void createNotice(String toUser, String fromUser, String description, Ordering ordering , Status status){
        Notice notice = new Notice();
        notice.setToUser(toUser);
        notice.setFromUser(fromUser);
        notice.setDescription(description);
        notice.setTargetOrdering(ordering);
        notice.setStatus(status);
        jmsService.sendNotice(notice);
        noticeRepository.save(notice);
    }

    public void deleteMessage(int idMessage){
        try{
            bitronixTransactionManager.begin();
            Optional<Notice> optionalNotice = noticeRepository.findById(idMessage);
            if (optionalNotice.isPresent()){
                Notice message = optionalNotice.get();
                if (Status.MESSAGE==message.getStatus())  noticeRepository.deleteById(idMessage);
            }
            bitronixTransactionManager.commit();
        } catch (Exception ex){
            try {
                bitronixTransactionManager.rollback();
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void deleteNotices(Ordering ordering, Status stat){
        try {
            bitronixTransactionManager.begin();
            List<Notice> notices =  noticeRepository.findByTargetOrderingAndStatus(ordering, stat);
            noticeRepository.deleteAll(notices);
            bitronixTransactionManager.commit();
        }catch (Exception ex){
            try {
                bitronixTransactionManager.rollback();
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
