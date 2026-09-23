package com.main.MerchantMart.service.impl;

import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.entity.*;
import com.main.MerchantMart.exception.notfound.InventoryNotFoundException;
import com.main.MerchantMart.payload.dto.*;
import com.main.MerchantMart.repository.InventoryRepository;
import com.main.MerchantMart.repository.OrderRepository;
import com.main.MerchantMart.repository.PaymentRepository;
import com.main.MerchantMart.service.OrderPreparationService;
import com.main.MerchantMart.service.OrderPreparationService.OrderPreparation;
import com.main.MerchantMart.service.PaymentService;
import com.main.MerchantMart.service.UserService;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderPreparationService orderPreparationService;
    private final InventoryRepository inventoryRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public RazorpayCheckoutResponse createRazorpayCheckout(RazorpayCheckoutRequest request) {
        try {
            User cashier = userService.getCurrentUser();
            Branch branch = cashier.getBranch();

            if (branch == null) {
                throw new IllegalStateException("Cashier is not assigned to a branch.");
            }
            if (request.getPaymentType() == PaymentType.CASH) {
                throw new IllegalArgumentException("Cash payments cannot use Razorpay.");
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
                    .paymentType(request.getPaymentType())
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

    @Override
    @Transactional
    public void verifyRazorpayPayment(RazorpayPaymentVerificationRequest request) {
        try {
            User cashier = userService.getCurrentUser();

            Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                    .orElseThrow(() -> new IllegalStateException("Payment not found."));

            Order order = payment.getOrder();

            // Idempotency protection
            // If Razorpay verification is called again after success,
            // do not deduct inventory again.
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                return;
            }

            // Make sure this order belongs to the current cashier.
            if (!order.getCashier().getId().equals(cashier.getId())) {
                throw new IllegalStateException("You are not authorized to verify this payment.");
            }

            if (order.getBranch() == null || cashier.getBranch() == null || !order.getBranch().getId().equals(cashier.getBranch().getId())) {
                throw new IllegalStateException("Order does not belong to your branch.");
            }

            // Make sure Razorpay order ID matches our stored payment.
            if (!payment.getRazorpayOrderId().equals(
                    request.getRazorpayOrderId()
            )) {
                throw new IllegalStateException("Invalid Razorpay order ID.");
            }

            // Verify Razorpay signature
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);

            if (!isValid) {
                throw new IllegalStateException("Invalid Razorpay payment signature.");
            }
            com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());
            String method = razorpayPayment.get("method");

            if ("card".equalsIgnoreCase(method)) {
                order.setPaymentType(PaymentType.CARD);
            } else if ("upi".equalsIgnoreCase(method)) {
                order.setPaymentType(PaymentType.UPI);
            } else {
                throw new IllegalStateException("Unsupported Razorpay payment method: " + method);
            }
            // Store Razorpay payment details
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());

            payment.setRazorpaySignature(request.getRazorpaySignature());

            payment.setStatus(PaymentStatus.SUCCESS);

            // Deduct inventory only after successful payment verification
            for (var item : order.getItems()) {

                Inventory inventory = inventoryRepository.findByProductIdAndBranchId(
                                item.getProduct().getId(),
                                order.getBranch().getId()
                        )
                        .orElseThrow(InventoryNotFoundException::new);

                if (inventory.getQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Insufficient inventory for product: " + item.getProduct().getName());
                }

                inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            }

            // Complete the order
            order.setStatus(OrderStatus.COMPLETED);

            paymentRepository.save(payment);
            orderRepository.save(order);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to verify Razorpay payment.", e);
        }
    }
}