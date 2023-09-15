package sudyar.blps.delegates.employer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sudyar.blps.dto.response.InfoResponse;
import sudyar.blps.entity.Notice;
import sudyar.blps.entity.Ordering;
import sudyar.blps.entity.Status;
import sudyar.blps.security.DelegateAuthChecker;
import sudyar.blps.service.NoticeService;
import sudyar.blps.service.OrderService;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StartNewRound implements JavaDelegate {

    private final DelegateAuthChecker delegateAuthChecker;
    private final NoticeService noticeService;
    private final OrderService orderService;
    final private Integer DIFF_TIME = 1000000;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try{
            String employer = delegateAuthChecker.checkEmployerAuth(delegateExecution);
            int idOrder = Integer.valueOf(String.valueOf(delegateExecution.getVariable("idForNext")));
            Optional<Ordering> optionalOrdering = orderService.getById(idOrder);
            if (optionalOrdering.isPresent()) {
                Ordering order = optionalOrdering.get();
                if (noticeService.existByStatusAndOrdering(order, Status.START3)) {
                    throw new Throwable("Вы не можете пойти на 4 круг. Необходимо выбрать исполнителя");
                } else if (noticeService.existByStatusAndOrdering(order, Status.START2)) {
                    List<Notice> notices = noticeService.getByStatusAndOrdering(order, Status.READY2);
                    for (Iterator<Notice> iter = notices.iterator(); iter.hasNext(); ) {
                        Notice element = iter.next();
                        Date ldt = new Date(element.getCreatedDate().getTime() + DIFF_TIME);
                        Date ldtNow = new Date();
                        if (ldt.after(ldtNow)) iter.remove();
                    }
                    if (notices.size() < 2)
                        throw new Throwable("Не хватает ответивших вовремя исполнителей (должно быть как минимум два)\nОтвет: " + notices);
                    else {
                        for (Iterator<Notice> iter = notices.iterator(); iter.hasNext(); ) {
                            Notice element = iter.next();
                            noticeService.createNotice(element.getFromUser(), employer, "Появились новые условия, так как несколько исполниетелей", order, Status.START3);
                        }
                        delegateExecution.setVariable("result","Начался следующий раунд\nПредыдущие ответы: " + notices);
                    }
                } else {
                    List<Notice> notices = noticeService.getByStatusAndOrdering(order, Status.READY);
                    for (Iterator<Notice> iter = notices.iterator(); iter.hasNext(); ) {
                        Notice element = iter.next();
                        Date ldt = new Date(element.getCreatedDate().getTime() + DIFF_TIME);
                        Date ldtNow = new Date();
                        if (ldt.after(ldtNow)) iter.remove();
                    }
                    if (notices.size() < 2)
                        throw new Throwable("Не хватает ответивших вовремя исполнителей (должно быть как минимум два)\nОтвет: " + notices);
                    else {
                        for (Iterator<Notice> iter = notices.iterator(); iter.hasNext(); ) {
                            Notice element = iter.next();
                            noticeService.createNotice(element.getFromUser(), employer, "Появились новые условия, так как несколько исполниетелей", order, Status.START2);
                        }

                        delegateExecution.setVariable("result","Начался следующий раунд\nПредыдущие ответы: " + notices);
                    }
                }
            }else throw new Throwable("Нет заказа с таким id");
        }catch (Throwable throwable) {
            delegateExecution.setVariable("result", throwable.getMessage());
            throw new BpmnError("new_round_error", throwable.getMessage());
        }

    }
}
