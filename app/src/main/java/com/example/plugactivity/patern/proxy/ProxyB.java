package com.example.plugactivity.patern.proxy;

import java.lang.reflect.Method;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 13:58
 */
public class ProxyB extends ISubjectProxy {
    public ProxyB(Subject subject) {
        super(subject);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("operationA")){
            throw new UnsupportedOperationException("ProxyB can't invoke operationA");
        } else if (method.getName().equals("operationB")){
            return method.invoke(mSubject, args);
        }
        return null;
    }
}
