package org.casbin.aop;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

public class CasbinAnnotationHasRoleForUserMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {
    public CasbinAnnotationHasRoleForUserMethodInterceptor(AnnotationResolver resolver) {
        super(new CasbinHasRoleForUserAnnotationHandler(), resolver);
    }
}
