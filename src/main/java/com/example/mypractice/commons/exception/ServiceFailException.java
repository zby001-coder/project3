package com.example.mypractice.commons.exception;

public class ServiceFailException extends Exception {
    public ServiceFailException() {
        super("服务失败!");
    }

    public ServiceFailException(String name) {
        super("服务失败: " + name);
    }
}
