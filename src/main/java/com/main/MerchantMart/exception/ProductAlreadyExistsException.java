package com.main.MerchantMart.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }

    public ProductAlreadyExistsException(String message,String additionalMessage) {
        super(message+additionalMessage);
    }
}
