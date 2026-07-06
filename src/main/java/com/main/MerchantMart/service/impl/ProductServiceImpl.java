package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Category;
import com.main.MerchantMart.entity.Product;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.exception.*;
import com.main.MerchantMart.payload.dto.ProductDto;
import com.main.MerchantMart.repository.CategoryRepository;
import com.main.MerchantMart.repository.ProductRepository;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.service.ProductService;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.utility.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private  final ProductRepository productRepository;
    private  final StoreRepository storeRepository;
    private  final CategoryRepository categoryRepository;

    @Override
    public ProductDto createProduct(ProductDto productDto, User user) {
        Store store=storeRepository.findById(productDto.getStoreId())
                .orElseThrow(()->
                        new StoreNotFoundException(" with store id: "+productDto.getStoreId()
                        )
                );
        Category category=categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(()-> new CategoryNotFoundException());

        Product isExits=productRepository.findBySku(productDto.getSku());

        if(isExits != null){
            throw new ProductAlreadyExistsException(ExceptionMessageConstants.PRODUCT_ALREADY_EXISTS,"with provided sku");
        }

        Product product= ProductMapper.toEntity(productDto,store,category);
        return ProductMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto, User user) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        if (productDto.getName() != null) {
            product.setName(productDto.getName());
        }

        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }

        if (productDto.getSku() != null) {
            product.setSku(productDto.getSku());
        }

        if (productDto.getImage() != null) {
            product.setImage(productDto.getImage());
        }

        if (productDto.getMrp() != null) {
            product.setMrp(productDto.getMrp());
        }

        if (productDto.getSellingPrice() != null) {
            product.setSellingPrice(productDto.getSellingPrice());
        }

        if (productDto.getBrand() != null) {
            product.setBrand(productDto.getBrand());
        }

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() ->new CategoryNotFoundException());

            product.setCategory(category);
        }

        product.setUpdatedDate(LocalDateTime.now());

        return ProductMapper.toDto(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id, User user) {
        Product product=productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
        productRepository.delete(product);
    }

    @Override
    public List<ProductDto> getProductsByStoreId(Long storeId) {
        return productRepository.findByStoreId(storeId).stream().map(ProductMapper::toDto).toList();
    }

    @Override
    public List<ProductDto> searchByKeyword(Long storeId, String keyword) {
        return productRepository.searchByKeyword(storeId,keyword).stream().map(ProductMapper::toDto).toList();
    }
}
