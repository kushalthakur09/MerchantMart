package com.main.MerchantMart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.MerchantMart.domain.OrderStatus;
import com.main.MerchantMart.domain.PaymentType;
import com.main.MerchantMart.payload.dto.OrderDto;
import com.main.MerchantMart.payload.dto.OrderItemDto;
import com.main.MerchantMart.payload.response.ApiResponse;
import com.main.MerchantMart.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(OrderControllerTest.TestSecurityConfig.class)
public class OrderControllerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private OrderDto orderDto;

    private List<OrderDto> orderDtos;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    public void  setUp(){
        orderDto=OrderDto.builder()
                .id(1L)
                .totalAmount(500.0)
                .brandId(1L)
                .customerId(1L)
                .paymentType(PaymentType.CASH)
                .items(List.of())
                .build();
        OrderDto temp1=OrderDto.builder()
                .id(1L)
                .totalAmount(500.0)
                .brandId(1L)
                .customerId(1L)
                .paymentType(PaymentType.CASH)
                .items(List.of())
                .build();

        OrderDto temp2=OrderDto.builder()
                .id(1L)
                .totalAmount(500.0)
                .brandId(1L)
                .customerId(1L)
                .paymentType(PaymentType.CASH)
                .items(List.of())
                .build();

        orderDtos =new ArrayList<>();
        orderDtos.add(temp1);
        orderDtos.add(temp2);
    }

    @Test
    public void create_shouldReturn201_whenOrderIsValid() throws Exception {
        when(orderService.createOrder(any(OrderDto.class))).thenReturn(orderDto);

        mockMvc.perform(post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void getOrderById_shouldReturn200_whenOrderExists() throws Exception {
        when(orderService.getOrderById(anyLong())).thenReturn(orderDto);
        mockMvc.perform(get("/api/order/1")).andExpect(status().isOk());
    }

    @Test
    public void delete_shouldReturn200_whenOrderExists() throws Exception {
        doNothing().when(orderService).deleteOrder(anyLong());
        mockMvc.perform(delete("/api/order/1")).andExpect(status().isOk());
    }

    @Test
    public void getOrdersByBranch_shouldReturn200() throws Exception {
        when(orderService.getOrdersByBranch(anyLong(),anyLong(),anyLong(),any(PaymentType.class),any(OrderStatus.class))).thenReturn(orderDtos);
        mockMvc.perform(get("/api/order/branch/1?customerId=1&cashierId=1&paymentType=CASH&orderStatus=PENDING")).andExpect(status().isOk());
    }

    @Test
    public void getTodayOrdersByBranch_shouldReturn200() throws Exception {
        when(orderService.getTodayOrdersByBranch(anyLong())).thenReturn(orderDtos);
        mockMvc.perform(get("/api/order/branch/1")).andExpect(status().isOk());
    }

    @Test
    public void getOrderByCashier_shouldReturn200() throws Exception {
        when(orderService.getOrderByCashier(anyLong())).thenReturn(orderDtos);
        mockMvc.perform(get("/api/order/cashier/1")).andExpect(status().isOk());
    }

    @Test
    public void getOrdersByCustomerId_shouldReturn200() throws Exception {
        when(orderService.getOrdersByCustomerId(anyLong())).thenReturn(orderDtos);
        mockMvc.perform(get("/api/order/customer/1")).andExpect(status().isOk());
    }
}
