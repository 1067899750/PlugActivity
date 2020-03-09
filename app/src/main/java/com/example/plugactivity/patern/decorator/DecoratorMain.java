package com.example.plugactivity.patern.decorator;

/**
 * @author puyantao
 * @describe  装饰者模式 https://blog.csdn.net/self_study/article/details/51591709
 * @create 2020/3/9 16:03
 */
public class DecoratorMain {
    public static void main(String[] argc) {
        int a = 1;
        switch (a) {
            case 1:
                IWindow horizontalWindow = new HorizontalScrollBarDecorator(new SimpleWindow());
                horizontalWindow.draw();
                System.out.println(horizontalWindow.getDescription());
                break;
            case 2:
                IWindow verticalWindow = new VerticalScrollBarDecorator(new SimpleWindow());
                verticalWindow.draw();
                System.out.println(verticalWindow.getDescription());
                break;
        }

    }

}















