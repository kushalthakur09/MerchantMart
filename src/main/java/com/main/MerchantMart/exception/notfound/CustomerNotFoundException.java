package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class CustomerNotFoundException extends NotFoundException  {
    public CustomerNotFoundException() {
        super(ExceptionMessageConstants.CUSTOMER_NOT_FOUND);
    }
}
