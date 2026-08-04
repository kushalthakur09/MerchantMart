package com.main.MerchantMart.exception.conflict;

import com.main.MerchantMart.exception.base.MerchantMartException;

public abstract class ConflictException extends MerchantMartException {

    public ConflictException(String message) {
        super(message);
    }
}