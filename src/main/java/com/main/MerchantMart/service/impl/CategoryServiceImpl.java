package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Category;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.exception.notfound.CategoryNotFoundException;
import com.main.MerchantMart.exception.notfound.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.CategoryDto;
import com.main.MerchantMart.repository.CategoryRepository;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.CategoryService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final StoreRepository storeRepository;
    private final AuthorizationService authorizationService;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Store store = storeRepository.findById(categoryDto.getStoreId())
                .orElseThrow(StoreNotFoundException::new);
        authorizationService.canManageStore(store);
        Category category = Category.builder()
                .name(categoryDto.getName())
                .store(store)
                .build();
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        authorizationService.canManageStore(category.getStore());
        category.setName(categoryDto.getName());
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategoriesByStoreId(Long storeId) {
        return categoryRepository.findByStoreId(storeId).stream().map(CategoryMapper::toDto).toList();
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        authorizationService.canManageStore(category.getStore());
        categoryRepository.delete(category);
    }


}
