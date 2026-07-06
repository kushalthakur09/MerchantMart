package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class ProductNotFoundException extends RuntimeException {
   public ProductNotFoundException(){
        super(ExceptionMessageConstants.PRODUCT_NOT_FOUND);
    }
}
