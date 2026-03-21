# Memory Monitoring & Profiling Learning Roadmap

## Context
Goal: understand how to detect, track, and diagnose memory problems in a Java/Spring Boot application —
specifically: knowing when memory usage exceeds expectations, and identifying which features/components are responsible.

Each topic below is a standalone use case to implement and study.

---

## Suggested Implementation Order
**1 → 3 → 6 → 7 → 4 → 5 → 2 → 8 → 9 → 10**

Start with observable basics (Actuator), move to leak patterns, then deep profiling.

---

## Topics

### 1. JVM Memory Model Fundamentals
Understand the regions before measuring them.
- Heap (Young Gen / Old Gen), Stack, Metaspace, Off-Heap
- How objects are allocated and promoted
- **Use case:** log `-XX:+PrintGCDetails` startup flags and observe region sizes at startup vs under load

---

### 2. JVM Flags for Memory Limits & Diagnostics
- `-Xms`, `-Xmx`, `-XX:MaxMetaspaceSize`
- `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./dumps`
- **Use case:** intentionally trigger an OOM with a bounded heap and capture the dump

---

### 3. Spring Boot Actuator — Live Memory Metrics
- Expose `/actuator/metrics/jvm.memory.used`, `/jvm.memory.max`, `/jvm.gc.*`
- **Use case:** create an endpoint that allocates objects and poll Actuator before/during/after to observe heap changes
- File: `src/main/resources/application.properties` (enable actuator endpoints)

---

### 4. Micrometer + Prometheus + Grafana
- Wire Micrometer (already bundled in Spring Boot Actuator) to a Prometheus scrape target
- Visualize heap, GC pause time, thread count over time on a Grafana dashboard
- **Use case:** run a Gatling load test and observe memory pressure graphs in real time

---

### 5. Custom Memory Metrics with Micrometer
- Register a `Gauge` that tracks a specific in-memory structure (e.g., a cache, a queue)
- **Use case:** add a `ConcurrentHashMap` cache to `ControllerSample`, register its size as a gauge, watch it grow under load

---

### 6. Detecting Memory Leaks — Patterns
Study common leak patterns and reproduce each:
- **Static collections** — objects added but never removed
- **ThreadLocal misuse** — values not removed after request, leaks in thread pools
- **Event listeners / callbacks** — registered but never deregistered
- **Unclosed streams / connections** — held references prevent GC
- **Use case:** implement one leak per pattern, confirm via Actuator metrics that heap grows monotonically

---

### 7. Heap Dump Analysis with VisualVM / Eclipse MAT
- Trigger a heap dump manually (`jcmd <pid> GC.heap_dump`) or via OOM flag
- Load in VisualVM or Eclipse MAT, find the dominator tree and retained heap
- **Use case:** introduce a static list leak, dump heap, identify the culprit class

---

### 8. GC Tuning & Collector Comparison
- Compare G1GC (default) vs ZGC vs Shenandoah under the same Gatling load
- Metrics to observe: GC pause time, throughput, heap reclaimed
- **Use case:** run `./gradlew gatlingRun` with each `-XX:+UseG1GC` / `-XX:+UseZGC`, compare reports

---

### 9. Off-Heap & Direct Memory
- `ByteBuffer.allocateDirect()` allocates outside the heap — invisible to standard heap metrics
- `-XX:MaxDirectMemorySize` limits it
- **Use case:** allocate large direct buffers in an endpoint, show they don't appear in heap metrics but do appear in system memory

---

### 10. Async-Profiler — Allocation Profiling
- Async-profiler in `alloc` mode shows which code paths allocate the most memory per second
- **Use case:** profile a Gatling run and generate a flame graph, identify the hottest allocation site

---

## Relevant Files
| File | Purpose |
|------|---------|

| `src/main/java/com/example/demo/ControllerSample.java` | Add memory-stressing endpoints per topic |
| `src/main/resources/application.properties` | Actuator + Micrometer config |
| `src/gatling/java/com/example/demo/SampleSimulation.java` | Load generation for all scenarios |
| `build.gradle` | Add dependencies (e.g. `micrometer-registry-prometheus`) as needed per topic |
