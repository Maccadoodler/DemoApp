package com.cowparlour.cp.task.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cowparlour.cp.task.dto.Average;
import com.cowparlour.cp.task.dto.TaskTime;
import com.cowparlour.cp.task.service.ServiceFailure;
import com.cowparlour.cp.task.service.TimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.Optional;

@WebMvcTest(TimeController.class)
public class TimeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TimeService timeService;

    @Test
    void testTaskRecord() throws Exception {
        TaskTime input = new TaskTime("jmc", BigInteger.TEN);


        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/time/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Task jmc processed successfully."));
    }


    @Test
    void testTaskRecord_Runtime() throws Exception {

        TaskTime input = new TaskTime("jmc", BigInteger.TEN);
        doThrow(new RuntimeException("BAD")).when(timeService).updateTask(any());
        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/time/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Server error"));
    }

    @Test
    void testTaskRecord_NoService() throws Exception {
        TaskTime input = new TaskTime("jmc", BigInteger.TEN);
        doThrow(new ServiceFailure("DB unavailable")).when(timeService).updateTask(any());

        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/time/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
    }


    @Test
    void testTaskAverage() throws Exception {
        Optional<Average> average = Optional.of(new Average("John", BigInteger.valueOf(120)));

        when(timeService.getAverage("John")).thenReturn(average);
        mockMvc.perform(get("/time/average/John")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task").value("John"))
                .andExpect(jsonPath("$.averageDuration").value(120));

    }

    @Test
    void testTaskAverage_NoData() throws Exception {
        Optional<Average> average = Optional.empty();

        when(timeService.getAverage("John")).thenReturn(average);
        mockMvc.perform(get("/time/average/John")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

    }

    @Test
    void testTaskAverage_Runtime() throws Exception {

        when(timeService.getAverage(any())).thenThrow(new RuntimeException("BAD"));
        mockMvc.perform(get("/time/average/John")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Server error"));
    }


    @Test
    void testTaskAverage_NoService() throws Exception {

        when(timeService.getAverage(any())).thenThrow(new ServiceFailure("DB unavailable"));

        mockMvc.perform(get("/time/average/John")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("DB unavailable"));
    }

}
