package com.example.demo;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class SampleSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("text/plain");

    private final ScenarioBuilder scn = scenario("Executor Benchmark")
            .exec(http("single-thread-executor").get("/sample/single-thread-executor").check(status().is(200)))
            .exec(http("cached-thread-pool").get("/sample/cached-thread-pool").check(status().is(200)))
            .exec(http("fixed-thread-pool").get("/sample/fixed-thread-pool").check(status().is(200)))
            .exec(http("scheduled-thread-pool").get("/sample/scheduled-thread-pool").check(status().is(200)))
            .exec(http("thread-pool-executor").get("/sample/thread-pool-executor").check(status().is(200)));

    {
        setUp(
                scn.injectOpen(constantUsersPerSec(20).during(10))
        )
        .protocols(httpProtocol)
        .assertions(
                global().responseTime().mean().lt(1000),
                global().successfulRequests().percent().gt(95.0)
        );
    }
}