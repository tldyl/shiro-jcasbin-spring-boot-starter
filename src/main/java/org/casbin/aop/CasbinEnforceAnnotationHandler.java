package org.casbin.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.casbin.annotation.RequireCasbin;
import org.casbin.subject.CasbinSubject;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

public class CasbinEnforceAnnotationHandler extends AuthorizingAnnotationHandler {
    public CasbinEnforceAnnotationHandler() {
        super(RequireCasbin.class);
    }

    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof RequireCasbin)) return;
        CasbinSubject subject = (CasbinSubject) getSubject();
        HttpServletRequest request = (HttpServletRequest) subject.getServletRequest();
        String path = request.getServletPath();
        String method = request.getMethod();
        if (!subject.enforce(String.valueOf(subject.getPrincipal()), path, method)) {
            throw new UnauthorizedException("没有权限");
        }
    }
}
