package sudyar.blps.delegates.employer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sudyar.blps.dto.response.OrdersResponse;
import sudyar.blps.entity.Ordering;
import sudyar.blps.security.DelegateAuthChecker;
import sudyar.blps.service.OrderService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetOrders implements JavaDelegate {

    private final DelegateAuthChecker delegateAuthChecker;
    private final OrderService orderService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try {
            String owner = delegateAuthChecker.checkEmployerAuth(delegateExecution);
            OrdersResponse orders = orderService.getAllByOwner(owner);
            delegateExecution.setVariable("orders", orders.toString());

        }catch (Throwable throwable) {
            delegateExecution.setVariable("error", throwable.getMessage());
            throw new BpmnError("get_orders_error", throwable.getMessage());
        }
    }
}
