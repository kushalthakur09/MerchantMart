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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final AuthorizationService authorizationService;

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {

        Store store = storeRepository.findById(categoryDto.getStoreId())
                .orElseThrow(StoreNotFoundException::new);

        authorizationService.authorizeCategoryCreate(store);

        if (categoryRepository.existsByStoreIdAndNameIgnoreCase(
                store.getId(),
                categoryDto.getName().trim())) {

            throw new IllegalArgumentException("Category already exists in this store.");
        }

        Category category = Category.builder()
                .name(categoryDto.getName().trim())
                .store(store)
                .build();

        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(
            Long id,
            CategoryDto categoryDto) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        authorizationService.authorizeCategoryUpdate(category);

        String name = categoryDto.getName().trim();

        if (categoryRepository.existsByStoreIdAndNameIgnoreCaseAndIdNot(
                category.getStore().getId(),
                name,
                id)) {
            throw new IllegalArgumentException("Category already exists in this store.");
        }

        category.setName(name);

        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategoriesByStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);
        authorizationService.authorizeCategoryView(store);

        return categoryRepository.findByStoreId(storeId)
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        authorizationService.authorizeCategoryDelete(category);
        categoryRepository.delete(category);
    }


}
