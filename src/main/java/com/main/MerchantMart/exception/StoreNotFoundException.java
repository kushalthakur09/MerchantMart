package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class StoreNotFoundException extends  RuntimeException{
    public StoreNotFoundException(){
        super(ExceptionMessageConstants.STORE_NOT_FOUND);
    }
    public StoreNotFoundException(String message){
        super(ExceptionMessageConstants.STORE_NOT_FOUND+message);
    }
}
