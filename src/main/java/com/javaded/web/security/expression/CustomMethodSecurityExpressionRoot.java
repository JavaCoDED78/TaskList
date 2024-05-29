package com.javaded.web.security.expression;

import com.javaded.domain.user.Role;
import com.javaded.service.UserService;
import com.javaded.web.security.JwtEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

@Setter
@Getter
public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;
    private HttpServletRequest request;

    private UserService userService;

    public CustomMethodSecurityExpressionRoot(
            final Authentication authentication
    ) {
        super(authentication);
    }

    public boolean canAccessUser(final Long id) {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        JwtEntity user = (JwtEntity) authentication.getPrincipal();
        Long userId = user.getId();
        return userId.equals(id) || hasAnyRole(authentication, Role.ROLE_ADMIN);
    }

    private boolean hasAnyRole(final Authentication authentication,
                               final Role... roles) {
        return Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .anyMatch(authentication.getAuthorities()::contains);
    }

    public boolean canAccessTask(final Long taskId) {
        Authentication authentication = SecurityContextHolder.
                getContext().getAuthentication();
        JwtEntity user = (JwtEntity) authentication.getPrincipal();
        Long id = user.getId();
        return userService.isTaskOwner(id, taskId);
    }

    @Override
    public Object getThis() {
        return target;
    }
}
