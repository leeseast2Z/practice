package com.seast.thread;

import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: limf
 * @Date: 2022/4/6 15:45
 * @Description:
 */
public class ThreadTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start");
        // p1();
        // p2();
        // p3();
        // p4();
        // ps5();
        // scheduledThreadPool();
        // threadLocal();
        // t1();
        t2();
        System.out.println("main end");
    }

    public static void p1() throws InterruptedException{
        MyThread t1 = new MyThread();
        MyThread t2 = new MyThread();
        t1.start();
        t2.start();
        Thread.sleep(1);
        t1.running = false;
        Thread.sleep(2000);
        t2.interrupt();
        // t1.start();

    }

    public static void p2() throws InterruptedException {
        TimeThread t1 = new TimeThread();
        t1.setDaemon(true);     // 守护线程，主线程执行完后不关心守护线程是否执行结束
        t1.start();
        t1.join(2000);  // 最长等待时间，毫秒
    }

    public static void p3() throws InterruptedException {
        AddThread addThread = new AddThread();
        DecThread decThread = new DecThread();
        decThread.start();
        addThread.start();
        addThread.join();
        decThread.join();
        System.out.println("Counter.count = " + Counter.count);
    }

    /**
     * 多线程协调运行，条件不满足时等待，满足时唤醒
     * wait、notify、notifyAll方法
     * @throws InterruptedException
     */
    public static void p4() throws InterruptedException {
        // TaskQueue taskQueue = new TaskQueue();
        TaskQueue taskQueue2 = new TaskQueue();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        String task = taskQueue2.getTask();
                        if(StringUtils.isNotEmpty(task)) System.out.println("this.getName() = " + this.getName() + ", get task" + task);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            threads.add(t);
        }

        Thread add = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    String task = "task-" + i;
                    System.out.println("add task = " + task);
                    taskQueue2.addTask(task);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        add.start();
        add.join();
        Thread.sleep(100);
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    public static void ps5() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 6; i++) {
            executorService.submit(new Task("Task-"+i));
        }
        executorService.shutdown(); // 放入6个任务，线程池最多存放4个，shutdown会等待所有任务执行完毕； shutdownnow会立刻停止正在执行的任务
    }

    /**
     * 思考：
     *
     * Q1:在FixedRate模式下，假设每秒触发，如果某次任务执行时间超过1秒，后续任务会不会并发执行？
     * A1:If any execution of this task takes longer than its period, then subsequent executions may start late, but will not concurrently execute.
     * 译：如果此任务的任何执行时间超过其周期，则后续执行可能会延迟开始，但不会并发执行。
     *
     * Q2:如果任务抛出了异常，后续任务是否继续执行？
     * A2：If any execution of the task encounters an exception, subsequent executions are suppressed.
     * 译：如果任务的任何执行遇到异常，则将禁止后续任务的执行。
     * @throws InterruptedException
     */
    public static void scheduledThreadPool() throws InterruptedException {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
        ses.schedule(new Task("once-task"), 1, TimeUnit.SECONDS);
        ses.scheduleAtFixedRate(new Task("fixed-rate"), 1,1, TimeUnit.SECONDS); // 固定频率：不管任务是否执行完毕，继续执行下一次
        // ses.scheduleWithFixedDelay(new Task("fixed-delay"), 0,1, TimeUnit.SECONDS); // 上一任务执行完后才继续执行下一次
        Thread.sleep(10000);
        ses.shutdown();
    }

    public static void threadLocal() {
        try(UserContext ctx = new UserContext("lee")) {
            setp1();
            setp2();
        }
    }

    public static void setp1() {
        String currentUser = UserContext.currentUser();
        System.out.println("currentUser = " + currentUser);
    }

    public static void setp2() {
        String currentUser = UserContext.currentUser();
        System.out.println("currentUser = " + currentUser);
    }

    public static void t1() {
        // new Thread(() -> System.out.println("runnable")).start();
        new Thread(()-> System.out.println("runnable"))
        {
            @Override
            public void run() {
                System.out.println("Thread run");
            }
        }.start();
    }

    public static void t2() {
        // 获取Java线程管理MXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean() ;
        // 不需要获取同步的monitor和synchronizer信息，仅获取线程和线程堆栈信息
        ThreadInfo[ ] threadInfos =
                threadMXBean.dumpAllThreads(false, false) ;
        // 遍历线程信息，仅打印线程ID和线程名称信息
        for (ThreadInfo threadInfo : threadInfos) {
            System. out. println("[ " + threadInfo. getThreadId() +
                    "] " + threadInfo.getThreadName()) ;
        }
        Object o = Optional.ofNullable(null).orElse("7");
        System.out.println("o = " + o);
        String s = StringUtils.defaultIfEmpty(" ", "7");
        String s2 = StringUtils.defaultIfBlank("     ", "7");
        System.out.println("s = " + s);
        System.out.println("s2 = " + s2);
    }
}
