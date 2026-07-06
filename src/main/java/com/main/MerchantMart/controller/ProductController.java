package com.main.MerchantMart.controller;

import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.ProductDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.ProductService;
import com.main.MerchantMart.service.UserService;
import com.main.MerchantMart.utility.contants.ApiConstants;
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
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody  ProductDto productDto, @RequestHeader("Authorization") String jwt){
        User user= userService.getUserFromJwtToken(jwt);
        return  ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDto,user));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductDto>> getProductByStoreId(@PathVariable("storeId") Long storeId){
        return ResponseEntity.ok(productService.getProductsByStoreId(storeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable("id") Long id,@RequestBody ProductDto productDto, @RequestHeader("Authorization") String jwt){
        return  ResponseEntity.status(HttpStatus.CREATED).body(productService.updateProduct(id,productDto,null));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id, @RequestHeader("Authorization") String jwt){
        User user= userService.getUserFromJwtToken(jwt);
        productService.deleteProduct(id,user);
        return  ResponseEntity.ok(new ApiResponse(ApiConstants.PRODUCT_DELETED_SUCCESSFULLY));
    }

    @GetMapping("/store/{storeId}/search")
    public ResponseEntity<List<ProductDto>> search(@PathVariable("storeId") Long storeId,@RequestParam String keyword){
        return ResponseEntity.ok(productService.searchByKeyword(storeId,keyword));
    }
}
