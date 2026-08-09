package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.ProductDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.ProductService;
import com.main.MerchantMart.utility.contants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> create(
            @Valid @RequestBody ProductDto productDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productDto));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductDto>> getProductByStoreId(
            @PathVariable Long storeId) {

        return ResponseEntity.ok(productService.getProductsByStoreId(storeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(
            @PathVariable Long id,
            @RequestBody ProductDto productDto) {

        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                new ApiResponse(ApiConstants.PRODUCT_DELETED_SUCCESSFULLY));
    }

    @GetMapping("/store/{storeId}/search")
    public ResponseEntity<List<ProductDto>> search(
            @PathVariable Long storeId,
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                productService.searchByKeyword(storeId, keyword));
    }
}