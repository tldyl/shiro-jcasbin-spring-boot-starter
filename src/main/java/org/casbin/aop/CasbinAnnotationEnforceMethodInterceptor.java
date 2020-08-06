package org.casbin.aop;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

@SuppressWarnings("WeakerAccess")
public class CasbinAnnotationEnforceMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {
    public CasbinAnnotationEnforceMethodInterceptor(AnnotationResolver resolver) {
        super(new CasbinEnforceAnnotationHandler(), resolver);
    }
}
