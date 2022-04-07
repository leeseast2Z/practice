package com.seast.thread;

/**
 * @Author: limf
 * @Date: 2022/4/7 09:07
 * @Description:
 */
public class UserContext implements AutoCloseable {

    static final ThreadLocal<String> ctx = new ThreadLocal<>();

    public UserContext(String user) {
        ctx.set(user);
    }

    public static String currentUser() {
        return ctx.get();
    }

    @Override
    public void close() {
        ctx.remove();
        System.out.println("执行close");
    }
}
