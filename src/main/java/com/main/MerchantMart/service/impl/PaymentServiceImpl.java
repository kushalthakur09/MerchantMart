package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.entity.Branch;
import com.main.MerchantMart.entity.Order;
import com.main.MerchantMart.entity.Payment;
import com.main.MerchantMart.entity.User;
import com.main.MerchantMart.payload.dto.OrderDto;
import com.main.MerchantMart.payload.dto.RazorpayCheckoutRequest;
import com.main.MerchantMart.payload.dto.RazorpayCheckoutResponse;
import com.main.MerchantMart.payload.dto.RazorpayCreateOrderRequest;
import com.main.MerchantMart.repository.OrderRepository;
import com.main.MerchantMart.repository.PaymentRepository;
import com.main.MerchantMart.service.OrderPreparationService;
import com.main.MerchantMart.service.OrderService;
import com.main.MerchantMart.service.PaymentService;
import com.main.MerchantMart.service.UserService;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.main.MerchantMart.service.OrderPreparationService.OrderPreparation;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderPreparationService orderPreparationService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Override
    @Transactional
    public RazorpayCheckoutResponse createRazorpayCheckout(RazorpayCheckoutRequest request) {
        try {
            User cashier = userService.getCurrentUser();
            Branch branch = cashier.getBranch();

            if (branch == null) {
                throw new IllegalStateException("Cashier is not assigned to a branch.");
            }

            // Convert checkout request into the common OrderDto
            OrderDto orderDto = OrderDto.builder()
                    .customerId(request.getCustomerId())
                    .items(request.getItems())
                    .build();

            // Reuse existing validation + price calculation
            OrderPreparation preparation = orderPreparationService.prepareOrder(
                            orderDto,
                            cashier,
                            branch
            );

            // Create MerchantMart order as PENDING
            Order order = Order.builder()
                    .branch(branch)
                    .cashier(cashier)
                    .customer(preparation.customer())
                    .paymentType(PaymentType.UPI)
                    .status(OrderStatus.PENDING)
                    .totalAmount(preparation.totalAmount())
                    .items(preparation.orderItems())
                    .build();

            preparation.orderItems().forEach(item -> item.setOrder(order));

            Order savedOrder = orderRepository.save(order);

            // Create MerchantMart payment as PENDING
            Payment payment = paymentServiceCreatePending(
                    savedOrder,
                    preparation.totalAmount()
            );

            // Create Razorpay order
            long amountInPaise = preparation.totalAmount()
                    .movePointRight(2)
                    .longValueExact();

            JSONObject razorpayRequest = new JSONObject();

            razorpayRequest.put("amount", amountInPaise);
            razorpayRequest.put("currency", "INR");
            razorpayRequest.put(
                    "receipt",
                    "ORDER_" + savedOrder.getId()
            );

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(razorpayRequest);

            // Store Razorpay order ID
            payment.setRazorpayOrderId(razorpayOrder.get("id"));
            paymentRepository.save(payment);

            return RazorpayCheckoutResponse.builder()
                    .orderId(savedOrder.getId())
                    .paymentId(payment.getId())
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .razorpayKeyId(razorpayKeyId)
                    .amount(preparation.totalAmount())
                    .currency("INR")
                    .build();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Razorpay checkout.",e);
        }
    }

    @Override
    public com.razorpay.Order createRazorpayOrder(
            RazorpayCreateOrderRequest request
    ) {
        try {

            long amountInPaise = request.getAmount()
                    .movePointRight(2)
                    .longValueExact();

            JSONObject orderRequest = new JSONObject();

            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put(
                    "receipt",
                    "TEST_RECEIPT_" + System.currentTimeMillis()
            );

            return razorpayClient.orders.create(orderRequest);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Razorpay order.",e);
        }
    }

    @Override
    public Payment createPayment(
            Order order,
            BigDecimal amount,
            PaymentStatus status
    ) {

        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .status(status)
                .build();

        return paymentRepository.save(payment);
    }


    // helper
    private Payment paymentServiceCreatePending(
            Order order,
            BigDecimal amount
    ) {
        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }
}