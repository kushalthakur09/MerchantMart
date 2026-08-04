package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public  class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super(ExceptionMessageConstants.USER_NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(ExceptionMessageConstants.USER_NOT_FOUND + message);
    }

    public UserNotFoundException(Long cashierId) {
        super(ExceptionMessageConstants.CASHIER_NOT_FOUND+"with id :" + cashierId);
    }
}