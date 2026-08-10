package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.domain.StoreStatus;
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
        if (isStoreAdmin(user)) {
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
    public void authorizeStoreAccess(Store store) {

        if (isAdmin(currentUser())) {
            return;
        }

        if (store.getStatus() == StoreStatus.PENDING) {
            throw new AccessDeniedException("Store is pending approval. Please contact admin.");
        }

        if (store.getStatus() == StoreStatus.BLOCKED) {
            throw new AccessDeniedException("Store is blocked. Please contact admin.");
        }

        if (store.getStatus() == StoreStatus.INACTIVE) {
            throw new AccessDeniedException("Store is deactivated. Please contact admin.");
        }
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
        authorizeStoreAccess(store);
        User user = currentUser();
        if (isStoreAdmin(user) && belongsToStore(user, store)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_BRANCH);
    }

    @Override
    public void authorizeBranchUpdate(Branch branch) {
        authorizeStoreAccess(branch.getStore());
        User user = currentUser();
        if (isStoreAdmin(user) && belongsToStore(user, branch.getStore())) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_BRANCH);
    }

    @Override
    public void authorizeBranchDelete(Branch branch) {
        authorizeStoreAccess(branch.getStore());
        User user = currentUser();
        if (isStoreAdmin(user) && belongsToStore(user, branch.getStore())) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_BRANCH);
    }

    @Override
    public void authorizeBranchView(Store store) {
        authorizeSameStore(store);
    }


    @Override
    public void authorizeCategoryCreate(Store store) {
        authorizeStoreAccess(store);
        User user = currentUser();
        if (isStoreAdmin(user) && belongsToStore(user, store)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_CATEGORY);
    }

    @Override
    public void authorizeCategoryUpdate(Category category) {
        authorizeStoreAccess(category.getStore());
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
        authorizeStoreAccess(store);
        User user = currentUser();
        if (isStoreAdmin(user) && belongsToStore(user, store)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_PRODUCT);
    }

    @Override
    public void authorizeProductUpdate(Product product) {
        authorizeStoreAccess(product.getStore());
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
    public void authorizeProductSearch(Store store) {

        User user = currentUser();

        if (isAdmin(user)) {
            return;
        }

        if (isStoreAdmin(user) || isStoreManager(user)) {
            if (belongsToStore(user, store)) {
                return;
            }
        }

        if (isBranchManager(user) || isCashier(user)) {
            if (user.getBranch() != null
                    && user.getBranch().getStore() != null
                    && user.getBranch().getStore().getId().equals(store.getId())) {
                return;
            }
        }

        throw new AccessDeniedException(
                ExceptionMessageConstants.ACCESS_DENIED_TO_PRODUCT);
    }

    // Order
    @Override
    public void authorizeOrderCreate(Branch branch) {
        authorizeStoreAccess(branch.getStore());
        User user = currentUser();
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
        authorizeStoreAccess(branch.getStore());

        User user = currentUser();

        if (isStoreAdmin(user) && belongsToStore(user, branch.getStore())) {
            return;
        }

        if (isBranchManager(user) && belongsToBranch(user, branch)) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_INVENTORY);
    }

    @Override
    public void authorizeInventoryUpdate(Inventory inventory) {
        Branch branch = inventory.getBranch();
        authorizeStoreAccess(branch.getStore());
        User user = currentUser();
        if (isStoreAdmin(user) && belongsToStore(user, branch.getStore())) {
            return;
        }
        if (isBranchManager(user) && belongsToBranch(user, branch)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_INVENTORY);
    }

    @Override
    public void authorizeInventoryDelete(Inventory inventory) {
        Branch branch = inventory.getBranch();
        authorizeStoreAccess(branch.getStore());
        User user = currentUser();

        if (isStoreAdmin(user) && belongsToStore(user, branch.getStore())) {
            return;
        }

        if (isBranchManager(user) && belongsToBranch(user, branch)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_INVENTORY);
    }

    @Override
    public void authorizeInventoryView(Branch branch) {
        authorizeBranch(branch, true);
    }


    @Override
    public void authorizeEmployeeCreate(
            Store store,
            Role roleToCreate,
            Branch branch) {

        authorizeStoreAccess(store);

        User user = currentUser();

        if (isStoreAdmin(user)
                && belongsToStore(user, store)
                && (roleToCreate == Role.ROLE_STORE_MANAGER
                || roleToCreate == Role.ROLE_BRANCH_MANAGER)) {
            return;
        }

        if (isStoreManager(user)
                && belongsToStore(user, store)
                && roleToCreate == Role.ROLE_BRANCH_MANAGER) {
            return;
        }

        if (isBranchManager(user)
                && roleToCreate == Role.ROLE_BRANCH_CASHIER
                && belongsToBranch(user, branch)) {
            return;
        }

        throw new AccessDeniedException(
                ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
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
        authorizeStoreAccess(employee.getStore());
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (isStoreAdmin(user)
                && belongsToStore(user, employee.getStore())
                && employee.getRole() != Role.ROLE_ADMIN) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
    }

    @Override
    public void authorizeEmployeeDelete(User employee) {

        if (isAdmin(currentUser())) {
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

        if ((isStoreAdmin(user) || isStoreManager(user))
                && belongsToStore(user, employee.getStore())) {
            return;
        }

        if ((isBranchManager(user)
                || isCashier(user))
                && employee.getBranch() != null
                && belongsToBranch(user, employee.getBranch())) {
            return;
        }

        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
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
        authorizeStoreAccess(branch.getStore());
        User user = currentUser();
        if (isCashier(user) && belongsToBranch(user, branch)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_REFUND);
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
        if (!isCashier(user) || user.getBranch() == null) {
            throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_SHIFT);
        }
        authorizeStoreAccess(user.getBranch().getStore());
    }

    @Override
    public void authorizeShiftEnd(ShiftReport shiftReport) {
        User user = currentUser();
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
        if (isCashier(user) && user.getBranch() != null) {
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
        if (isBranchManager(user)
                && belongsToBranch(user, branch)) {
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
                && user.getId().equals(shiftReport.getCashier().getId())
                && belongsToBranch(user, shiftReport.getBranch())) {
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

    @Override
    public void authorizeCustomerAccess() {
        User user = currentUser();
        if (isAdmin(user)
                || isStoreAdmin(user)
                || isStoreManager(user)
                || isBranchManager(user)
                || isCashier(user)) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_CUSTOMER);
    }

    //  Super - Admin Validations
    @Override
    public void authorizeStoreStatusChange() {
        if (isAdmin(currentUser())) {
            return;
        }
        throw new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_STORE);
    }

    @Override
    public void authorizeStoreAdminCreate() {
        if (!isAdmin(currentUser())) {
            throw new AccessDeniedException(
                    ExceptionMessageConstants.ACCESS_DENIED_TO_EMPLOYEE);
        }
    }
    // ===========================
    // PRIVATE HELPERS
    // ===========================

    private void authorizeStore(Store store, boolean allowStoreManager) {
        User user = currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (belongsToStore(user, store) && (isStoreAdmin(user) || (allowStoreManager && isStoreManager(user)))) {
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
                && branch != null
                && user.getBranch().getId().equals(branch.getId());
    }
}