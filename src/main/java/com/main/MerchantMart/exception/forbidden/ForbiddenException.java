package com.main.MerchantMart.exception.forbidden;

import com.main.MerchantMart.exception.base.MerchantMartException;

public abstract class ForbiddenException extends MerchantMartException {

    public ForbiddenException(String message) {
        super(message);
    }
}