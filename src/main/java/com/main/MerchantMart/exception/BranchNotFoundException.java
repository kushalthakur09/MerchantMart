package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException() {
        super(ExceptionMessageConstants.BRANCH_NOT_FOUND);
    }
}
