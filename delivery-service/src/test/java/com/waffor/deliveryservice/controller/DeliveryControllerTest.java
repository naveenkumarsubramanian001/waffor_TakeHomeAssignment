package com.waffor.deliveryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waffor.deliveryservice.dto.DeliveryRequest;
import com.waffor.deliveryservice.entity.Delivery;
import com.waffor.deliveryservice.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryController.class)
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private DeliveryRepository deliveryRepository;

    @Test
    public void testAssignDelivery() throws Exception {
        DeliveryRequest request = new DeliveryRequest();
        request.setOrderId(1L);

        Delivery mockDelivery = Delivery.builder()
                .id(1L)
                .orderId(1L)
                .driverName("Driver John Doe")
                .status("DELIVERED")
                .build();

        Mockito.when(deliveryRepository.save(any(Delivery.class))).thenReturn(mockDelivery);

        mockMvc.perform(post("/api/delivery/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }
}
