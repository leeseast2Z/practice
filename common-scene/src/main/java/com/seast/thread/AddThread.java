package com.seast.thread;

/**
 * @Author: limf
 * @Date: 2022/4/6 16:22
 * @Description:
 */
public class AddThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            synchronized (Counter.lock) {
                Counter.count += 1;
                if( i == 999) {
                    throw new RuntimeException();
                }
            }// 无论有无异常，都会在此释放锁
        }
    }
}
