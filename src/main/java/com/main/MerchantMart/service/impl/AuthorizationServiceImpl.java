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

    @Override
    public void authorizeStoreViewAll() {
        if (isAdmin(currentUser())) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_STORE);
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

    // Order
    @Override
    public void authorizeOrderCreate(Branch branch) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (isCashier(user) && belongsToBranch(user, branch)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_ORDER);
    }

    @Override
    public void authorizeOrderView(Branch branch) {
        authorizeBranch(branch, true);
    }

    @Override
    public void authorizeOrderViewByCashier(User cashier) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (isStoreAdmin(user)
                && belongsToStore(user, cashier.getStore())) {
            return;
        }
        if (isStoreManager(user) && belongsToStore(user, cashier.getStore())) {
            return;
        }

        if (isBranchManager(user) && belongsToBranch(user, cashier.getBranch())) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_ORDER);
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
    public void authorizeEmployeeCreate(Role roleToCreate) {

        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        if (isStoreAdmin(user)
                && (roleToCreate == Role.ROLE_STORE_MANAGER
                || roleToCreate == Role.ROLE_BRANCH_MANAGER)) {
            return;
        }

        if (isStoreManager(user) && roleToCreate == Role.ROLE_BRANCH_MANAGER) {
            return;
        }

        if (isBranchManager(user) && roleToCreate == Role.ROLE_BRANCH_CASHIER) {
            return;
        }

        throw new AccessDeniedException( ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
    }

    @Override
    public void authorizeEmployeeBranchView(Branch branch) {
        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        if (belongsToStore(user, branch.getStore())
                && (isStoreAdmin(user) || isStoreManager(user))) {
            return;
        }

        if (isBranchManager(user) && belongsToBranch(user, branch)) {
            return;
        }

        throw new AccessDeniedException(
                ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
    }

    @Override
    public void authorizeEmployeeUpdate(User employee) {

        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        if (isStoreAdmin(user)
                && belongsToStore(user, employee.getStore())
                && employee.getRole() != Role.ROLE_ADMIN) {
            return;
        }

        if (isStoreManager(user)
                && belongsToStore(user, employee.getStore())
                && (isBranchManager(employee)
                || isBranchCashier(employee))) {
            return;
        }

        if (isBranchManager(user)
                && belongsToBranch(user, employee.getBranch())
                && isBranchCashier(employee)) {
            return;
        }

        throw new AccessDeniedException(
                ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
    }

    @Override
    public void authorizeEmployeeDelete(User employee) {

        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        if (isStoreAdmin(user)
                && belongsToStore(user, employee.getStore())) {
            return;
        }

        if (isStoreManager(user)
                && belongsToStore(user, employee.getStore())
                && (isBranchManager(employee)
                || isBranchCashier(employee))) {
            return;
        }

        if (isBranchManager(user)
                && belongsToBranch(user, employee.getBranch())
                && isBranchCashier(employee)) {
            return;
        }

        throw new AccessDeniedException(
                ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
    }

    @Override
    public void authorizeEmployeeView(User employee) {

        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        if (belongsToStore(user, employee.getStore())) {
            return;
        }

        throw new AccessDeniedException(
                ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
    }

    @Override
    public void authorizeEmployeeStoreView(Store store) {
        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        if (belongsToStore(user, store)
                && (isStoreAdmin(user) || isStoreManager(user))) {
            return;
        }

        throw new AccessDeniedException(
                ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
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
        if (isCashier(user) || isBranchManager(user)) {
            if (user.getBranch() != null) {
                return;
            }
        }
        if (isStoreAdmin(user) || isStoreManager(user)) {
            if (user.getStore() != null) {
                return;
            }
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND);
    }

    @Override
    public void authorizeRefundViewByCashier(User cashier) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }

        // Cashier can only view his own refunds
        if (isCashier(user)) {
            if (user.getId().equals(cashier.getId())) {
                return;
            }

            throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND);
        }

        // Branch manager can view cashiers from his branch
        if (isBranchManager(user)) {
            if (belongsToBranch(user, cashier.getBranch())) {
                return;
            }

            throw new AccessDeniedException( ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND);
        }

        // Store admin / manager can view cashiers from their store
        if ((isStoreAdmin(user) || isStoreManager(user))  && belongsToStore(user, cashier.getStore())) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND);
    }
    @Override
    public void authorizeOrderDelete(Order order) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_ORDER);
    }


    @Override
    public void authorizeCustomerCreate() {
        currentUser();
    }

    @Override
    public void authorizeCustomerUpdate() {
        currentUser();
    }

    @Override
    public void authorizeCustomerDelete() {
        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_CUSTOMER);
    }

    @Override
    public void authorizeCustomerView() {
        currentUser();
    }

    @Override
    public void authorizeShiftStart() {
        User user = currentUser();
        if (isAdmin(user) || isCashier(user)) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
    }

    @Override
    public void authorizeShiftEnd(ShiftReport shiftReport) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (isCashier(user)
                && belongsToBranch(user, shiftReport.getBranch())
                && user.getId().equals(shiftReport.getCashier().getId())) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
    }

    @Override
    public void authorizeShiftViewOwn() {
        User user = currentUser();
        if (isAdmin(user) || isCashier(user)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
    }

    @Override
    public void authorizeShiftViewByCashier(User cashier) {

        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }

        if (cashier.getBranch() == null) {
            throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
        }

        // Branch Manager → only cashiers in his branch
        if (isBranchManager(user)
                && belongsToBranch(user, cashier.getBranch())) {
            return;
        }

        // Store Admin / Store Manager → employees in same store
        if ((isStoreAdmin(user) || isStoreManager(user))
                && belongsToStore(user, cashier.getStore())) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
    }

    @Override
    public void authorizeShiftViewByBranch(Branch branch) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        if ((isStoreAdmin(user) || isStoreManager(user)) && belongsToStore(user, branch.getStore())) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
    }

    @Override
    public void authorizeShiftReportView(ShiftReport shiftReport) {
        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        if (shiftReport.getBranch() == null) {
            throw new AccessDeniedException(
                    ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
        }

        if (isStoreAdmin(user) || isStoreManager(user)) {
            if (belongsToStore(user, shiftReport.getBranch().getStore())) {
                return;
            }
        }

        if (isBranchManager(user) && belongsToBranch(user, shiftReport.getBranch())) {
            return;
        }

        if (isCashier(user)
                && shiftReport.getCashier() != null
                && user.getId().equals(shiftReport.getCashier().getId())) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
    }

    @Override
    public void authorizeShiftViewAll() {
        if (isAdmin(currentUser())) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
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

    private boolean isBranchCashier(User user) {
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