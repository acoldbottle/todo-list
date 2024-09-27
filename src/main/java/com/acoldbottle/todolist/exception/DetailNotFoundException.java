package com.acoldbottle.todolist.exception;

public class DetailNotFoundException extends MyDbException {

    public DetailNotFoundException() {
    }

    public DetailNotFoundException(String message) {
        super(message);
    }

    public DetailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DetailNotFoundException(Throwable cause) {
        super(cause);
    }
}
