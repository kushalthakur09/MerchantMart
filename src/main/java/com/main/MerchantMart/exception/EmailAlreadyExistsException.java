package com.main.MerchantMart.exception;

public class EmailAlreadyExistsException extends  RuntimeException{
    public  EmailAlreadyExistsException(String message){
        super(message);
    }
}
