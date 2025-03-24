# AI Scenario Analyzer

A full-stack web application that enables users to enter a scenario description and key constraints, then generates an AI-powered analysis that includes a summary, potential pitfalls, proposed strategies, recommended resources, and a disclaimer.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Setup and Running the Project](#setup-and-running-the-project)
  - [Backend (Spring Boot)](#backend-spring-boot)
  - [Frontend (React)](#frontend-react)
- [AI Integration and Prompt Structure](#ai-integration-and-prompt-structure)
- [Testing](#testing)
- [Deployment](#deployment)
- [Notes and Considerations](#notes-and-considerations)
- [License](#license)

## Overview

The AI Scenario Analyzer is a full-stack application that allows users to input a scenario (a descriptive paragraph outlining a problem or challenge) along with key constraints (e.g., budget, timeline, resources). The backend leverages an AI (via the OpenAI API) to generate a structured analysis of the scenario. The AI output is parsed into the following sections:

- **Scenario Summary:** A concise restatement of the scenario.
- **Potential Pitfalls:** A list of possible issues or risks.
- **Proposed Strategies:** Recommended approaches or solutions.
- **Recommended Resources:** Tools, frameworks, or references.
- **Disclaimer:** A note on AI limitations or the need for expert consultation.

## Features

- **Responsive Frontend:** Built with React and Bootstrap for a modern, responsive UI.
- **Structured AI Response:** The backend builds a detailed prompt for GPT-4o, then parses the structured response into clearly defined sections.
- **Error Handling & Logging:** Robust error handling in the backend with meaningful fallback responses.
- **Unit Testing:** JUnit tests for the controller layer with mocked AI service calls.
- **Easy Deployment:** Separate instructions for building and running both backend and frontend components.

## Technologies Used

- **Backend:** Java, Spring Boot, Maven
- **Frontend:** React, Bootstrap, react-markdown
- **AI Integration:** OpenAI API (GPT-4o)
- **Testing:** JUnit, Spring Boot Test, MockMvc

## Project Structure

```
ai-scenario-analyzer/
├── backend/                      # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/ai_scenario_analyzer/
│   │   │   │   ├── controller/
│   │   │   │   ├── model/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
├── frontend/                     # React application
│   ├── public/
│   ├── src/
│   │   ├── App.js
│   │   └── index.js
│   ├── package.json
│   └── ...                       # Other React config and assets
└── .gitignore
```

## Setup and Running the Project

### Backend (Spring Boot)

1. **Navigate to the backend directory:**

```bash
cd backend
```

2. **Build and Run the Application:**

```bash
./mvnw spring-boot:run
```

The application will start and expose the endpoint at:

```
http://localhost:8080/api/analyze-scenario
```

**Environment Variables:**

Currently, the OpenAI API key is hard-coded in `application.properties`. _(For production, consider using environment variables for better security.)_

### Frontend (React)

1. **Navigate to the frontend directory:**

```bash
cd frontend
```

2. **Install Dependencies:**

```bash
npm install
```

3. **Start the Development Server:**

```bash
npm start
```

The React app will run on `http://localhost:3000`.

Ensure your frontend is configured to point to `http://localhost:8080/api/analyze-scenario` for API calls (configured in `App.js`).

## AI Integration and Prompt Structure

**Prompt Structure:**

The backend builds a prompt instructing GPT-4o to output a structured analysis:

```markdown
### Summary:

[A brief summary of the scenario in 1-2 sentences]

### Potential Pitfalls:

- [List potential pitfalls, each on a new line]

### Proposed Strategies:

- [List proposed strategies, each on a new line]

### Recommended Resources:

- [List recommended resources, each on a new line]

### Disclaimer:

[A one-sentence disclaimer about AI limitations or the need for expert consultation]

Scenario: [user scenario]
Constraints: [list of constraints]
```

**Response Parsing:**

The backend parses the AI response based on defined delimiters and maps the output to JSON fields.

**Modifying the Prompt:**

To adjust the AI behavior, update the `buildPrompt()` method in `AiService.java`.

## Testing

**Unit Tests (Backend):**

JUnit tests for the controller are located at:

```
backend/src/test/java/com/example/ai_scenario_analyzer/controller/ScenarioAnalysisControllerTests.java
```

To run tests:

```bash
./mvnw test
```

## Deployment

### Backend Deployment

Build and run the backend JAR:

```bash
./mvnw clean package
java -jar target/ai-scenario-analyzer-0.0.1-SNAPSHOT.jar
```

Ensure environment variables (e.g., OpenAI API key) are set appropriately.

### Frontend Deployment

Create a production build:

```bash
npm run build
```

Serve the build with a static server or deploy to platforms like Netlify, Vercel, or GitHub Pages.

## Notes and Considerations

- **API Key Visibility:** Use environment variables instead of hard-coding API keys for security.
- **CORS Configuration:** Adjust `@CrossOrigin` settings if the frontend URL changes.
- **Error Handling & Logging:** SLF4J is used for logging; fallback responses ensure graceful API responses.
- **Node.js Version:** The frontend runs on Node.js v18, resolving dependency warnings.

## License

This project is licensed under the MIT License.
