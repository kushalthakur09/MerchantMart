package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class EmployeeNotFoundException extends  RuntimeException{
    public EmployeeNotFoundException(){
        super(ExceptionMessageConstants.EMPLOYEE_NOT_FOUND);
    }
    public EmployeeNotFoundException(String message){
        super(ExceptionMessageConstants.EMPLOYEE_NOT_FOUND+message);
    }
}
