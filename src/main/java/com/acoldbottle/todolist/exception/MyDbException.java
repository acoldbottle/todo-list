package com.acoldbottle.todolist.exception;

/**
 * DB 안에 있는 User, Category, Detail 관련 예외
 */
public class MyDbException extends RuntimeException {


    public MyDbException() {
        super();
    }

    public MyDbException(String message) {
        super(message);
    }

    public MyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }
}
