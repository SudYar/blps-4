package sudyar.blps.delegates.executor;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import sudyar.blps.dto.response.OrdersResponse;
import sudyar.blps.security.DelegateAuthChecker;
import sudyar.blps.service.OrderService;

@Component
@RequiredArgsConstructor
public class GetAllOrders implements JavaDelegate {

    private final DelegateAuthChecker delegateAuthChecker;
    private final OrderService orderService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try {
            String executor = delegateAuthChecker.checkExecutorAuth(delegateExecution);
            OrdersResponse orders = orderService.getAll();
            delegateExecution.setVariable("orders", orders.toString());

        }catch (Throwable throwable) {
            delegateExecution.setVariable("error", throwable.getMessage());
            throw new BpmnError("get_orders_error", throwable.getMessage());
        }
    }
}
