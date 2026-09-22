package com.main.MerchantMart.controller;

import com.main.MerchantMart.payload.dto.RazorpayCheckoutRequest;
import com.main.MerchantMart.payload.dto.RazorpayCheckoutResponse;
import com.main.MerchantMart.payload.dto.RazorpayCreateOrderRequest;
import com.main.MerchantMart.service.PaymentService;
import com.razorpay.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<?> createRazorpayOrder(@Valid @RequestBody RazorpayCreateOrderRequest request) {
        Order razorpayOrder =paymentService.createRazorpayOrder(request);
        return ResponseEntity.ok(razorpayOrder.toString());
    }

    @PostMapping("/razorpay/checkout")
    public ResponseEntity<RazorpayCheckoutResponse> createRazorpayCheckout(
            @Valid @RequestBody RazorpayCheckoutRequest request
    ) {
        return ResponseEntity.ok(
                paymentService.createRazorpayCheckout(request)
        );
    }
}