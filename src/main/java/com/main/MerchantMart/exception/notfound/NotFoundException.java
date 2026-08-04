package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.exception.base.MerchantMartException;

public abstract class NotFoundException extends MerchantMartException {

    public NotFoundException(String message) {
        super(message);
    }
}