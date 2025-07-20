"""
Example demonstrating the trace table with a binary search tree:
- Tree data structure
- Recursive traversal
- Object-oriented programming
"""

class Node:
    def __init__(self, value):
        self.value = value
        self.left = None
        self.right = None
    
    def __str__(self):
        return f"Node({self.value})"

def insert_node(root, value):
    """Insert a value into the binary search tree"""
    if root is None:
        return Node(value)
    
    if value < root.value:
        root.left = insert_node(root.left, value)
    else:
        root.right = insert_node(root.right, value)
    return root

def search_node(root, target):
    """Search for a value in the binary search tree"""
    if root is None or root.value == target:
        return root is not None
    
    if target < root.value:
        return search_node(root.left, target)
    return search_node(root.right, target)

def test_bst(trace_table):
    """Test binary search tree operations with trace."""
    # Create a binary search tree
    root = None
    values = [5, 3, 7, 2, 4, 6, 8]
    
    # Insert values
    for value in values:
        root = insert_node(root, value)
    
    # Search for a value
    result = search_node(root, 4)
    assert result == True
    
    # Get and print trace
    trace = trace_table()
    if trace:
        print("\n=== Binary Search Tree Trace ===")
        print("Line | Function            | Variables")
        print("-" * 60)
        
        for entry in trace:
            # Skip internal pytest functions and lambdas
            if entry['function'].startswith('_') or entry['function'] == '<lambda>':
                continue
                
            # Format local variables more concisely
            locals_str = ', '.join(
                f"{k}={v}" for k, v in entry['locals'].items()
                if not k.startswith('@') and k not in ['trace_table', 'self']
            )
            
            print(f"{entry['line']:4d} | {entry['function']:<18} | {locals_str}")

if __name__ == '__main__':
    print("Run this example with pytest to see the trace table output") 