package com.example.plugactivity.decorator;

import android.util.Log;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 16:02
 */
public class VerticalScrollBarDecorator extends WindowDecorator {
    public VerticalScrollBarDecorator(IWindow window) {
        super(window);
    }

    @Override
    public void draw() {
        super.draw();
        System.out.println("then drawing the vertical scroll bar");
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " with vertical scroll bar";
    }

}














