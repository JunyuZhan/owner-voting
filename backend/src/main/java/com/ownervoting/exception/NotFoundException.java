package com.ownervoting.exception;

public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
    public static NotFoundException of(String resourceType, Object id) {
        return new NotFoundException(resourceType + " 不存在，ID: " + id);
    }
} 