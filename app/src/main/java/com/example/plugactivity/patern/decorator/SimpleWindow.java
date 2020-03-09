package com.example.plugactivity.patern.decorator;

/**
 * @author puyantao
 * @describe 装饰者模式
 * @create 2020/3/9 15:58
 */
public class SimpleWindow implements IWindow {
    @Override
    public void draw() {
        System.out.println("drawing a window");
    }

    @Override
    public String getDescription() {
        return "a window";
    }
}











