package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.CustomerStatus;
import com.main.MerchantMart.entity.Customer;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.notfound.CustomerNotFoundException;
import com.main.MerchantMart.exception.notfound.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.CustomerDto;
import com.main.MerchantMart.repository.CustomerRepository;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.CustomerService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AuthorizationService authorizationService;
    private final UserService userService;

    // =========================================================
    // GENERAL CUSTOMER MANAGEMENT
    // =========================================================

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {

        authorizationService.authorizeCustomerCreate();

        Store store = getCurrentUserStore();

        validateDuplicateCustomer(
                store.getId(),
                customerDto.getPhoneNo(),
                customerDto.getEmail(),
                null
        );

        Customer customer = CustomerMapper.toEntity(
                customerDto,
                store
        );

        return CustomerMapper.toDto(
                customerRepository.save(customer)
        );
    }

    @Override
    public CustomerDto updateCustomer(
            Long id,
            CustomerDto customerDto
    ) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        authorizationService.authorizeCustomerUpdate(customer);

        validateDuplicateCustomer(
                customer.getStore().getId(),
                customerDto.getPhoneNo(),
                customerDto.getEmail(),
                id
        );

        if (customerDto.getFullName() != null) {
            customer.setFullName(customerDto.getFullName());
        }

        if (customerDto.getEmail() != null) {
            customer.setEmail(customerDto.getEmail());
        }

        if (customerDto.getPhoneNo() != null) {
            customer.setPhoneNo(customerDto.getPhoneNo());
        }

        return CustomerMapper.toDto(
                customerRepository.save(customer)
        );
    }

    @Override
    public void deactivateCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        authorizationService.authorizeCustomerDeactivation(customer);

        customer.setStatus(CustomerStatus.INACTIVE);

        customerRepository.save(customer);
    }

    @Override
    public void activateCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        authorizationService.authorizeCustomerActivation(customer);

        customer.setStatus(CustomerStatus.ACTIVE);

        customerRepository.save(customer);
    }

    @Override
    public CustomerDto getCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        authorizationService.authorizeCustomerView(customer);

        validateCustomerScope(customer);

        return CustomerMapper.toDto(customer);
    }

    @Override
    public List<CustomerDto> getAllCustomer() {

        authorizationService.authorizeCustomerViewAll();

        User user = userService.getCurrentUser();

        // Super Admin
        if (user.getStore() == null && user.getBranch() == null) {

            return customerRepository.findAll()
                    .stream()
                    .map(CustomerMapper::toDto)
                    .toList();
        }

        // Branch Manager
        if (user.getBranch() != null) {

            return customerRepository
                    .findByOrderBranchId(user.getBranch().getId())
                    .stream()
                    .map(CustomerMapper::toDto)
                    .toList();
        }

        // Store Admin / Store Manager
        Store store = getCurrentUserStore();

        return customerRepository
                .findByStoreId(store.getId())
                .stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    @Override
    public List<CustomerDto> search(String keyword) {

        authorizationService.authorizeCustomerSearch();

        User user = userService.getCurrentUser();

        // Super Admin
        if (user.getStore() == null && user.getBranch() == null) {

            return customerRepository
                    .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            keyword,
                            keyword
                    )
                    .stream()
                    .map(CustomerMapper::toDto)
                    .toList();
        }

        // Branch Manager
        if (user.getBranch() != null) {

            return customerRepository
                    .searchByOrderBranch(
                            user.getBranch().getId(),
                            keyword
                    )
                    .stream()
                    .map(CustomerMapper::toDto)
                    .toList();
        }

        // Store Admin / Store Manager
        Store store = getCurrentUserStore();

        return customerRepository
                .searchByStore(store.getId(), keyword)
                .stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    // =========================================================
    // POS / ORDER CUSTOMER OPERATIONS
    // =========================================================

    @Override
    public CustomerDto createCustomerForOrder(
            CustomerDto customerDto
    ) {

        authorizationService.authorizeCustomerCreateForOrder();

        Store store = getCurrentUserStore();

        validateDuplicateCustomer(
                store.getId(),
                customerDto.getPhoneNo(),
                customerDto.getEmail(),
                null
        );

        Customer customer = CustomerMapper.toEntity(
                customerDto,
                store
        );

        return CustomerMapper.toDto(
                customerRepository.save(customer)
        );
    }

    @Override
    public CustomerDto getCustomerForOrder(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        authorizationService.authorizeCustomerAccessForOrder();

        Store store = getCurrentUserStore();

        if (customer.getStore() == null
                || !customer.getStore().getId().equals(store.getId())) {

            throw new CustomerNotFoundException();
        }

        return CustomerMapper.toDto(customer);
    }

    @Override
    public List<CustomerDto> searchForOrder(String keyword) {

        authorizationService.authorizeCustomerAccessForOrder();

        Store store = getCurrentUserStore();

        return customerRepository
                .searchByStore(store.getId(), keyword)
                .stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    @Override
    public void activateCustomerForOrder(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        authorizationService.authorizeCustomerActivationForOrder(customer);

        Store store = getCurrentUserStore();

        if (customer.getStore() == null
                || !customer.getStore().getId().equals(store.getId())) {

            throw new CustomerNotFoundException();
        }

        customer.setStatus(CustomerStatus.ACTIVE);

        customerRepository.save(customer);
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Store getCurrentUserStore() {

        User user = userService.getCurrentUser();

        if (user.getStore() != null) {
            return user.getStore();
        }

        if (user.getBranch() != null
                && user.getBranch().getStore() != null) {

            return user.getBranch().getStore();
        }

        throw new StoreNotFoundException();
    }

    private void validateCustomerScope(Customer customer) {

        User user = userService.getCurrentUser();

        // Super Admin can access every customer
        if (user.getStore() == null && user.getBranch() == null) {
            return;
        }

        // Branch Manager can only access customers
        // who have orders from their branch
        if (user.getBranch() != null) {

            boolean accessible = customerRepository
                    .findByOrderBranchId(user.getBranch().getId())
                    .stream()
                    .anyMatch(c -> c.getId().equals(customer.getId()));

            if (!accessible) {
                throw new CustomerNotFoundException();
            }

            return;
        }

        // Store-level users
        Store store = getCurrentUserStore();

        if (customer.getStore() == null
                || !customer.getStore().getId().equals(store.getId())) {

            throw new CustomerNotFoundException();
        }
    }

    private void validateDuplicateCustomer(
            Long storeId,
            String phoneNo,
            String email,
            Long currentCustomerId
    ) {

        if (phoneNo != null && !phoneNo.isBlank()) {

            boolean phoneExists = customerRepository
                    .findByStoreIdAndPhoneNo(storeId, phoneNo)
                    .stream()
                    .anyMatch(customer ->
                            currentCustomerId == null
                                    || !customer.getId().equals(currentCustomerId)
                    );

            if (phoneExists) {
                throw new IllegalArgumentException(
                        "A customer with this phone number already exists in this store."
                );
            }
        }

        if (email != null && !email.isBlank()) {

            boolean emailExists = customerRepository
                    .findByStoreIdAndEmailIgnoreCase(storeId, email)
                    .stream()
                    .anyMatch(customer ->
                            currentCustomerId == null
                                    || !customer.getId().equals(currentCustomerId)
                    );

            if (emailExists) {
                throw new IllegalArgumentException(
                        "A customer with this email already exists in this store."
                );
            }
        }
    }
}