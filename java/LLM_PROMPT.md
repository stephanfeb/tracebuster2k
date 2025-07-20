# JTracer

JTracer is a tool for creating test-driven integrated debugging sessions for JVM applications.

LLM Agents are increasingly capable of understanding, writing, and debugging code. However, they lack the ability to "see" how code actually executes or inspect runtime state. JTracer bridges this gap by providing:

1. **Programmatic Debugging** - LLM Agents can write tests that set breakpoints, inspect variables, and analyze execution flow, just like a human developer using an IDE debugger
2. **Structured Insights** - All debugging data is returned in LLM-friendly formats, making it easy for agents to reason about program state and behavior
3. **Non-Intrusive Analysis** - The sidecar approach means agents can deeply analyze code without modifying the production code or its tests

### How It Works

The architecture consists of three key components:

1. **Production Code** - Your actual implementation (e.g., a Calculator class)
2. **Production Tests** - Standard tests for your implementation (e.g., TestCalculator)
3. **Tracer Tests** - Special tests written by LLM Agents using JTracer to debug and analyze the production code by running the production tests

This sidecar approach means your debugging logic remains separate from both your production code and its tests, allowing for clean separation of concerns:

```
┌─────────────────┐     ┌─────────────────┐
│  Production     │     │   Production    │
│     Code        │◄────│     Tests       │
└─────────────────┘     └─────────────────┘
         ▲                     ▲
         │                     │
         └──────────┬──────────┘
                    │
            ┌───────────────┐
            │   JTracer     │
            │    Tests      │
            └───────────────┘
            (Written by LLM Agents)
```

### Use Cases

LLM Agents can use JTracer to:
- Generate comprehensive execution traces for understanding complex codebases
- Create detailed variable state tables at critical points in execution
- Verify assumptions about code behavior through runtime inspection
- Debug issues by analyzing actual execution paths and variable values
- Build rich documentation based on real runtime behavior
- Generate test cases based on observed program state

## Implementation Guide

Based on our implementation experience, here are key considerations for setting up JTracer:

### Project Structure
```
src/
├── main/java/com/example/
│   ├── jtracer/                    # Debugger core
│   │   ├── JTracerSession.java     # Main debugger class
│   │   └── DebugEvent.java         # Event representation
│   ├── YourMainClass.java          # Class to be debugged
│   └── TracerDemo.java             # Standalone debugger runner
```

### Key Implementation Learnings

1. **Separate Process Architecture**
   - The debugger must run in a separate VM from the debugged code
   - Don't try to run the debugger in the same JVM or test environment
   - Use a standalone program rather than JUnit tests for debugging sessions

2. **Class Loading Management**
   - Classes aren't available immediately after VM creation
   - Implement a two-phase breakpoint system:
     - Queue breakpoints when requested
     - Set them when receiving ClassPrepareEvent events
   - Try immediate breakpoint setting if the class is already loaded

3. **Classpath Handling**
   - Ensure the debugger VM has access to the same classpath as the main application
   - Set explicit classpath in VM arguments:
   ```java
   args.get("options").setValue("-cp " + System.getProperty("java.class.path"));
   ```

4. **Event Handling**
   - Handle multiple event types (ClassPrepare, ThreadStart, Breakpoint, etc.)
   - Respect event ordering - classes must be prepared before setting breakpoints
   - Properly handle VM lifecycle events (death, disconnect)

5. **Error Management**
   - Expect and handle VMDisconnectException during shutdown
   - Implement robust error handling with detailed logging
   - Use structured event representation for errors

### Setup Instructions

1. **Dependencies (build.gradle)**:
```gradle
dependencies {
    implementation 'com.google.gson:gson:2.10.1'  // For JSON output
}

run {
    classpath = sourceSets.main.runtimeClasspath  // Ensure proper classpath
}
```

2. **Basic Usage**:
```java
try (JTracerSession tracer = new JTracerSession("your.MainClass")) {
    // Set breakpoints
    tracer.setBreakpoint("your.TargetClass", lineNumber);
    
    // Run and collect events
    tracer.run();
    
    // Get results as JSON
    String debugEvents = tracer.getEventsAsJson();
}
```

3. **Testing Strategy**:
   - Start with a simple target class for initial testing
   - Create a main class that exercises the target functionality
   - Begin with basic breakpoints before adding advanced features
   - Use extensive logging to understand event flow

### Future Enhancements

The current implementation provides a foundation for adding more advanced debugging features:
- Method entry/exit tracking
- Exception catching and handling
- Conditional breakpoints
- Variable modification
- Step-by-step execution
- Call stack analysis
- Thread state inspection

## Session API

The Session API is the primary interface for LLM Agents to implement this sidecar debugging approach. It provides a high-level, easy-to-use abstraction over the JVM Debugger API, allowing agents to debug and inspect JVM programs programmatically.