package com.main.MerchantMart.exception.conflict;

public class ProductAlreadyExistsException extends ConflictException  {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }

    public ProductAlreadyExistsException(String message,String additionalMessage) {
        super(message+additionalMessage);
    }
}
