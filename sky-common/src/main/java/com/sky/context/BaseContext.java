package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> adminThreadLocal = new ThreadLocal<>();
    public static ThreadLocal<String> userThreadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        adminThreadLocal.set(id);
    }

    public static Long getCurrentId() {
        return adminThreadLocal.get();
    }

    public static void removeCurrentId() {
        adminThreadLocal.remove();
    }

    public static void setCurrentUserId(String id) {
        userThreadLocal.set(id);
    }

    public static String getCurrentUserId() {
        return userThreadLocal.get();
    }

    public static void removeCurrentUserId() {
        userThreadLocal.remove();
    }

}
