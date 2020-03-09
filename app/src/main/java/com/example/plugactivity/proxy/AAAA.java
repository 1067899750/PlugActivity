package com.example.plugactivity.proxy;

import java.lang.reflect.Proxy;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 13:59
 */
public class AAAA {

    public static void main(String[] argc) {
        int a = 1;
        Subject subject = new RealSubject();
        ISubjectProxy proxy = null;
        switch (a) {
            case 1:
                proxy = new ProxyA(subject);
                break;
            case 2:
                proxy = new ProxyB(subject);
                break;
        }

        Subject sub = (Subject) Proxy.newProxyInstance(subject.getClass().getClassLoader(), subject.getClass().getInterfaces(), proxy);

        try {
            System.out.println(sub.operationA());
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
        try {
            System.out.println(sub.operationB());
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }

    }
}















