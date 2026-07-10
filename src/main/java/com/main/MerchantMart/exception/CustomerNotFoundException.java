package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException() {
        super(ExceptionMessageConstants.CUSTOMER_NOT_FOUND);
    }
}
