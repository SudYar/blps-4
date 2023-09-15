package sudyar.blps.service;

import bitronix.tm.BitronixTransactionManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sudyar.blps.dto.request.OrderingRequest;
import sudyar.blps.dto.response.OrdersResponse;
import sudyar.blps.entity.Notice;
import sudyar.blps.entity.Ordering;
import sudyar.blps.repo.NoticeRepository;
import sudyar.blps.repo.OrderRepository;

import javax.transaction.SystemException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final BitronixTransactionManager bitronixTransactionManager;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    public OrdersResponse getAll(){
        final var res = new OrdersResponse(orderRepository.findAll().stream().toList());
        return res;
    }

    public OrdersResponse getAllByOwner(String loginOwner){
        final var res = new OrdersResponse(orderRepository.findByOwnerLogin(loginOwner).stream().toList());
        return res;
    }

    public Optional<Ordering> getById(Integer idOrdering){
        return orderRepository.findById(idOrdering);
    }

    public boolean existById(int idOrdering){
        return orderRepository.existsById(idOrdering);
    }

    public void createOrdering(@NonNull OrderingRequest orderingRequest, String owner){
        final var newOrder = new Ordering();
        newOrder.setAddress(orderingRequest.getAddress());
        newOrder.setDescription(orderingRequest.getDescription());
        newOrder.setPrice(orderingRequest.getPrice());
        newOrder.setOwnerLogin(owner);
        orderRepository.save(newOrder);
    }

    public void deleteOrdering(int idOrdering){
        try {
            bitronixTransactionManager.begin();
            List<Notice> notices = noticeRepository.findByTargetOrdering(orderRepository.findById(idOrdering).get());
            noticeRepository.deleteAll(notices);
            orderRepository.deleteById(idOrdering);
            bitronixTransactionManager.commit();
        }catch (Exception e){

            try {
                bitronixTransactionManager.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    public int deleteAllOrdering(String loginOwner){
         try {
                bitronixTransactionManager.begin();
                final var res = orderRepository.findAll();
                List<Notice> notices = noticeRepository.findByFromUser(loginOwner);
                noticeRepository.deleteAll(notices);
                orderRepository.deleteAll(res);
                bitronixTransactionManager.commit();
                return res.size();
        }catch (Exception e){
             try {
                 bitronixTransactionManager.rollback();
             } catch (SystemException ex) {
                 throw new RuntimeException(ex);
             }
             return 0;
        }
    }

}
