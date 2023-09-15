package sudyar.blps.delegates.executor;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import sudyar.blps.entity.Ordering;
import sudyar.blps.entity.Status;
import sudyar.blps.security.DelegateAuthChecker;
import sudyar.blps.service.NoticeService;
import sudyar.blps.service.OrderService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FinishOrder implements JavaDelegate {

    private final NoticeService noticeService;
    private final OrderService orderService;
    private final DelegateAuthChecker delegateAuthChecker;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try {
            String executor = delegateAuthChecker.checkExecutorAuth(delegateExecution);
            int idOrder = Integer.valueOf(String.valueOf(delegateExecution.getVariable("idForNext")));
            Optional<Ordering> optionalOrdering = orderService.getById(idOrder);
            if (optionalOrdering.isPresent()) {
                Ordering order = optionalOrdering.get();
                if (noticeService.existChosenOrdering(executor, order.getOwnerLogin(), order, Status.IN_WORK)) {
                    noticeService.deleteNotices(order, Status.IN_WORK);
                    noticeService.createNotice(order.getOwnerLogin(), executor, "Заказ выполнен", order, Status.FINISH);
                } else throw new Throwable("Данный заказ ещё не взят в работу");

            } else throw new Throwable("Заказа с id '" + idOrder + "' нет");
        }catch (Throwable throwable) {
            delegateExecution.setVariable("result", throwable.getMessage());
            throw new BpmnError("finish_error", throwable.getMessage());
        }
    }
}
