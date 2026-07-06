package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.CategoryDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.CategoryService;
import com.main.MerchantMart.utility.contants.ApiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private  final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto categoryDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryDto));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByStoreId(@PathVariable("storeId") Long storeId){
        return  ResponseEntity.ok(categoryService.getCategoriesByStoreId(storeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable("id") Long id,@RequestBody CategoryDto categoryDto){
        return  ResponseEntity.ok(categoryService.updateCategory(id,categoryDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse(ApiConstants.CATEGORY_DELETED_SUCCESSFULLY));
    }
}
