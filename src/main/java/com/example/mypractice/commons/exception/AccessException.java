package com.example.mypractice.commons.exception;

public class AccessException extends Exception{
    public AccessException() {
        super("越权操作!");
    }
}
