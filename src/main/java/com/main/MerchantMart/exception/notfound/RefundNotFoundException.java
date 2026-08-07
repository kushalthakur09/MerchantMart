package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class RefundNotFoundException extends  RuntimeException{
    public RefundNotFoundException(){
        super(ExceptionMessageConstants.REFUND_NOT_FOUND);
    }
    public RefundNotFoundException(String id){
        super(ExceptionMessageConstants.REFUND_NOT_FOUND +" with id : " + id);
    }
}
