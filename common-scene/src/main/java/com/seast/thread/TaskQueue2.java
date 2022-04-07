package com.seast.thread;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: limf
 * @Date: 2022/4/6 17:01
 * @Description:
 */
public class TaskQueue2 {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public Queue<String> queue = new LinkedList<>();

    public void addTask(String task) {
        lock.lock();
        try {
            this.queue.add(task);
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public String getTask() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                // 释放this锁
                condition.await();
                // 重新获取this锁
            }
            return queue.remove();
        }finally {
            lock.unlock();
        }
    }
}
