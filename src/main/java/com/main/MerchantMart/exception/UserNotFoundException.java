package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class UserNotFoundException extends  RuntimeException{
    public UserNotFoundException(){
        super(ExceptionMessageConstants.USER_NOT_FOUND);
    }
    public UserNotFoundException(String message){
        super(ExceptionMessageConstants.USER_NOT_FOUND+message);
    }
}
