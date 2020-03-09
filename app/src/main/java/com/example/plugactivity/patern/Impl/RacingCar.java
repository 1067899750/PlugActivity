package com.example.plugactivity.patern.Impl;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 16:30
 */
public class RacingCar extends Car {
    public RacingCar(ITire ITire) {
        super(ITire);
    }

    @Override
    public void run() {
        System.out.println("racing car " + getITire().run());
    }
}
