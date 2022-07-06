package com.example.mypractice.commons.exception;

public class FilterException extends Exception {
    public FilterException() {
        super("参数错误!");
    }

    public FilterException(String name) {
        super("参数错误: " + name);
    }
}
