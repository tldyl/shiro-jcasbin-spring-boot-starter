package org.casbin.advisor;

import org.casbin.annotation.RequireCasbin;
import org.casbin.aop.CasbinAnnotationsAuthorizingMethodInterceptor;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class CasbinAdvisor extends StaticMethodMatcherPointcutAdvisor {
    public CasbinAdvisor() {
        setAdvice(new CasbinAnnotationsAuthorizingMethodInterceptor());
    }

    @Override
    public boolean matches(@NonNull Method method, Class<?> targetClass) {
        Method m = method;

        if (hasAnnotated(m)) { //这个注解有没有注在方法上
            return true; //如果返回true表示这个方法里含有你想要处理的注解
        }

        try { //这个注解有没有注在方法的参数上
            m = targetClass.getMethod(m.getName(), m.getParameterTypes());
            return hasAnnotated(m) || hasAnnotated(targetClass);
        } catch (NoSuchMethodException ignored) {
        }
        return false; //如果返回false表示这个方法没有被注解或没有含有你想要处理的注解
    }

    private <T extends AnnotatedElement> boolean hasAnnotated(T m) {
        return AnnotationUtils.findAnnotation(m, RequireCasbin.class) != null;
    }
}
