package com.example.plugactivity.patern.decorator;

/**
 * @author puyantao
 * @describe
 * @create 2020/3/9 16:01
 */
public class HorizontalScrollBarDecorator extends WindowDecorator {
    public HorizontalScrollBarDecorator(IWindow window) {
        super(window);
    }

    @Override
    public void draw() {
        super.draw();
        System.out.println("then drawing the horizontal scroll bar");
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " with horizontal scroll bar";
    }


}









