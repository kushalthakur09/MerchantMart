package com.main.MerchantMart.exception.conflict;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class ShiftAlreadyStartedException extends ConflictException  {
    public ShiftAlreadyStartedException(){
        super(ExceptionMessageConstants.SHIFT_ALREADY_STARTED);
    }
}
