package com.main.MerchantMart.exception;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class InventoryNotFoundException extends  RuntimeException{
    public InventoryNotFoundException(){
        super(ExceptionMessageConstants.INVENTORY_NOT_FOUND);
    }
    public InventoryNotFoundException(String additionalMessage){
        super(ExceptionMessageConstants.INVENTORY_NOT_FOUND+additionalMessage);
    }
}
