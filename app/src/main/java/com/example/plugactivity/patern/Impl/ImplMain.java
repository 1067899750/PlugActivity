package com.example.plugactivity.patern.Impl;

/**
 * @author puyantao
 * @describe 桥接模式 https://blog.csdn.net/self_study/article/details/51622243
 * @create 2020/3/9 16:31
 */
public class ImplMain {
    public static void main(String[] argc){
        int a = 1;
        Car car = null;
        switch (a) {
            case 1:
                car = new SedanCar(new RainyTire());
                break;
            case 2:
                car = new SedanCar(new SandyTire());
                break;
            case 3:
                car = new RacingCar(new RainyTire());
                break;
            case 4:
                car = new RacingCar(new SandyTire());
                break;
        }
        car.run();
    }
}
