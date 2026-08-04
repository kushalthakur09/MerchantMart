package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public  class StoreNotFoundException extends  NotFoundException{
    public StoreNotFoundException(){
        super(ExceptionMessageConstants.STORE_NOT_FOUND);
    }
    public StoreNotFoundException(String message){
        super(ExceptionMessageConstants.STORE_NOT_FOUND+message);
    }
}

