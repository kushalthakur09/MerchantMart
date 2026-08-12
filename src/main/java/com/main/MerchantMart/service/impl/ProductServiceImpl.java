package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.entity.Category;
import com.main.MerchantMart.entity.Product;
import com.main.MerchantMart.entity.Store;
import com.main.MerchantMart.exception.conflict.ProductAlreadyExistsException;
import com.main.MerchantMart.exception.notfound.CategoryNotFoundException;
import com.main.MerchantMart.exception.notfound.ProductNotFoundException;
import com.main.MerchantMart.exception.notfound.StoreNotFoundException;
import com.main.MerchantMart.payload.dto.ProductDto;
import com.main.MerchantMart.repository.CategoryRepository;
import com.main.MerchantMart.repository.ProductRepository;
import com.main.MerchantMart.repository.StoreRepository;
import com.main.MerchantMart.service.AuthorizationService;
import com.main.MerchantMart.service.ProductService;
import com.main.MerchantMart.utility.contants.ExceptionMessageConstants;
import com.main.MerchantMart.utility.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private  final ProductRepository productRepository;
    private  final StoreRepository storeRepository;
    private  final CategoryRepository categoryRepository;
    private  final AuthorizationService authorizationService;


    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Store store=storeRepository.findById(productDto.getStoreId())
                .orElseThrow(()-> new StoreNotFoundException(" with store id: "+productDto.getStoreId()));
        authorizationService.authorizeProductCreate(store);

        Category category=categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);
        if (!category.getStore().getId().equals(store.getId())) {
            throw new IllegalArgumentException("Category does not belong to the selected store.");
        }

        Product isExits=productRepository.findBySku(productDto.getSku());

        if(isExits != null){
            throw new ProductAlreadyExistsException(ExceptionMessageConstants.PRODUCT_ALREADY_EXISTS,"with provided sku");
        }

        Product product= ProductMapper.toEntity(productDto,store,category);
        return ProductMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
        authorizationService.authorizeProductUpdate(product);

        if (productDto.getName() != null) {
            product.setName(productDto.getName());
        }

        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }

        if (productDto.getSku() != null && !productDto.getSku().equals(product.getSku())) {

            Product existing = productRepository.findBySku(productDto.getSku());

            if (existing != null) {
                throw new ProductAlreadyExistsException( ExceptionMessageConstants.PRODUCT_ALREADY_EXISTS,
                        " with provided sku");
            }

            product.setSku(productDto.getSku());
        }

        if (productDto.getImage() != null) {
            product.setImage(productDto.getImage());
        }

        BigDecimal mrp = productDto.getMrp() != null
                ? productDto.getMrp()
                : product.getMrp();

        if (productDto.getMrp() != null) {
            product.setMrp(mrp);
        }
        BigDecimal sellingPrice = productDto.getSellingPrice() != null
                ? productDto.getSellingPrice()
                : product.getSellingPrice();

        if (sellingPrice.compareTo(mrp) > 0) {
            throw new IllegalArgumentException("Selling price cannot be greater than MRP.");
        }

        if (productDto.getSellingPrice() != null) {
            product.setSellingPrice(sellingPrice);
        }


        if (productDto.getBrand() != null) {
            product.setBrand(productDto.getBrand());
        }

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(CategoryNotFoundException::new);
            if (!category.getStore().getId().equals(product.getStore().getId())) {
                throw new IllegalArgumentException("Category does not belong to the same store.");
            }
            product.setCategory(category);
        }
        return ProductMapper.toDto(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product=productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
        authorizationService.authorizeProductDelete(product);
        productRepository.delete(product);
    }

    @Override
    public List<ProductDto> getProductsByStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        authorizationService.authorizeProductView(store);

        return productRepository.findByStoreId(storeId)
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> searchByKeyword(Long storeId, String keyword) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);
        authorizationService.authorizeProductSearch(store);
        return productRepository.searchByKeyword(storeId, keyword)
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }
}
