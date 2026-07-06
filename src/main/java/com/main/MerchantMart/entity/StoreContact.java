package com.main.MerchantMart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreContact {

    @Column(nullable = false)
    private  String address;

    @Length(message = "Must be a 10 digit number",max = 10,min = 10)
    private  String phone;

    @Email(message = "Invalid Email Format")
    private  String email;
}
