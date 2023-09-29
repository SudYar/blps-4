package sudyar.blps.security;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sudyar.blps.entity.Role;
import sudyar.blps.service.TokenService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DelegateAuthChecker {
    private final JwtAuthenticationFilter jwtTokenFilter;
    private final JwtTokenProvider jwtTokenProvider;

    public String checkExecutorAuth(DelegateExecution delegateExecution) throws IllegalAccessException {
        return checkRoleAuth(delegateExecution, List.of(Role.EXECUTOR.name()));
    }

    public String checkEmployerAuth(DelegateExecution delegateExecution) throws IllegalAccessException {
        return checkRoleAuth(delegateExecution, List.of(Role.EMPLOYER.name()));
    }

    private String checkRoleAuth(DelegateExecution delegateExecution, List<String> roles) throws IllegalAccessException {
        String userId = delegateExecution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getUserId();
        String token = TokenService.getUserToken(userId);
        if (token == null)
            throw new IllegalAccessException("Попробуйте авторизоваться еще раз. С вашим токеном что-то не так.");
        try {
            jwtTokenFilter.doFilter(token);
        }catch (Exception e){
            TokenService.putUserToken(userId, null);
            throw new IllegalAccessException("Попробуйте авторизоваться еще раз. С вашим токеном что-то не так.");
        }

        List<String> userRoles = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        if (Collections.disjoint(userRoles, roles))
            throw new IllegalAccessException("У вас нет прав на этот процесс");
        return jwtTokenProvider.getUserLoginFromToken(token.substring(7));
    }
}
