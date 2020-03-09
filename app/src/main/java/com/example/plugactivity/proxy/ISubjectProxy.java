package com.example.plugactivity.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 13:54
 */
public abstract class ISubjectProxy implements InvocationHandler {
    protected Subject mSubject;

    public ISubjectProxy(Subject subject) {
        mSubject = subject;
    }
}
