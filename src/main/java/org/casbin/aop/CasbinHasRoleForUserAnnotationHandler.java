package org.casbin.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.casbin.annotation.HasRoleForUser;
import org.casbin.subject.CasbinSubject;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

public class CasbinHasRoleForUserAnnotationHandler extends AuthorizingAnnotationHandler {
    public CasbinHasRoleForUserAnnotationHandler() {
        super(HasRoleForUser.class);
    }

    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof HasRoleForUser)) return;
        HasRoleForUser anno = (HasRoleForUser) a;
        CasbinSubject subject = (CasbinSubject) getSubject();
        HttpServletRequest request = (HttpServletRequest) subject.getServletRequest();
        if (!subject.getEnforcer().hasRoleForUser(String.valueOf(subject.getPrincipal()), anno.role())) {
            throw new UnauthorizedException("没有权限");
        }
    }
}
