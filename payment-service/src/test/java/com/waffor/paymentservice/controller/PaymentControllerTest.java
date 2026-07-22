package com.waffor.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waffor.paymentservice.dto.PaymentRequest;
import com.waffor.paymentservice.entity.Payment;
import com.waffor.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private PaymentRepository paymentRepository;

    @Test
    public void testProcessPayment() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("15.99"));

        Payment mockPayment = Payment.builder()
                .id(1L)
                .orderId(1L)
                .status("SUCCESS")
                .build();

        Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").isString()); // Since it's random SUCCESS/FAILED
    }
}
