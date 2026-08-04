package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class BranchNotFoundException extends NotFoundException  {
    public BranchNotFoundException() {
        super(ExceptionMessageConstants.BRANCH_NOT_FOUND);
    }
}
