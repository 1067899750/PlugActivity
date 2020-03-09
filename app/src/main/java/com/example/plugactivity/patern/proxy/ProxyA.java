package com.example.plugactivity.patern.proxy;

import java.lang.reflect.Method;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 13:55
 */
public class ProxyA extends ISubjectProxy {
    public ProxyA(Subject subject) {
        super(subject);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("operationB")){
            throw new UnsupportedOperationException("ProxyA can't invoke operationB");
        } else if (method.getName().equals("operationA")){
           return method.invoke(mSubject, args);
        }
        return null;
    }
}











