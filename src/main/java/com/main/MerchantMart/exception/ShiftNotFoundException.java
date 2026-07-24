package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class ShiftNotFoundException extends  RuntimeException{
    public ShiftNotFoundException(){
        super(ExceptionMessageConstants.SHIFT_NOT_FOUND);
    }
}
