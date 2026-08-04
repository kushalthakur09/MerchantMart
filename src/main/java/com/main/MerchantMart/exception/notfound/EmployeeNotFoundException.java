package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class EmployeeNotFoundException extends  NotFoundException {
    public EmployeeNotFoundException(){
        super(ExceptionMessageConstants.EMPLOYEE_NOT_FOUND);
    }
    public EmployeeNotFoundException(String message){
        super(ExceptionMessageConstants.EMPLOYEE_NOT_FOUND+message);
    }
}
