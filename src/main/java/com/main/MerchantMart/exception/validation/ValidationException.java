package com.main.MerchantMart.exception.validation;

import com.main.MerchantMart.exception.base.MerchantMartException;

public abstract class ValidationException extends MerchantMartException {

    public ValidationException(String message) {
        super(message);
    }
}