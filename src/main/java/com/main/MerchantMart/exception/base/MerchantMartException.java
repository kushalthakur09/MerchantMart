package com.main.MerchantMart.exception.base;

public abstract class MerchantMartException extends RuntimeException {

    public MerchantMartException(String message) {
        super(message);
    }
}