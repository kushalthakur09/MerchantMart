package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.domain.CategoryStatus;
import com.main.MerchantMart.entity.Category;
import com.main.MerchantMart.payload.dto.CategoryDto;

import java.util.Objects;

public class CategoryMapper {

    public static CategoryDto toDto(Category category) {

        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setStatus(category.getStatus());

        categoryDto.setStoreId(
                Objects.isNull(category.getStore())
                        ? null
                        : category.getStore().getId()
        );

        return categoryDto;
    }
}