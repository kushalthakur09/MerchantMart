package com.main.MerchantMart.service;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.*;


public interface AuthorizationService {

    // Store
    void authorizeStoreCreate();
    void authorizeStoreUpdate(Store store);
    void authorizeStoreDelete(Store store);
    void authorizeStoreView(Store store);

    // Branch
    void authorizeBranchCreate(Store store);
    void authorizeBranchUpdate(Branch branch);
    void authorizeBranchDelete(Branch branch);
    void authorizeBranchView(Store store);

    // Category
    void authorizeCategoryCreate(Store store);
    void authorizeCategoryUpdate(Category category);
    void authorizeCategoryDelete(Category category);
    void authorizeCategoryView(Store store);

    // Product
    void authorizeProductCreate(Store store);
    void authorizeProductUpdate(Product product);
    void authorizeProductDelete(Product product);
    void authorizeProductView(Store product);

    // Inventory
    void authorizeInventoryCreate(Branch branch);
    void authorizeInventoryUpdate(Inventory inventory);
    void authorizeInventoryDelete(Inventory inventory);
    void authorizeInventoryView(Branch branch);

    // Employee (later)
    void authorizeEmployeeCreate(Role roleToCreate);
    void authorizeEmployeeUpdate(User employee);
    void authorizeEmployeeDelete(User employee);
    void authorizeEmployeeView(User employee);

    // Refund
    void authorizeRefundCreate(Branch branch);
    void authorizeRefundView(Branch branch);
    void authorizeRefundDelete(Refund refund);
    void authorizeRefundViewAll();
}