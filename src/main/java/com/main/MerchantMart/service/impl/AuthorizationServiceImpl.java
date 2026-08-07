package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.forbidden.AccessDeniedException;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserService userService;

    @Override
    public void authorizeStoreCreate() {
        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_STORE);
    }

    @Override
    public void authorizeStoreUpdate(Store store) {
        authorizeStore(store, false);
    }

    @Override
    public void authorizeStoreDelete(Store store) {
        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_STORE);
    }

    @Override
    public void authorizeStoreView(Store store) {
        authorizeStore(store, true);
    }

    // ===========================
    // BRANCH
    // ===========================

    @Override
    public void authorizeBranchCreate(Store store) {
        authorizeStore(store, false);
    }

    @Override
    public void authorizeBranchUpdate(Branch branch) {
        authorizeStore(branch.getStore(), false);
    }

    @Override
    public void authorizeBranchDelete(Branch branch) {
        authorizeStore(branch.getStore(), false);
    }

    @Override
    public void authorizeBranchView(Store store) {
        authorizeSameStore(store);
    }


    @Override
    public void authorizeCategoryCreate(Store store) {
        authorizeStore(store, false);
    }

    @Override
    public void authorizeCategoryUpdate(Category category) {
        authorizeStore(category.getStore(), true);
    }

    @Override
    public void authorizeCategoryDelete(Category category) {
        authorizeStore(category.getStore(), false);
    }

    @Override
    public void authorizeCategoryView(Store store) {
        authorizeSameStore(store);
    }

    @Override
    public void authorizeProductCreate(Store store) {
        authorizeStore(store, false);
    }

    @Override
    public void authorizeProductUpdate(Product product) {
        authorizeStore(product.getStore(), true);
    }

    @Override
    public void authorizeProductDelete(Product product) {
        authorizeStore(product.getStore(), false);
    }

    @Override
    public void authorizeProductView(Store store) {
        authorizeSameStore(store);
    }

    @Override
    public void authorizeInventoryCreate(Branch branch) {
        authorizeBranch(branch, true);
    }

    @Override
    public void authorizeInventoryUpdate(Inventory inventory) {
        authorizeBranch(inventory.getBranch(), true);
    }

    @Override
    public void authorizeInventoryDelete(Inventory inventory) {
        authorizeBranch(inventory.getBranch(), false);
    }

    @Override
    public void authorizeInventoryView(Branch branch) {
        authorizeBranch(branch, true);
    }


    @Override
    public void authorizeEmployeeCreate(Role role) {
        // TODO
    }

    @Override
    public void authorizeEmployeeUpdate(User employee) {
        // TODO
    }

    @Override
    public void authorizeEmployeeDelete(User employee) {
        // TODO
    }

    @Override
    public void authorizeEmployeeView(User employee) {
        // TODO
    }

    @Override
    public void authorizeRefundCreate(Branch branch) {
        authorizeBranch(branch, true);
    }

    @Override
    public void authorizeRefundView(Branch branch) {
        authorizeBranch(branch, true);
    }

    @Override
    public void authorizeRefundDelete(Refund refund) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND_DELETION);
    }

    @Override
    public void authorizeRefundViewAll() {
        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        throw new AccessDeniedException(
                ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND);
    }
    // ===========================
    // PRIVATE HELPERS
    // ===========================

    private void authorizeStore(Store store, boolean allowStoreManager) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (belongsToStore(user, store)
                && (isStoreAdmin(user)
                || (allowStoreManager && isStoreManager(user)))) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_STORE);
    }

    private void authorizeSameStore(Store store) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (belongsToStore(user, store)) {
            return;
        }
        throw new AccessDeniedException( ExceptionMessageConstants.ACCESS_DENIED_TO_STORE);
    }

    private void authorizeBranch(Branch branch, boolean allowBranchManager) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (belongsToStore(user, branch.getStore())
                && (isStoreAdmin(user) || isStoreManager(user))) {
            return;
        }
        if (allowBranchManager
                && (isBranchManager(user) || isCashier(user))
                && belongsToBranch(user, branch)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_BRANCH);
    }

    // ===========================
    // USER HELPERS
    // ===========================

    private User currentUser() {
        return userService.getCurrentUser();
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ROLE_ADMIN;
    }

    private boolean isStoreAdmin(User user) {
        return user.getRole() == Role.ROLE_STORE_ADMIN;
    }

    private boolean isStoreManager(User user) {
        return user.getRole() == Role.ROLE_STORE_MANAGER;
    }

    private boolean isBranchManager(User user) {
        return user.getRole() == Role.ROLE_BRANCH_MANAGER;
    }
    private boolean isCashier(User user) {
        return user.getRole() == Role.ROLE_BRANCH_CASHIER;
    }

    private boolean belongsToStore(User user, Store store) {
        return user.getStore() != null
                && user.getStore().getId().equals(store.getId());
    }

    private boolean belongsToBranch(User user, Branch branch) {
        return user.getBranch() != null
                && user.getBranch().getId().equals(branch.getId());
    }
}