package com.waffor.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waffor.orderservice.dto.OrderRequest;
import com.waffor.orderservice.entity.Order;
import com.waffor.orderservice.entity.OrderStatus;
import com.waffor.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private JmsTemplate jmsTemplate;

    @Test
    public void testCreateOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerName("John Doe");
        request.setItem("Pizza");
        request.setAmount(new BigDecimal("15.99"));

        Order mockSavedOrder = Order.builder()
                .id(1L)
                .customerName("John Doe")
                .item("Pizza")
                .amount(new BigDecimal("15.99"))
                .status(OrderStatus.PLACED)
                .build();

        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(mockSavedOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PLACED"));
    }
}
