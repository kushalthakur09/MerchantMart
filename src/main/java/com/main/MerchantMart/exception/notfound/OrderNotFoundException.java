package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class OrderNotFoundException extends  NotFoundException {
    public OrderNotFoundException(){
        super(ExceptionMessageConstants.ORDER_NOT_FOUND);
    }
    public OrderNotFoundException(String message){
        super(ExceptionMessageConstants.ORDER_NOT_FOUND+message);
    }

    public static class RefundNotFoundException extends  RuntimeException{
        public RefundNotFoundException(){
            super(ExceptionMessageConstants.REFUND_NOT_FOUND);
        }
        public RefundNotFoundException(String id){
            super(ExceptionMessageConstants.REFUND_NOT_FOUND +" with id : " + id);
        }
    }
}
