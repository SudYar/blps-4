package sudyar.blps.delegates.employer;

import org.springframework.security.core.context.SecurityContextHolder;
import sudyar.blps.dto.request.OrderingRequest;
import sudyar.blps.security.DelegateAuthChecker;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import sudyar.blps.service.OrderService;

import javax.inject.Named;

import static org.camunda.spin.Spin.JSON;

@Component
@Named
@RequiredArgsConstructor
public class CreateOrder implements JavaDelegate {

    private final DelegateAuthChecker delegateAuthChecker;
    private final String SUCCESS_OUT = "Ваш заказ сохранен";
    private final OrderService orderService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try {
            String owner = delegateAuthChecker.checkEmployerAuth(delegateExecution);
            String address = String.valueOf(delegateExecution.getVariable("orderAdress"));
            String description = String.valueOf(delegateExecution.getVariable("orderDescription"));
            Integer price = Integer.valueOf(String.valueOf(delegateExecution.getVariable("orderPrice")));
            orderService.createOrdering(new OrderingRequest(address, description, price), owner);
            delegateExecution.setVariable("result", SUCCESS_OUT);
        } catch (Throwable throwable) {
            delegateExecution.setVariable("error", throwable.getMessage());
            throw new BpmnError("error", throwable.getMessage());
        }
    }
}
