package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class CategoryNotFoundException extends NotFoundException  {
    public CategoryNotFoundException() {
        super(ExceptionMessageConstants.CATEGORY_NOT_FOUND);
    }
}
