package com.example.plugactivity.patern.Impl;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 16:29
 */
public abstract class Car {
    private ITire mITire;

    public Car(ITire ITire) {
        mITire = ITire;
    }

    public ITire getITire() {
        return mITire;
    }

    public abstract void run();
}



























