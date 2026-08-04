package com.main.MerchantMart.exception.notfound;


import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public  class ShiftNotFoundException extends  NotFoundException{
    public ShiftNotFoundException(){
        super(ExceptionMessageConstants.SHIFT_NOT_FOUND);
    }
    public ShiftNotFoundException(String additionalMessage){
        super(ExceptionMessageConstants.SHIFT_NOT_FOUND+additionalMessage);
    }
}
