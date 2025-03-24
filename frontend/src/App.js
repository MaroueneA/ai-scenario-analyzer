import React, { useState } from "react";
import ReactMarkdown from "react-markdown";
import {
  Container,
  Row,
  Col,
  Form,
  Button,
  Spinner,
  Alert,
} from "react-bootstrap";

function App() {
  // States for form input and API response.
  const [scenario, setScenario] = useState("");
  const [constraints, setConstraints] = useState([""]);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Update a constraint by index.
  const handleConstraintChange = (index, value) => {
    const newConstraints = [...constraints];
    newConstraints[index] = value;
    setConstraints(newConstraints);
  };

  // Add a new empty constraint.
  const addConstraintField = () => {
    setConstraints([...constraints, ""]);
  };

  // Remove a constraint field.
  const removeConstraintField = (index) => {
    setConstraints(constraints.filter((_, i) => i !== index));
  };

  // Handle form submission.
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setResult(null);

    // Prepare the payload.
    const payload = {
      scenario: scenario,
      constraints: constraints.filter((c) => c.trim() !== ""),
    };

    try {
      const response = await fetch(
        "http://localhost:8080/api/analyze-scenario",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        }
      );

      if (!response.ok) {
        throw new Error(`API error: ${response.statusText}`);
      }
      const data = await response.json();
      setResult(data);
    } catch (err) {
      console.error("Error:", err);
      setError(err.message);
    }
    setLoading(false);
  };

  return (
    <Container className="my-4">
      <Row>
        <Col md={6}>
          <h2>Scenario Analysis</h2>
          <Form onSubmit={handleSubmit}>
            <Form.Group controlId="scenario">
              <Form.Label>Scenario</Form.Label>
              <Form.Control
                as="textarea"
                rows={4}
                value={scenario}
                onChange={(e) => setScenario(e.target.value)}
                placeholder="Describe your scenario..."
                required
              />
            </Form.Group>
            <Form.Label className="mt-3">Constraints</Form.Label>
            {constraints.map((constraint, index) => (
              <Form.Group
                key={index}
                className="d-flex align-items-center mb-2"
              >
                <Form.Control
                  type="text"
                  value={constraint}
                  onChange={(e) =>
                    handleConstraintChange(index, e.target.value)
                  }
                  placeholder="e.g., Budget: $10,000"
                  required
                />
                <Button
                  variant="danger"
                  size="sm"
                  className="ms-2"
                  onClick={() => removeConstraintField(index)}
                >
                  Remove
                </Button>
              </Form.Group>
            ))}
            <Button variant="secondary" size="sm" onClick={addConstraintField}>
              Add Constraint
            </Button>
            <div className="mt-3">
              <Button variant="primary" type="submit" disabled={loading}>
                {loading ? (
                  <>
                    <Spinner
                      as="span"
                      animation="border"
                      size="sm"
                      role="status"
                      aria-hidden="true"
                    />{" "}
                    Loading...
                  </>
                ) : (
                  "Analyze Scenario"
                )}
              </Button>
            </div>
          </Form>
          {error && (
            <Alert variant="danger" className="mt-3">
              {error}
            </Alert>
          )}
        </Col>
        <Col md={6}>
          <h2>Analysis Result</h2>
          {result ? (
            <div>
              <h4>Summary</h4>
              <ReactMarkdown>{result.scenarioSummary}</ReactMarkdown>

              <h4>Potential Pitfalls</h4>
              <ul>
                {result.potentialPitfalls.map((pitfall, idx) => (
                  <li key={idx}>
                    <ReactMarkdown>{pitfall}</ReactMarkdown>
                  </li>
                ))}
              </ul>

              <h4>Proposed Strategies</h4>
              <ul>
                {result.proposedStrategies.map((strategy, idx) => (
                  <li key={idx}>
                    <ReactMarkdown>{strategy}</ReactMarkdown>
                  </li>
                ))}
              </ul>

              <h4>Recommended Resources</h4>
              <ul>
                {result.recommendedResources.map((resource, idx) => (
                  <li key={idx}>
                    <ReactMarkdown>{resource}</ReactMarkdown>
                  </li>
                ))}
              </ul>

              <h4>Disclaimer</h4>
              <ReactMarkdown>{result.disclaimer}</ReactMarkdown>
            </div>
          ) : (
            <p>No analysis yet.</p>
          )}
        </Col>
      </Row>
    </Container>
  );
}

export default App;
