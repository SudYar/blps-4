package sudyar.blps.delegates.employer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sudyar.blps.dto.response.InfoResponse;
import sudyar.blps.entity.Ordering;
import sudyar.blps.security.DelegateAuthChecker;
import sudyar.blps.service.OrderService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeleteOrder implements JavaDelegate {

    private final DelegateAuthChecker delegateAuthChecker;
    private final OrderService orderService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try {
            String owner = delegateAuthChecker.checkEmployerAuth(delegateExecution);
            Integer idOrdering = Integer.valueOf(String.valueOf(delegateExecution.getVariable("idForDelete")));
            Optional<Ordering> order = orderService.getById(idOrdering);
            if (order.isPresent()) {
                Ordering ordering = order.get();
                String login = SecurityContextHolder.getContext().getAuthentication().getName();
                if (login.equals(ordering.getOwnerLogin())) {
                    orderService.deleteOrdering(idOrdering);
                } else throw new Throwable( "Вы не владелец данного заказа");
            } else throw new Throwable("Нет заказа с таким id");
        }catch (Throwable throwable) {
            delegateExecution.setVariable("result", throwable.getMessage());
            throw new BpmnError("result", throwable.getMessage());
        }
    }
}
