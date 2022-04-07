package com.seast.thread;

import java.time.LocalTime;
import java.util.Locale;

/**
 * @Author: limf
 * @Date: 2022/4/6 16:15
 * @Description:
 */
public class TimeThread extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println("LocalTime.now() = " + LocalTime.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
