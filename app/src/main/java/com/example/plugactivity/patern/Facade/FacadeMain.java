package com.example.plugactivity.patern.Facade;

/**
 * @author puyantao
 * @describe 外观模式 https://blog.csdn.net/self_study/article/details/51931196
 * @create 2020/3/9 17:07
 */
public class FacadeMain {
    public static void main(String[] argc){
        IFacade facade = new Facade();
        facade.operationA();
        facade.operationB();
        facade.operationC();
    }
}
