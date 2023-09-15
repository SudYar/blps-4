package sudyar.blps.delegates.executor;

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
public class ChooseOrder implements JavaDelegate {

    private final NoticeService noticeService;
    private final OrderService orderService;
    private final DelegateAuthChecker delegateAuthChecker;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String executor = delegateAuthChecker.checkExecutorAuth(delegateExecution);
        Integer idOrdering = Integer.valueOf(String.valueOf(delegateExecution.getVariable("idForChoose")));
        Optional<Ordering> optionalOrdering = orderService.getById(idOrdering);
        try {
            if (optionalOrdering.isPresent()) {
                Ordering order = optionalOrdering.get();
                if (noticeService.existByStatusAndOrdering(order, Status.IN_WORK))
                    throw  new  Throwable("Данный заказ уже кто-то взял");

                String MESSAGE_READY = "Я готов взяться за заказ";
                String response;
                // Костыль с 3 раундами участия в конкурсе
                if (!noticeService.existChosenOrdering(order.getOwnerLogin(), executor, order, Status.READY)) { // Если ещё не отправлял запрос, что готов выполнять, то отправляем
                    noticeService.createNotice(order.getOwnerLogin(), executor, MESSAGE_READY, order, Status.READY);
                    response = "Вы взялись за заказ: "+ order;
                } else if (!noticeService.existChosenOrdering(executor, order.getOwnerLogin(), order, Status.START2)) { //Запрос Ready уже был. Если не было старта второго раунда, то пока что ждем
                    throw  new  Throwable("Вы уже и так взялись за заказ: " + order);
                } else if (!noticeService.existChosenOrdering(order.getOwnerLogin(), executor, order, Status.READY2)) { // Если ещё не отправлял запрос, что готов выполнять, то отправляем
                    noticeService.createNotice(order.getOwnerLogin(), executor, MESSAGE_READY, order, Status.READY2);
                    response = "Вы взялись за заказ: "+ order;
                } else if (!noticeService.existChosenOrdering(executor, order.getOwnerLogin(), order, Status.START3)) { //Запрос Ready уже был. Если не было старта второго раунда, то пока что ждем
                    throw  new  Throwable("Вы уже и так взялись за заказ: " + order);
                } else if (!noticeService.existChosenOrdering(order.getOwnerLogin(), executor, order, Status.READY3)) { // Если ещё не отправлял запрос, что готов выполнять, то отправляем
                    noticeService.createNotice(order.getOwnerLogin(), executor, MESSAGE_READY, order, Status.READY3);
                    response = "Вы взялись за заказ: "+ order;
                } else //Запрос Ready уже был. Если не было старта второго раунда, то пока что ждем
                    throw  new  Throwable("Вы уже и так взялись за заказ: " + order);
                delegateExecution.setVariable("result", response);
            } else throw  new  Throwable("Заказа с id '" + idOrdering + "' нет");
        } catch (Throwable throwable){
            delegateExecution.setVariable("result", throwable.getMessage());
            throw new BpmnError("choose_error", throwable.getMessage());
        }
    }
}
