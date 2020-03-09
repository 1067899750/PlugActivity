package com.example.plugactivity.patern.Facade;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 17:06
 */
public class Facade implements IFacade {
    private SystemA systemA = new SystemA();
    private SystemB systemB = new SystemB();
    private SystemC systemC = new SystemC();

    @Override
    public void operationA() {
        systemA.operation1();
        systemB.operation2();
        systemC.operation3();
    }

    @Override
    public void operationB() {
        systemA.operation2();
        systemB.operation1();
        systemC.operation3();
    }

    @Override
    public void operationC() {
        systemC.operation1();
        systemB.operation2();
        systemA.operation3();
    }

}
