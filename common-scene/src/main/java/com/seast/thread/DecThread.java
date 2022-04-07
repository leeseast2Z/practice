package com.seast.thread;

/**
 * @Author: limf
 * @Date: 2022/4/6 16:25
 * @Description:
 */
public class DecThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            synchronized (Counter.lock) {
                Counter.count -= 1;
            }
        }
    }
}
