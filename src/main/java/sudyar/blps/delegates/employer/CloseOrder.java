package sudyar.blps.delegates.employer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sudyar.blps.dto.response.InfoResponse;
import sudyar.blps.entity.Feedback;
import sudyar.blps.entity.Notice;
import sudyar.blps.entity.Ordering;
import sudyar.blps.entity.Status;
import sudyar.blps.security.DelegateAuthChecker;
import sudyar.blps.service.NoticeService;
import sudyar.blps.service.OrderService;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CloseOrder implements JavaDelegate {

    private final NoticeService noticeService;
    private final OrderService orderService;
    private final DelegateAuthChecker delegateAuthChecker;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try {
            String owner = delegateAuthChecker.checkEmployerAuth(delegateExecution);
            int idOrder = Integer.valueOf(String.valueOf(delegateExecution.getVariable("idForNext")));
            Optional<Ordering> optionalOrdering = orderService.getById(idOrder);
            if (optionalOrdering.isPresent()) {
                Ordering order = optionalOrdering.get();
                List<Notice> finishNotice =  noticeService.getByStatusAndOrdering(order, Status.FINISH);
                if (finishNotice.size() != 1) throw new Throwable("Зазаз ещё не закончен");
                else {
                    noticeService.deleteNotices(order, Status.FINISH);
                    noticeService.deleteNotices(order, Status.MESSAGE);
                    orderService.deleteOrdering(idOrder);
                    delegateExecution.setVariable("result","Поздравляю с выполненным заказом");
                }

            } else throw new Throwable("Заказа с id '" + idOrder + "' нет");
        }catch (Throwable throwable) {
            delegateExecution.setVariable("result", throwable.getMessage());
            throw new BpmnError("finish_error", throwable.getMessage());
        }
    }
}
