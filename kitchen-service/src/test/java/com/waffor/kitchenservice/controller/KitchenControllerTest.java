package com.waffor.kitchenservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waffor.kitchenservice.dto.KitchenRequest;
import com.waffor.kitchenservice.entity.KitchenTicket;
import com.waffor.kitchenservice.repository.KitchenTicketRepository;
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

@WebMvcTest(KitchenController.class)
public class KitchenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private KitchenTicketRepository kitchenTicketRepository;

    @Test
    public void testPrepareFood() throws Exception {
        KitchenRequest request = new KitchenRequest();
        request.setOrderId(1L);
        request.setItem("Burger");

        KitchenTicket mockTicket = KitchenTicket.builder()
                .id(1L)
                .orderId(1L)
                .status("READY")
                .build();

        Mockito.when(kitchenTicketRepository.save(any(KitchenTicket.class))).thenReturn(mockTicket);

        mockMvc.perform(post("/api/kitchen/prepare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("READY"));
    }
}
