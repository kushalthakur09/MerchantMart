package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class RefundNotFound extends  RuntimeException{
    public RefundNotFound(){
        super(ExceptionMessageConstants.REFUND_NOT_FOUND);
    }
    public RefundNotFound(String id){
        super(ExceptionMessageConstants.REFUND_NOT_FOUND +" with id : " + id);
    }
}
