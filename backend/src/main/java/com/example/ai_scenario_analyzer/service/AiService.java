package com.example.ai_scenario_analyzer.service;

import com.example.ai_scenario_analyzer.model.ScenarioAnalysisRequest;
import com.example.ai_scenario_analyzer.model.ScenarioAnalysisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    public ScenarioAnalysisResponse generateAnalysis(ScenarioAnalysisRequest request) {
        // Build the prompt using the request details.
        String prompt = buildPrompt(request);

        // Create the payload for the GPT-4o API using the Chat Completions format.
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "gpt-4o");  // Use GPT‑4o model

        // Create the messages list required by the Chat API.
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        payload.put("messages", messages);

        // Other parameters
        payload.put("max_tokens", 500);
        payload.put("temperature", 0.7);

        // Set up HTTP headers.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, headers);

        // Call the OpenAI API.
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(openaiApiUrl, httpEntity, Map.class);
        Map responseBody = responseEntity.getBody();

        // Extract the text from the API response.
        String aiText = "";
        if (responseBody != null && responseBody.containsKey("choices")) {
            List choices = (List) responseBody.get("choices");
            if (!choices.isEmpty()) {
                Map firstChoice = (Map) choices.get(0);
                // The chat endpoint returns a nested structure: message -> content
                Map message = (Map) firstChoice.get("message");
                if (message != null) {
                    aiText = (String) message.get("content");
                }
            }
        }
        
        // Parse the structured AI response to extract individual sections.
        ScenarioAnalysisResponse analysisResponse = parseAiResponse(aiText);
        return analysisResponse;
    }

    private String buildPrompt(ScenarioAnalysisRequest request) {
        return "Given the following scenario and constraints, generate a structured analysis using the following format:"
                +"Ensure that each section is complete and ends with proper punctuation.\n\n"
                + "### Summary:\n"
                + "[A brief summary of the scenario in 1-2 sentences]\n\n"
                + "### Potential Pitfalls:\n"
                + "- [List potential pitfalls, each on a new line]\n\n"
                + "### Proposed Strategies:\n"
                + "- [List proposed strategies, each on a new line]\n\n"
                + "### Recommended Resources:\n"
                + "- [List recommended resources, each on a new line]\n\n"
                + "### Disclaimer:\n"
                + "[A one-sentence disclaimer about AI limitations or the need for expert consultation]\n\n"
                + "Scenario: " + request.getScenario() + "\n"
                + "Constraints: " + String.join(", ", request.getConstraints());
    }
    
    private ScenarioAnalysisResponse parseAiResponse(String aiText) {
        // Define the section delimiters.
        String summaryDelimiter = "### Summary:";
        String pitfallsDelimiter = "### Potential Pitfalls:";
        String strategiesDelimiter = "### Proposed Strategies:";
        String resourcesDelimiter = "### Recommended Resources:";
        String disclaimerDelimiter = "### Disclaimer:";

        try {
            int summaryStart = aiText.indexOf(summaryDelimiter);
            int pitfallsStart = aiText.indexOf(pitfallsDelimiter);
            int strategiesStart = aiText.indexOf(strategiesDelimiter);
            int resourcesStart = aiText.indexOf(resourcesDelimiter);
            int disclaimerStart = aiText.indexOf(disclaimerDelimiter);

            if (summaryStart == -1 || pitfallsStart == -1 || strategiesStart == -1 ||
                resourcesStart == -1 || disclaimerStart == -1) {
                // If any delimiter is missing, fallback to a default response.
                ScenarioAnalysisResponse defaultResponse = new ScenarioAnalysisResponse();
                defaultResponse.setScenarioSummary(aiText);
                defaultResponse.setPotentialPitfalls(Collections.singletonList("Could not parse pitfalls."));
                defaultResponse.setProposedStrategies(Collections.singletonList("Could not parse strategies."));
                defaultResponse.setRecommendedResources(Collections.singletonList("Could not parse resources."));
                defaultResponse.setDisclaimer("Could not parse disclaimer.");
                return defaultResponse;
            }

            // Extract each section by substring.
            String summary = aiText.substring(summaryStart + summaryDelimiter.length(), pitfallsStart).trim();
            String pitfallsText = aiText.substring(pitfallsStart + pitfallsDelimiter.length(), strategiesStart).trim();
            String strategiesText = aiText.substring(strategiesStart + strategiesDelimiter.length(), resourcesStart).trim();
            String resourcesText = aiText.substring(resourcesStart + resourcesDelimiter.length(), disclaimerStart).trim();
            String disclaimer = aiText.substring(disclaimerStart + disclaimerDelimiter.length()).trim();

            // Process list sections by splitting on line breaks.
            List<String> pitfalls = Arrays.stream(pitfallsText.split("\n"))
                                          .map(String::trim)
                                          .filter(s -> !s.isEmpty() && s.startsWith("-"))
                                          .map(s -> s.replaceFirst("^-\\s*", ""))
                                          .collect(Collectors.toList());

            List<String> strategies = Arrays.stream(strategiesText.split("\n"))
                                            .map(String::trim)
                                            .filter(s -> !s.isEmpty() && s.startsWith("-"))
                                            .map(s -> s.replaceFirst("^-\\s*", ""))
                                            .collect(Collectors.toList());

            List<String> resources = Arrays.stream(resourcesText.split("\n"))
                                           .map(String::trim)
                                           .filter(s -> !s.isEmpty() && s.startsWith("-"))
                                           .map(s -> s.replaceFirst("^-\\s*", ""))
                                           .collect(Collectors.toList());

            ScenarioAnalysisResponse response = new ScenarioAnalysisResponse();
            response.setScenarioSummary(summary);
            response.setPotentialPitfalls(pitfalls);
            response.setProposedStrategies(strategies);
            response.setRecommendedResources(resources);
            response.setDisclaimer(disclaimer);

            return response;
        } catch (Exception e) {
            // Fallback in case of parsing errors.
            ScenarioAnalysisResponse fallbackResponse = new ScenarioAnalysisResponse();
            fallbackResponse.setScenarioSummary(aiText);
            fallbackResponse.setPotentialPitfalls(Collections.singletonList("Error parsing pitfalls."));
            fallbackResponse.setProposedStrategies(Collections.singletonList("Error parsing strategies."));
            fallbackResponse.setRecommendedResources(Collections.singletonList("Error parsing resources."));
            fallbackResponse.setDisclaimer("Error parsing disclaimer.");
            return fallbackResponse;
        }
    }
}
