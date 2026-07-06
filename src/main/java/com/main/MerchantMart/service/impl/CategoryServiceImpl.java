package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.Category;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.CategoryNotFoundException;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.exception.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.CategoryDto;
import com.main.MerchantMart.repository.CategoryRepository;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.service.CategoryService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final StoreRepository storeRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        User user = userService.getCurrentUser();
        Store store = storeRepository.findById(categoryDto.getStoreId()).orElseThrow(() -> new StoreNotFoundException());
        checkAuthority(user,store);
        Category category = Category.builder()
                .name(categoryDto.getName())
                .store(store)
                .build();
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException());
        User user = userService.getCurrentUser();
        checkAuthority(user,category.getStore());
        category.setName(categoryDto.getName());
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategoriesByStoreId(Long storeId) {
        return categoryRepository.findByStoreId(storeId).stream().map(CategoryMapper::toDto).toList();
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException());
        User user = userService.getCurrentUser();
        checkAuthority(user,category.getStore());
        categoryRepository.delete(category);
    }

    private void checkAuthority(User user,Store store){
        boolean isAdmin = Role.ROLE_STORE_ADMIN.equals(user.getRole());
        boolean isManager = Role.ROLE_STORE_MANAGER.equals(user.getRole());
        boolean isFrmSameStore =user.equals(store.getStoreAdmin());

        if(!(isAdmin && isFrmSameStore) && !isManager){
            throw  new AccessDeniedException(ExceptionMessageConstants.ACCESS_DENIED_TO_CATEGORY);
        }

    }
}
