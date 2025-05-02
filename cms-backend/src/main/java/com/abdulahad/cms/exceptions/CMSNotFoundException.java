package com.abdulahad.cms.exceptions;

public class CMSNotFoundException extends RuntimeException{

    public CMSNotFoundException(String message) {
        super(message);
    }
    public CMSNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public CMSNotFoundException(Throwable cause) {
        super(cause);
    }
}
