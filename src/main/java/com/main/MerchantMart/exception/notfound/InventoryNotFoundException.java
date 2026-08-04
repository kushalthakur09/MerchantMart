package com.main.MerchantMart.exception.notfound;

import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;

public class InventoryNotFoundException extends  NotFoundException {
    public InventoryNotFoundException(){
        super(ExceptionMessageConstants.INVENTORY_NOT_FOUND);
    }
    public InventoryNotFoundException(String additionalMessage){
        super(ExceptionMessageConstants.INVENTORY_NOT_FOUND+additionalMessage);
    }
}
