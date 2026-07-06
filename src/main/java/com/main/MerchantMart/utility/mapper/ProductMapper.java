package com.main.MerchantMart.utility.mapper;

import com.main.MerchantMart.entity.Category;
import com.main.MerchantMart.entity.Product;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.payload.dto.ProductDto;

public class ProductMapper {

    public static ProductDto toDto(Product product) {
            return
                    ProductDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .sku(product.getSku())
                    .description(product.getDescription())
                    .mrp(product.getMrp())
                    .sellingPrice(product.getSellingPrice())
                    .brand(product.getBrand())
                    .storeId(product.getStore() != null ? product.getStore().getId():null)
                    .image(product.getImage())
                    .category(CategoryMapper.toDto(product.getCategory()))
                    .categoryId(product.getCategory().getId())
                    .build();
    }
    public static Product toEntity(ProductDto productDto, Store store, Category category){
        return Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .sku(productDto.getSku())
                .brand(productDto.getBrand())
                .mrp(productDto.getMrp())
                .sellingPrice(productDto.getSellingPrice())
                .store(store)
                .category(category)
                .build();
    }
}
