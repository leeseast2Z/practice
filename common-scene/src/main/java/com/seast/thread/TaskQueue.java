package com.seast.thread;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @Author: limf
 * @Date: 2022/4/6 17:01
 * @Description:
 */
public class TaskQueue {
    public Queue<String> queue = new LinkedList<>();

    public synchronized void addTask(String task) {
        this.queue.add(task);
        this.notifyAll();  // 唤醒this锁的等待线程
    }

    public synchronized String getTask() throws InterruptedException {
        while (queue.isEmpty()) {
            // 释放this锁
            this.wait();
            // 重新获取this锁
        }
        return queue.remove();
    }
}
