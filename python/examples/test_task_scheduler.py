"""
Example demonstrating the trace table with a task scheduler that uses various Python features:
- Custom exceptions
- Context managers
- Generators
- Classes with special methods
"""

class TaskError(Exception):
    """Custom exception for task-related errors"""
    pass

class Task:
    def __init__(self, name, priority=0):
        if not name:
            raise TaskError("Task name cannot be empty")
        self.name = name
        self.priority = priority
        self.completed = False
    
    def complete(self):
        """Mark task as completed"""
        self.completed = True
        return self
    
    def __str__(self):
        return f"{self.name}(priority={self.priority})"

class TaskScheduler:
    def __init__(self):
        self.tasks = []
    
    def add_task(self, task):
        """Add a task to the scheduler"""
        self.tasks.append(task)
        # Sort by priority (higher numbers first)
        self.tasks.sort(key=lambda t: (-t.priority, t.name))
    
    def get_tasks(self):
        """Generator that yields uncompleted tasks"""
        for task in self.tasks:
            if not task.completed:
                yield task
    
    def __enter__(self):
        """Context manager entry"""
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        """Context manager exit - complete all remaining tasks"""
        for task in self.tasks:
            task.complete()

def test_scheduler(trace_table):
    """Test task scheduler with various Python features."""
    try:
        # This should raise a TaskError
        Task("")
    except TaskError as e:
        assert str(e) == "Task name cannot be empty"
    
    # Create some tasks
    tasks = [
        Task("Write docs", priority=2),
        Task("Fix bugs", priority=3),
        Task("Add tests", priority=1)
    ]
    
    # Use context manager
    with TaskScheduler() as scheduler:
        # Add tasks to scheduler
        for task in tasks:
            scheduler.add_task(task)
        
        # Process tasks using generator
        for task in scheduler.get_tasks():
            if task.priority > 2:
                task.complete()
    
    # Verify all tasks are completed (due to context manager)
    assert all(task.completed for task in tasks)
    
    # Get and print trace
    trace = trace_table()
    if trace:
        print("\n=== Task Scheduler Trace ===")
        print("Line | Function            | Variables")
        print("-" * 60)
        
        for entry in trace:
            # Skip internal pytest functions and lambdas
            if entry['function'].startswith('_') or entry['function'] == '<lambda>':
                continue
                
            # Format local variables more concisely
            locals_str = ', '.join(
                f"{k}={v}" for k, v in entry['locals'].items()
                if not k.startswith('@') and k not in ['trace_table']
            )
            
            print(f"{entry['line']:4d} | {entry['function']:<18} | {locals_str}")

if __name__ == '__main__':
    print("Run this example with pytest to see the trace table output") 