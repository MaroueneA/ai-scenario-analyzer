package com.example.ai_scenario_analyzer.model;

import java.util.List;

public class ScenarioAnalysisRequest {
    private String scenario;
    private List<String> constraints;

    // Default constructor
    public ScenarioAnalysisRequest() {
    }

    // Parameterized constructor
    public ScenarioAnalysisRequest(String scenario, List<String> constraints) {
        this.scenario = scenario;
        this.constraints = constraints;
    }

    // Getters and Setters
    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public List<String> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<String> constraints) {
        this.constraints = constraints;
    }
}
