/*
 * (C): cowparlour.com  2025
 */
package com.cowparlour.cp.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cowparlour.cp.task.dto.TaskTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;


/**
 * Unit test to run against a running system with database.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AveragesApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void testAverage_NoRecord() throws Exception {
		mockMvc.perform(get("/time/average/John"))
				.andExpect(status().isNotFound());
	}

	@Test
	void testAverage_TwoRecord() throws Exception {

		TaskTime input = new TaskTime("jmc", BigInteger.TEN);
		TaskTime inputTwo = new TaskTime("jmc", BigInteger.valueOf(20L));

		ObjectMapper objectMapper = new ObjectMapper();

		String json = objectMapper.writeValueAsString(input);
		String jsonTwo = objectMapper.writeValueAsString(inputTwo);

		mockMvc.perform(post("/time/record")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(content().string("Task jmc processed successfully."));

		mockMvc.perform(post("/time/record")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonTwo))
				.andExpect(status().isOk())
				.andExpect(content().string("Task jmc processed successfully."));

		mockMvc.perform(get("/time/average/jmc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.task").value("jmc"))
				.andExpect(jsonPath("$.averageDuration").value(BigInteger.valueOf(15L)));
	}

}
