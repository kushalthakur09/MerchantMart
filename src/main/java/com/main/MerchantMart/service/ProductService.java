package com.main.MerchantMart.service;

import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto, User user);

    ProductDto updateProduct(Long id,ProductDto productDto,User user);

    void deleteProduct(Long id,User user);

    List<ProductDto> getProductsByStoreId(Long storeId);
    List<ProductDto> searchByKeyword(Long storeId,String keyword);
}
