package sudyar.blps.delegates.employer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sudyar.blps.dto.response.InfoResponse;
import sudyar.blps.entity.Ordering;
import sudyar.blps.entity.Status;
import sudyar.blps.security.DelegateAuthChecker;
import sudyar.blps.service.NoticeService;
import sudyar.blps.service.OrderService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChooseExecutor implements JavaDelegate {

    private final DelegateAuthChecker delegateAuthChecker;
    private final NoticeService noticeService;
    private final OrderService orderService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try {
            String employer = delegateAuthChecker.checkEmployerAuth(delegateExecution);
            int idOrder = Integer.valueOf(String.valueOf(delegateExecution.getVariable("idForNext")));
            String executor = String.valueOf(delegateExecution.getVariable("loginExecutor"));
            Optional<Ordering> optionalOrdering = orderService.getById(idOrder);
            if (optionalOrdering.isPresent()) {
                Ordering order = optionalOrdering.get();
                if (noticeService.existChosenOrdering(employer, executor, order, Status.READY)) {
                    noticeService.deleteNotices(order, Status.READY);
                    noticeService.deleteNotices(order, Status.READY2);
                    noticeService.deleteNotices(order, Status.READY3);
                    noticeService.deleteNotices(order, Status.START2);
                    noticeService.deleteNotices(order, Status.START3);
                    noticeService.createNotice(executor, employer, "Можете приступать к работе", order, Status.IN_WORK);
                    delegateExecution.setVariable("result","Ответ исполнителю отправлен");
                } else throw new Throwable("Исполнитель не брался за этот заказ");

            } else throw new Throwable("Нет заказа с таким id");
        }catch (Throwable throwable) {
            delegateExecution.setVariable("result", throwable.getMessage());
            throw new BpmnError("choose_error", throwable.getMessage());
        }
    }
}
