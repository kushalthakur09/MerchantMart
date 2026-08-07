package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class OrderNotFoundException extends  NotFoundException {
    public OrderNotFoundException(){
        super(ExceptionMessageConstants.ORDER_NOT_FOUND);
    }
    public OrderNotFoundException(String message){
        super(ExceptionMessageConstants.ORDER_NOT_FOUND+message);
    }
}
