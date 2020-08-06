package org.casbin.subject;

import lombok.Getter;
import lombok.NonNull;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.casbin.jcasbin.main.Enforcer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.List;

public class CasbinSubject extends WebDelegatingSubject {
    @Getter
    private final Enforcer enforcer;

    public CasbinSubject(PrincipalCollection principals, boolean authenticated, String host, Session session,
                         ServletRequest request, ServletResponse response, SecurityManager securityManager, Enforcer enforcer) {
        super(principals, authenticated, host, session, request, response, securityManager);
        this.enforcer = enforcer;
    }

    public boolean enforce(@NonNull Object... rvals) throws AuthorizationException {
        assertAuthzCheckPossible();
        return enforcer.enforce(rvals);
    }

    @Override
    public boolean hasRole(String roleIdentifier) {
        return hasPrincipals() && enforcer.hasRoleForUser(principals.toString(), roleIdentifier);
    }

    public List<List<String>> getPermissionsForUser() {
        return hasPrincipals() ? enforcer.getPermissionsForUser(principals.toString()) : null;
    }

    public List<String> getRolesForUser() {
        return hasPrincipals() ? enforcer.getRolesForUser(principals.toString()) : null;
    }
}
