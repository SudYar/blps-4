package sudyar.blps.delegates;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.AuthenticationException;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import sudyar.blps.entity.Role;
import sudyar.blps.etc.AuthUserWithRole;
import sudyar.blps.service.AuthService;
import sudyar.blps.service.TokenService;

@Component
@RequiredArgsConstructor
public class Registration implements JavaDelegate {

    private final AuthService authService;
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String userId = delegateExecution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getUserId();
        String email = String.valueOf(delegateExecution.getVariable("login"));
        String password = String.valueOf(delegateExecution.getVariable("password"));
        String role = String.valueOf(delegateExecution.getVariable("role"));
        var response = authService.signUp(new AuthUserWithRole(email,password, Role.valueOf(role)));
        if (HttpStatus.BAD_REQUEST != response.getStatusCode()){
            TokenService.putUserToken(userId, response.getBody().getJwt());
            delegateExecution.setVariable("result", response.getBody().getJwt());
        }
        else {
            TokenService.putUserToken(userId, null);
            delegateExecution.setVariable("error", response.getBody().getErrorMessage());
            throw new BpmnError("reg_error", response.getBody().getErrorMessage());
        }
    }
}
