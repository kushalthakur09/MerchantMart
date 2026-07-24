package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class ShiftAlreadyStartedException extends RuntimeException {
    public ShiftAlreadyStartedException(){
        super(ExceptionMessageConstants.SHIFT_ALREADY_STARTED);
    }
}
