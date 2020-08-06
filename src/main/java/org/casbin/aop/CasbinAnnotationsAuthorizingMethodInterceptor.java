package org.casbin.aop;

import org.apache.shiro.spring.aop.SpringAnnotationResolver;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;

import java.util.Arrays;

public class CasbinAnnotationsAuthorizingMethodInterceptor extends AopAllianceAnnotationsAuthorizingMethodInterceptor {
    public CasbinAnnotationsAuthorizingMethodInterceptor() {
        setMethodInterceptors(Arrays.asList(
                new CasbinAnnotationEnforceMethodInterceptor(new SpringAnnotationResolver()),
                new CasbinAnnotationHasRoleForUserMethodInterceptor(new SpringAnnotationResolver())
        ));
    }
}
