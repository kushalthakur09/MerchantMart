package com.main.MerchantMart.service;

import com.main.MerchantMart.payload.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Long id,CategoryDto categoryDto);

    List<CategoryDto> getCategoriesByStoreId(Long storeId);

    void deleteCategory(Long id);
}
