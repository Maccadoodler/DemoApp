package com.cowparlour.cp.task.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.cowparlour.cp.task.dto.Average;
import com.cowparlour.cp.task.dto.TaskTime;
import com.cowparlour.cp.task.service.ServiceFailure;
import com.cowparlour.cp.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.Optional;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void testTaskRecord() throws Exception {
        TaskTime input = new TaskTime("jmc", BigInteger.TEN);

        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/task/duration")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "test-user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Task jmc processed successfully."));
    }

    void testTaskRecord_NullData() throws Exception {
        TaskTime input = new TaskTime(null, BigInteger.TEN);


        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/task/duration")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "test-user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Payload"));
    }


    @Test
    void testTaskRecord_Runtime() throws Exception {

        TaskTime input = new TaskTime("jmc", BigInteger.TEN);
        doThrow(new RuntimeException("BAD")).when(taskService).updateTask(any());
        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/task/duration")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "test-user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Server error"));
    }

    @Test
    void testTaskRecord_NoService() throws Exception {
        TaskTime input = new TaskTime("jmc", BigInteger.TEN);
        doThrow(new ServiceFailure("DB unavailable")).when(taskService).updateTask(any());

        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/task/duration")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "test-user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
    }


    @Test
    void testTaskAverage() throws Exception {
        Optional<Average> average = Optional.of(new Average("John", BigInteger.valueOf(120)));

        when(taskService.getAverage("John")).thenReturn(average);
        mockMvc.perform(get("/task/average/John")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "test-user")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task").value("John"))
                .andExpect(jsonPath("$.averageDuration").value(120));

    }

    @Test
    void testTaskAverage_NoData() throws Exception {
        Optional<Average> average = Optional.empty();

        when(taskService.getAverage("John")).thenReturn(average);
        mockMvc.perform(get("/task/average/John")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "test-user")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

    }

    @Test
    void testTaskAverage_Runtime() throws Exception {

        when(taskService.getAverage(any())).thenThrow(new RuntimeException("BAD"));
        mockMvc.perform(get("/task/average/John")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "test-user")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Server error"));
    }


    @Test
    void testTaskAverage_NoService() throws Exception {

        when(taskService.getAverage(any())).thenThrow(new ServiceFailure("DB unavailable"));

        mockMvc.perform(get("/task/average/John")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "test-user")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("DB unavailable"));
    }

}
