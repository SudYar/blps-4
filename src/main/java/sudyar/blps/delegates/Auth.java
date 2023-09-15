package sudyar.blps.delegates;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import sudyar.blps.etc.AuthUser;
import sudyar.blps.service.AuthService;
import sudyar.blps.service.TokenService;

import javax.inject.Named;

@Component
@Named
@RequiredArgsConstructor
public class Auth implements JavaDelegate {
    private final AuthService authService;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        String userId = delegateExecution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getUserId();
        try {
            String login = String.valueOf(delegateExecution.getVariable("login"));
            String password = String.valueOf(delegateExecution.getVariable("password"));
            var result = authService.signIn(new AuthUser(login, password));
            delegateExecution.setVariable("result", result.getJwt() != null ? result.getJwt() : result.getErrorMessage());
            TokenService.putUserToken(userId, result.getJwt());
        } catch (Throwable throwable) {
            TokenService.putUserToken(userId, null);
            delegateExecution.setVariable("error", throwable.getMessage());
            throw new BpmnError("auth_error", throwable.getMessage());
        }
    }
}


