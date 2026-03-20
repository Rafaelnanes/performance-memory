package com.example.demo;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class SampleSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("text/plain");

    private final ScenarioBuilder scn = scenario("Sample Endpoint Load Test")
            .exec(http("GET /sample").get("/sample").check(status().is(200)));

    {
        setUp(
                scn.injectOpen(atOnceUsers(100))
        )
        .protocols(httpProtocol)
        .assertions(
                global().responseTime().mean().lt(1000),
                global().successfulRequests().percent().gt(95.0)
        );
    }
}
