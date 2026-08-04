package com.main.MerchantMart.exception.forbidden;

public class AccessDeniedException extends ForbiddenException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
