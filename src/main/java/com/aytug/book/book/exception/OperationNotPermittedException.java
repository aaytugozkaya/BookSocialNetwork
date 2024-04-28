package com.aytug.book.book.exception;

public class OperationNotPermittedException extends RuntimeException {
    public OperationNotPermittedException(String s) {
        super(s);
    }
}
