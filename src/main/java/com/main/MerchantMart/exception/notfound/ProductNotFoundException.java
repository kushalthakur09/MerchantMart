package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException() {
        super(ExceptionMessageConstants.PRODUCT_NOT_FOUND);
    }
}
