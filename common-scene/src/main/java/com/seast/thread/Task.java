package com.seast.thread;

import java.time.LocalTime;

/**
 * @Author: limf
 * @Date: 2022/4/6 17:56
 * @Description:
 */
public class Task implements Runnable{
    private String taskName;

    public Task(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println("taskName = " + taskName + " start" + LocalTime.now());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("taskName = " + taskName + " end" + LocalTime.now());
    }
}
