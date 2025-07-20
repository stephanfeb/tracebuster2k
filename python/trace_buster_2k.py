import sys
import inspect
from typing import Dict, List, Any
from contextlib import contextmanager
import os
import pytest

class TraceCollector:
    def __init__(self):
        self.trace_data: List[Dict[str, Any]] = []
        self.enabled = False
        self.workspace_dir = os.getcwd()
        self.last_state = None  # Track last state to avoid duplicates
    
    def should_trace_file(self, filename: str) -> bool:
        """Filter out internal pytest and Python files"""
        if not filename or '<frozen' in filename:
            return False
        if 'site-packages' in filename:
            return False
        if not filename.startswith(self.workspace_dir):
            return False
        return True
    
    def safe_repr(self, obj: Any) -> str:
        """Safely get string representation of any object"""
        try:
            # For simple types, return as is
            if isinstance(obj, (int, float, str, bool, type(None))):
                return repr(obj)
            # For other objects, get their class name and string representation
            return f"{obj.__class__.__name__}({str(obj)})"
        except:
            return "<unprintable>"
    
    def trace_callback(self, frame, event, arg):
        if not self.enabled:
            return self.trace_callback
            
        if event == 'line':
            # Get current line info
            filename = frame.f_code.co_filename
            
            # Only trace files in our workspace
            if not self.should_trace_file(filename):
                return self.trace_callback
                
            function = frame.f_code.co_name
            lineno = frame.f_lineno
            
            # Get local variables
            locals_copy = {}
            for name, value in frame.f_locals.items():
                try:
                    locals_copy[name] = self.safe_repr(value)
                except:
                    continue
            
            # Create current state
            current_state = (lineno, function, str(locals_copy))
            
            # Only add if state has changed
            if current_state != self.last_state:
                self.trace_data.append({
                    'filename': os.path.relpath(filename, self.workspace_dir),
                    'function': function,
                    'line': lineno,
                    'locals': locals_copy
                })
                self.last_state = current_state
            
        return self.trace_callback
    
    @contextmanager
    def collect_trace(self):
        """Context manager to collect trace data for a code block"""
        self.enabled = True
        self.trace_data = []
        old_trace = sys.gettrace()
        sys.settrace(self.trace_callback)
        try:
            yield self.trace_data
        finally:
            sys.settrace(old_trace)
            self.enabled = False

# Global collector instance
collector = TraceCollector()

@pytest.fixture
def trace_table():
    """Fixture to collect and return trace data"""
    collector.trace_data = []  # Clear previous trace data
    with collector.collect_trace():
        yield lambda: collector.trace_data.copy()  # Return a function that returns a copy of the trace data 