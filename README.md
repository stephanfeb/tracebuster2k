# TraceBuster2K: Multi-Language Debugging Toolkit for LLM Agents

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Python Support](https://img.shields.io/badge/python-3.8+-blue.svg)](https://www.python.org/downloads/)
[![Go Support](https://img.shields.io/badge/go-1.19+-00ADD8.svg)](https://golang.org/)
[![Java Support](https://img.shields.io/badge/java-11+-orange.svg)](https://openjdk.org/)

> **Note**: This is an experimental project exploring the intersection of automated debugging and Large Language Models (LLMs). The author is keenly interested in enhancing LLMs' ability to understand, debug, and analyze code execution across multiple programming languages.

## Overview

TraceBuster2K is a comprehensive debugging and tracing toolkit designed specifically for LLM (Large Language Model) Agents. While traditional debuggers are built for human developers, TraceBuster2K bridges the gap between LLM capabilities and runtime program analysis by providing **programmatic access to execution traces, variable states, and debugging information** across Python, Go, and Java.

### The Problem

LLM Agents are increasingly capable of understanding, writing, and debugging code. However, they lack a fundamental capability that human developers take for granted: the ability to **"see" how code actually executes** and inspect runtime state. Without this insight, LLMs can only reason about code statically, missing critical information about:

- Actual execution paths and control flow
- Runtime variable values and state changes
- Function call sequences and stack traces
- Performance characteristics and bottlenecks
- The relationship between expected and actual program behavior

### The Solution: Sidecar Debugging

TraceBuster2K introduces a novel **"sidecar debugging"** approach that allows LLM Agents to programmatically debug and analyze code execution without modifying production code or its tests. This architecture consists of three key components:

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
            │ TraceBuster2K │
            │     Tests     │
            └───────────────┘
            (Written by LLM Agents)
```

1. **Production Code** - Your actual implementation (remains unchanged)
2. **Production Tests** - Standard unit/integration tests (remains unchanged)  
3. **Tracer Tests** - Special tests written by LLM Agents using TraceBuster2K to debug and analyze the production code

This separation of concerns means LLM Agents can deeply analyze your codebase without any modifications to your existing code or test suite.

## Language Support

TraceBuster2K provides consistent APIs and capabilities across three major programming languages:

### 🐍 Python (TraceBuster2K)
- **Technology**: Python's `sys.settrace()` mechanism
- **Integration**: pytest plugin for seamless test integration
- **Capabilities**: Line-by-line execution tracing, local variable capture, function call tracking
- **Best For**: Algorithm debugging, data structure analysis, recursive function tracing

### 🐹 Go (GoTracer)  
- **Technology**: Delve Debug Adapter Protocol (DAP)
- **Integration**: Native Go testing framework
- **Capabilities**: Breakpoint-based debugging, variable inspection, goroutine analysis
- **Best For**: Concurrent program analysis, performance debugging, complex control flow

### ☕ Java (JTracer)
- **Technology**: Java Debug Interface (JDI)
- **Integration**: JUnit and standard Java testing
- **Capabilities**: JVM-level debugging, object introspection, method tracing
- **Best For**: Enterprise application debugging, object-oriented analysis, multi-threaded programs

## Key Features

### 🤖 LLM-First Design
- **Structured Output**: All debugging data is formatted as JSON, tables, or other LLM-friendly formats
- **Programmatic APIs**: Simple, consistent APIs across all languages that LLMs can easily generate and use
- **Context-Rich Data**: Comprehensive variable states, call stacks, and execution context for deep analysis

### 🔍 Comprehensive Tracing
- **Execution Flow**: Complete function call sequences and control flow paths
- **Variable States**: Capture local, instance, and global variable values at any point
- **Data Structure Evolution**: Track how complex data structures change over time
- **Exception Analysis**: Detailed context about errors and their causes

### 🛡️ Non-Intrusive Analysis
- **Zero Code Changes**: Analyze existing codebases without modification
- **Production Safe**: Debugging logic stays separate from production code
- **Test Isolation**: Tracer tests don't interfere with existing test suites

### 🔧 Developer Friendly
- **Easy Integration**: Simple setup and configuration for all supported languages
- **Rich Examples**: Comprehensive example library showing common debugging patterns
- **Best Practices**: Documented patterns for effective LLM-assisted debugging

## Quick Start

### Python
```python
def test_algorithm_debug(trace_table):
    result = my_complex_algorithm(input_data)
    trace = trace_table()
    
    # LLM can analyze the complete execution flow
    for entry in trace:
        print(f"Line {entry['line']}: {entry['function']} | {entry['locals']}")
```

### Go
```go
func TestDebugMyFunction(t *testing.T) {
    debugger := tracer.NewDebugSession(t)
    defer debugger.Cleanup()
    
    debugger.StartDebugging("TestMyFunction")
    debugger.SetBreakpoints("myfile.go", []int{10, 20})
    debugger.ConfigurationDone()
    
    // LLM can inspect variables at specific points
    vars, _ := debugger.WaitForBreakpoint("MyFunction")
    fmt.Printf("Variables: %+v\n", vars)
}
```

### Java
```java
@Test
public void testDebugCalculator() throws Exception {
    try (JTracerSession session = new JTracerSession("Calculator")) {
        session.setBreakpoint("Calculator.java", 15);
        session.start();
        
        // LLM can analyze runtime state
        List<DebugEvent> events = session.getEvents();
        // Analyze variable states, call patterns, etc.
    }
}
```

## Use Cases for LLM Agents

### 🐛 Automated Debugging
- Generate comprehensive execution traces to understand complex bugs
- Analyze variable states leading up to errors
- Compare execution paths between working and failing scenarios
- Identify performance bottlenecks and optimization opportunities

### 📚 Code Understanding
- Build detailed documentation based on actual runtime behavior
- Generate flowcharts and execution diagrams from real traces
- Create comprehensive API usage examples with actual state transitions
- Understand legacy codebases through execution analysis

### 🧪 Test Generation
- Generate test cases based on observed program states and execution paths
- Create edge case tests by analyzing boundary conditions in traces
- Build property-based tests from runtime invariants
- Generate regression tests from execution patterns

### 🔍 Code Analysis
- Perform security analysis by tracing data flow and access patterns  
- Analyze algorithm complexity through actual execution metrics
- Validate architectural assumptions through runtime verification
- Generate code reviews based on execution behavior analysis

## Project Structure

```
tracebuster2k/
├── python/          # TraceBuster2K Python implementation
│   ├── trace_buster_2k.py    # Core pytest plugin
│   ├── examples/             # Python debugging examples
│   └── README.md            # Python-specific documentation
├── golang/          # GoTracer implementation  
│   ├── tracer/              # Core Go debugging library
│   ├── tests/               # Go debugging examples
│   └── README.md            # Go-specific documentation
├── java/            # JTracer implementation
│   ├── src/main/java/       # Core Java debugging library
│   ├── src/test/java/       # Java debugging examples
│   └── LLM_PROMPT.md        # Java-specific documentation
└── README.md        # This file
```

## Getting Started

1. **Choose Your Language**: Navigate to the `python/`, `golang/`, or `java/` directory
2. **Follow Setup Instructions**: Each language has specific installation and setup requirements
3. **Run Examples**: Try the provided examples to understand the debugging capabilities
4. **Write Tracer Tests**: Create your own debugging tests using the provided APIs

## Philosophy

TraceBuster2K is built on the belief that **the future of software development involves AI agents that can understand, debug, and enhance code through runtime analysis**. By providing programmatic access to execution traces and debugging information, we enable a new paradigm of AI-assisted development where:

- LLMs can reason about code behavior, not just structure
- Debugging becomes an automated, systematic process  
- Code understanding is enhanced through execution analysis
- Complex codebases become more accessible and maintainable

## Contributing

This is an experimental project, and we welcome contributions, feedback, and ideas! Whether you're interested in:

- Adding support for additional programming languages
- Improving LLM integration patterns
- Extending debugging capabilities
- Sharing interesting use cases

Please feel free to submit issues, pull requests, or start discussions.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Ready to give your LLM agents X-ray vision into code execution?** Start with the language-specific READMEs and examples to begin programmatic debugging with TraceBuster2K.
