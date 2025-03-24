package com.example.ai_scenario_analyzer.controller;

import com.example.ai_scenario_analyzer.model.ScenarioAnalysisRequest;
import com.example.ai_scenario_analyzer.model.ScenarioAnalysisResponse;
import com.example.ai_scenario_analyzer.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ScenarioAnalysisController {

    @Autowired
    private AiService aiService;

    @PostMapping("/analyze-scenario")
    public ResponseEntity<ScenarioAnalysisResponse> analyzeScenario(@RequestBody ScenarioAnalysisRequest request) {
        ScenarioAnalysisResponse response = aiService.generateAnalysis(request);
        return ResponseEntity.ok(response);
    }
}
