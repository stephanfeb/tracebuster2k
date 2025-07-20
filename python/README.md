# TraceBuster2K

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Python Support](https://img.shields.io/badge/python-3.8+-blue.svg)](https://www.python.org/downloads/)

A pytest plugin for generating detailed execution traces of your Python code. It helps you understand code flow, debug complex algorithms, and visualize program execution.

NOTE: This is an experimental project. The author is keenly interested in enhancing the ability to debug code with LLMs. Please submit feedback and PRs to improve this project.

## Features

- 🔍 Line-by-line execution tracing
- 📊 Capture local variable states
- 🌳 Support for complex data structures
- 🧩 Works with any Python code
- 🔌 Easy pytest integration
- 🛡️ Safe handling of unprintable objects

## Installation

1. Create and activate a virtual environment (recommended):
```bash
python -m venv .venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
```

2. Install from GitHub:
```bash
pip install git+https://github.com/twostack/tracebuster2k.git
```

For development installation:
```bash
# Clone the repository
git clone https://github.com/twostack/tracebuster2k.git
cd tracebuster2k

# Create and activate virtual environment
python -m venv .venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate

# Install in development mode with development dependencies
pip install -r requirements-dev.txt
pip install -e .
```

## Usage

1. Import the `trace_table` fixture in your test:
```python
def test_my_function(trace_table):
    # Your test code here
    result = my_function()
    
    # Get and print the trace
    trace = trace_table()
    if trace:
        print("\n=== Execution Trace ===")
        for entry in trace:
            print(f"{entry['line']:4d} | {entry['function']:<18} | {entry['locals']}")
```

2. Run your tests with pytest:
```bash
pytest -v -s  # The -s flag shows print output
```

## Examples

The `examples` directory contains several test cases demonstrating different Python features:

### 1. Binary Search Tree (`test_binary_search_tree.py`)
- Tree data structure implementation
- Recursive traversal
- Object-oriented programming
```python
def test_bst(trace_table):
    root = None
    values = [5, 3, 7, 2, 4, 6, 8]
    
    # Insert values into tree
    for value in values:
        root = insert_node(root, value)
    
    # Search for a value
    assert search_node(root, 4) == True
```

### 2. QuickSort Algorithm (`test_quicksort.py`)
- Recursive sorting algorithm
- In-place array manipulation
- Partitioning logic
```python
def test_sort(trace_table):
    arr = [64, 34, 25, 12, 22, 11, 90]
    quicksort(arr, 0, len(arr) - 1)
    assert arr == [11, 12, 22, 25, 34, 64, 90]
```

### 3. Task Scheduler (`test_task_scheduler.py`)
- Custom exceptions
- Context managers
- Generators
- Classes with special methods
```python
def test_scheduler(trace_table):
    with TaskScheduler() as scheduler:
        scheduler.add_task(Task("Fix bugs", priority=3))
        scheduler.add_task(Task("Write docs", priority=2))
        
        for task in scheduler.get_tasks():
            if task.priority > 2:
                task.complete()
```

## Trace Output Format

The trace output includes:
- Line number: The exact line being executed
- Function name: The current function or method
- Local variables: The state of all local variables

Example output:
```
=== Execution Trace ===
Line | Function            | Variables
------------------------------------------------------------
  40 | test_sort          | arr=[64, 34, 25, 12, 22, 11, 90]
  24 | quicksort          | arr=[64, 34, 25, 12, 22, 11, 90], low=0, high=6
  10 | partition          | arr=[64, 34, 25, 12, 22, 11, 90], pivot=90, i=-1
```

## Development

1. Clone the repository:
```bash
git clone https://github.com/yourusername/tracebuster2k.git
cd tracebuster2k
```

2. Create a virtual environment and install in development mode:
```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -e .
```

3. Run the examples:
```bash
pytest examples/ -v -s
```

## How It Works

TraceBuster2K uses Python's `sys.settrace()` to:
1. Hook into the Python interpreter's line-by-line execution
2. Collect function calls, line numbers, and local variables
3. Filter and format the trace data for readability
4. Provide the data through a simple pytest fixture

The trace collector handles:
- Recursive function calls
- Complex data structures
- Generator functions
- Context managers
- Exception handling

## Debugging Tips

TraceBuster2K can be a powerful debugging tool. Here are some effective strategies:

### 1. Understanding Complex Algorithms
When debugging recursive algorithms or complex data structures:
```python
def test_debug_recursion(trace_table):
    result = my_recursive_function(complex_input)
    trace = trace_table()
    
    # Filter trace to focus on specific function
    relevant_steps = [
        entry for entry in trace 
        if entry['function'] == 'my_recursive_function'
    ]
    
    # Analyze recursion depth and state changes
    for step in relevant_steps:
        print(f"Depth: {step['locals'].get('depth', 0)}")
        print(f"State: {step['locals']}")
```

### 2. Finding State Mutations
To track where and how variables change:
```python
def test_debug_mutations(trace_table):
    data = {'key': 'initial'}
    modify_data(data)
    
    # Get trace and find where 'data' changes
    trace = trace_table()
    previous_value = None
    
    for entry in trace:
        current_value = entry['locals'].get('data')
        if current_value != previous_value:
            print(f"Line {entry['line']}: data changed to {current_value}")
        previous_value = current_value
```

### 3. Exception Analysis
To understand what led to an exception:
```python
def test_debug_exception(trace_table):
    try:
        problematic_function()
    except Exception as e:
        trace = trace_table()
        
        # Print the last N steps before the exception
        print("\nLast 5 steps before exception:")
        for entry in trace[-5:]:
            print(f"Line {entry['line']}: {entry['function']}")
            print(f"Variables: {entry['locals']}\n")
```

### 4. Conditional Breakpoints
Use trace data to implement conditional logging:
```python
def test_debug_conditions(trace_table):
    process_large_dataset()
    
    trace = trace_table()
    # Find when a variable meets certain conditions
    suspicious_states = [
        entry for entry in trace
        if entry['locals'].get('counter', 0) > 1000
        and entry['locals'].get('error_rate', 0) > 0.5
    ]
    
    for state in suspicious_states:
        print(f"Suspicious state at line {state['line']}:")
        print(f"Variables: {state['locals']}")
```

### 5. Performance Bottleneck Analysis
To identify potential performance issues:
```python
from collections import Counter

def test_debug_performance(trace_table):
    result = compute_intensive_task()
    
    trace = trace_table()
    # Count function calls
    func_calls = Counter(entry['function'] for entry in trace)
    
    print("\nFunction call frequency:")
    for func, count in func_calls.most_common():
        print(f"{func}: {count} calls")
```

### Best Practices

1. **Filter Noise**: Use trace filtering to focus on relevant functions:
   ```python
   relevant_trace = [
       entry for entry in trace
       if not entry['function'].startswith('_')
       and entry['function'] not in ['pytest_runtest_call']
   ]
   ```

2. **Custom Formatters**: Create formatters for your specific data types:
   ```python
   def format_trace_entry(entry):
       return {
           'line': entry['line'],
           'func': entry['function'],
           'vars': {k: str(v) for k, v in entry['locals'].items()}
       }
   ```

3. **Trace Comparison**: Compare traces between working and failing cases:
   ```python
   def compare_traces(working_trace, failing_trace):
       working_steps = set((e['line'], e['function']) for e in working_trace)
       failing_steps = set((e['line'], e['function']) for e in failing_trace)
       return failing_steps - working_steps  # Different steps
   ```

4. **Integration with Logging**: Combine with Python's logging module:
   ```python
   import logging
   
   def test_with_logging(trace_table):
       logging.basicConfig(level=logging.DEBUG)
       result = my_function()
       
       for entry in trace_table():
           logging.debug(f"Line {entry['line']}: {entry['locals']}")
   ```

5. **Selective Tracing**: Focus on specific code paths:
   ```python
   def test_selective_debug(trace_table):
       result = complex_workflow()
       
       trace = trace_table()
       # Focus on a specific module or class
       module_trace = [
           entry for entry in trace
           if 'mymodule' in entry['function']
       ]
   ```

Remember to clean up debug traces before committing code by either:
- Removing the debug print statements
- Wrapping them in a debug flag condition
- Using Python's logging module with appropriate log levels

## Debugging with LLM Agents

TraceBuster2K can significantly enhance debugging sessions with LLM agents by providing structured execution traces. Here's how to effectively use it with AI assistants:

### 1. Providing Context to LLMs
When asking an LLM to help debug your code, include the trace output:
```python
def test_for_llm_analysis(trace_table):
    result = problematic_function()
    trace = trace_table()
    
    print("=== Trace for LLM Analysis ===")
    print(json.dumps({
        'execution_path': [
            {
                'line': entry['line'],
                'function': entry['function'],
                'variables': entry['locals']
            }
            for entry in trace
        ],
        'final_result': result
    }, indent=2))
```

### 2. Structured Error Analysis
Help LLMs understand error contexts:
```python
def test_error_context(trace_table):
    try:
        result = failing_function()
    except Exception as e:
        trace = trace_table()
        error_context = {
            'error_type': type(e).__name__,
            'error_message': str(e),
            'last_steps': trace[-5:],
            'variable_states': [
                entry['locals'] 
                for entry in trace[-5:]
            ]
        }
        print(f"Error Context for LLM:\n{json.dumps(error_context, indent=2)}")
```

### 3. State Evolution Analysis
Help LLMs track how data structures evolve:
```python
def test_state_evolution(trace_table):
    data = process_data()
    trace = trace_table()
    
    state_changes = []
    for entry in trace:
        if 'data' in entry['locals']:
            state_changes.append({
                'line': entry['line'],
                'function': entry['function'],
                'data_state': entry['locals']['data']
            })
    
    print("Data Evolution for LLM Analysis:")
    print(json.dumps(state_changes, indent=2))
```

### 4. Function Call Patterns
Help LLMs understand execution patterns:
```python
def test_call_patterns(trace_table):
    result = complex_operation()
    trace = trace_table()
    
    call_sequence = [
        {
            'step': i,
            'function': entry['function'],
            'args': {
                k: v for k, v in entry['locals'].items()
                if k not in ['self', 'trace_table']
            }
        }
        for i, entry in enumerate(trace)
    ]
    
    print("Call Sequence for LLM Analysis:")
    print(json.dumps(call_sequence, indent=2))
```

### Best Practices for LLM Integration

1. **Structured Output**: Format trace data as JSON for better LLM parsing:
   ```python
   def format_for_llm(trace):
       return {
           'execution_flow': [
               {
                   'step': i,
                   'location': f"{entry['function']}:{entry['line']}",
                   'state': entry['locals']
               }
               for i, entry in enumerate(trace)
           ]
       }
   ```

2. **Context Windows**: Consider LLM context limits when formatting traces:
   ```python
   def summarize_for_llm(trace):
       # Summarize long traces to fit context windows
       if len(trace) > 100:
           return {
               'summary': {
                   'total_steps': len(trace),
                   'key_points': trace[::10],  # Sample every 10th step
                   'final_states': trace[-5:]
               }
           }
   ```

3. **Semantic Grouping**: Group related operations for better LLM comprehension:
   ```python
   def group_operations(trace):
       return {
           'initialization': [e for e in trace if 'init' in e['function']],
           'processing': [e for e in trace if 'process' in e['function']],
           'cleanup': [e for e in trace if 'cleanup' in e['function']]
       }
   ```

4. **Error Patterns**: Help LLMs identify error patterns:
   ```python
   def analyze_error_pattern(trace, error):
       return {
           'error_info': {
               'type': type(error).__name__,
               'message': str(error),
               'traceback': traceback.format_exc()
           },
           'execution_context': {
               'last_successful_step': next(
                   (e for e in reversed(trace) if 'error' not in e['locals']),
                   None
               ),
               'error_location': trace[-1] if trace else None
           }
       }
   ```

5. **Incremental Analysis**: Break down complex traces for LLMs:
   ```python
   def analyze_incrementally(trace):
       phases = []
       current_phase = []
       
       for entry in trace:
           current_phase.append(entry)
           if len(current_phase) >= 20:  # Process in chunks
               phases.append(analyze_phase(current_phase))
               current_phase = []
       
       return {
           'analysis_phases': phases,
           'overall_summary': summarize_phases(phases)
       }
   ```

Remember when working with LLMs:
- Provide structured, well-formatted trace data
- Include relevant context but avoid overwhelming the context window
- Group related operations semantically
- Highlight important state changes and error conditions
- Use consistent formatting for better pattern recognition

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 