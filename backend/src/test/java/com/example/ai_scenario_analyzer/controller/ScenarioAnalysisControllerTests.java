package com.example.ai_scenario_analyzer.controller;

import com.example.ai_scenario_analyzer.model.ScenarioAnalysisRequest;
import com.example.ai_scenario_analyzer.model.ScenarioAnalysisResponse;
import com.example.ai_scenario_analyzer.service.AiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScenarioAnalysisController.class)
public class ScenarioAnalysisControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiService aiService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAnalyzeScenario() throws Exception {
        // Prepare a dummy request payload.
        ScenarioAnalysisRequest request = new ScenarioAnalysisRequest();
        request.setScenario("Our team has a new client project with a tight deadline.");
        request.setConstraints(List.of("Budget: $10,000", "Deadline: 6 weeks", "Team of 3 developers"));

        // Prepare a dummy response.
        ScenarioAnalysisResponse dummyResponse = new ScenarioAnalysisResponse();
        dummyResponse.setScenarioSummary("Dummy summary");
        dummyResponse.setPotentialPitfalls(List.of("Dummy pitfall"));
        dummyResponse.setProposedStrategies(List.of("Dummy strategy"));
        dummyResponse.setRecommendedResources(List.of("Dummy resource"));
        dummyResponse.setDisclaimer("Dummy disclaimer");

        // Configure the mock to return the dummy response.
        when(aiService.generateAnalysis(any(ScenarioAnalysisRequest.class))).thenReturn(dummyResponse);

        // Perform the POST request and verify the response.
        mockMvc.perform(post("/api/analyze-scenario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.scenarioSummary").value("Dummy summary"))
            .andExpect(jsonPath("$.potentialPitfalls[0]").value("Dummy pitfall"))
            .andExpect(jsonPath("$.proposedStrategies[0]").value("Dummy strategy"))
            .andExpect(jsonPath("$.recommendedResources[0]").value("Dummy resource"))
            .andExpect(jsonPath("$.disclaimer").value("Dummy disclaimer"));
    }
}
