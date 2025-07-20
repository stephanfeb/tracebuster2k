"""
Example demonstrating the trace table with the QuickSort algorithm:
- Recursive function calls
- In-place array manipulation
- Partitioning logic
"""

def partition(arr, low, high):
    """Partition the array using the last element as pivot"""
    pivot = arr[high]
    i = low - 1  # Index of smaller element
    
    for j in range(low, high):
        # If current element is smaller than or equal to pivot
        if arr[j] <= pivot:
            i += 1  # Increment index of smaller element
            arr[i], arr[j] = arr[j], arr[i]
    
    arr[i + 1], arr[high] = arr[high], arr[i + 1]
    return i + 1

def quicksort(arr, low, high):
    """Sort array in-place using QuickSort algorithm"""
    if low < high:
        # Partition array and get pivot position
        pivot_idx = partition(arr, low, high)
        
        # Sort elements before and after partition
        quicksort(arr, low, pivot_idx - 1)
        quicksort(arr, pivot_idx + 1, high)
    return arr

def test_sort(trace_table):
    """Test QuickSort algorithm with trace."""
    # Initialize array
    arr = [64, 34, 25, 12, 22, 11, 90]
    print(f"\nSorting array: {arr}")
    
    # Sort array
    quicksort(arr, 0, len(arr) - 1)
    assert arr == [11, 12, 22, 25, 34, 64, 90]
    
    # Get and print trace
    trace = trace_table()
    if trace:
        print("\n=== QuickSort Algorithm Trace ===")
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