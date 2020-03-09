package com.example.plugactivity.proxy;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 13:53
 */
public class RealSubject implements Subject {
    @Override
    public String operationA() {
        return "operationA";
    }

    @Override
    public String operationB() {
        return "operationB";
    }
}
