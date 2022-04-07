package com.seast.thread;

/**
 * @Author: limf
 * @Date: 2022/4/6 15:46
 * @Description:
 */
public class MyThread extends Thread {

    public volatile boolean running = true;

    @Override
    public void run() {
        System.out.println("getId() = " + getId());
        System.out.println("getName() = " + getName());
        System.out.println("getState() = " + getState());
        System.out.println("MyThread 执行run");
        int i = 1;
        while (running && !isInterrupted()) {
            i++;
            System.out.println("i = " + i);
        }
        System.out.println("end ");
    }
}
