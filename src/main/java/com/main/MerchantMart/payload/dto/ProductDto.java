package com.main.MerchantMart.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private  Long id;

    private String name;

    private String sku;

    private String description;

    private  Double mrp;

    private  Double sellingPrice;

    private  String brand;

    private  String image;

    private  CategoryDto category;

    private  Long categoryId;

    private Long storeId;

}
