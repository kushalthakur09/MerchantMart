package com.main.MerchantMart.service;

import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(Long id,ProductDto productDto);

    void deleteProduct(Long id);

    List<ProductDto> getProductsByStoreId(Long storeId);
    List<ProductDto> searchByKeyword(Long storeId,String keyword);
}
