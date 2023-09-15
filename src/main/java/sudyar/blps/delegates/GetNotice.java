package sudyar.blps.delegates;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sudyar.blps.service.NoticeService;

@Component
@RequiredArgsConstructor
public class GetNotice implements JavaDelegate {

    private final NoticeService noticeService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try {
            String login = SecurityContextHolder.getContext().getAuthentication().getName();
            var notices = noticeService.getAll(login);
            delegateExecution.setVariable("notices", notices.toString());

        }catch (Throwable throwable) {
            delegateExecution.setVariable("error", throwable.getMessage());
            throw new BpmnError("get_notices_error", throwable.getMessage());
        }
    }
}
