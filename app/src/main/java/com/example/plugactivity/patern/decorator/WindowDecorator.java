package com.example.plugactivity.patern.decorator;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 15:59
 */
public abstract class WindowDecorator implements IWindow {
    private IWindow window;

    public WindowDecorator(IWindow window) {
        this.window = window;
    }

    @Override
    public void draw() {
        window.draw();
    }


    @Override
    public String getDescription() {
        return window.getDescription();
    }
}









