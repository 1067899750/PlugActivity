package com.example.plugactivity.patern.Impl;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 16:30
 */
public class SedanCar extends Car {
    public SedanCar(ITire ITire) {
        super(ITire);
    }

    @Override
    public void run() {
       System.out.println("sedan car " + getITire().run());
    }
}
