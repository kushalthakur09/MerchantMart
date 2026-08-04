package com.main.MerchantMart.exception.conflict;

public class EmailAlreadyExistsException extends  ConflictException {
    public  EmailAlreadyExistsException(String message){
        super(message);
    }
}
