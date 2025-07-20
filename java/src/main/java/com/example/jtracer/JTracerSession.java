package com.example.jtracer;

import com.sun.jdi.*;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Main entry point for the JTracer debugging session.
 * Provides high-level API for LLM agents to debug and analyze JVM programs.
 */
public class JTracerSession implements AutoCloseable {
    private VirtualMachine vm;
    private final Map<String, List<BreakpointRequest>> breakpoints = new HashMap<>();
    private final List<Map<String, Object>> events = new ArrayList<>();
    private final Map<String, List<Integer>> pendingBreakpoints = new HashMap<>();
    private final String mainClass;

    /**
     * Creates a new debugging session for the specified main class
     */
    public JTracerSession(String mainClass) throws Exception {
        this.mainClass = mainClass;
        
        LaunchingConnector connector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> args = connector.defaultArguments();
        args.get("main").setValue(mainClass);
        args.get("options").setValue("-cp " + System.getProperty("java.class.path"));
        
        this.vm = connector.launch(args);
        
        // Set up class prepare request to handle breakpoints
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.enable();
        
        // Add VM death and disconnect events
        EventRequestManager erm = vm.eventRequestManager();
        VMDeathRequest vmDeathRequest = erm.createVMDeathRequest();
        vmDeathRequest.enable();
        
        // Set up exception events
        ExceptionRequest excReq = erm.createExceptionRequest(null, true, true);
        excReq.enable();
        
        // Set up thread start event to catch main thread
        ThreadStartRequest threadStartRequest = erm.createThreadStartRequest();
        threadStartRequest.enable();
    }

    /**
     * Sets a breakpoint at the specified location
     */
    public void setBreakpoint(String className, int lineNumber) {
        pendingBreakpoints.computeIfAbsent(className, k -> new ArrayList<>()).add(lineNumber);
        
        // Try to set immediately if the class is already loaded
        List<ReferenceType> classes = vm.classesByName(className);
        if (!classes.isEmpty()) {
            createBreakpoint(className, classes.get(0));
        }
    }

    private void createBreakpoint(String className, ReferenceType classRef) {
        List<Integer> lines = pendingBreakpoints.get(className);
        if (lines == null) return;
        
        for (Integer lineNumber : lines) {
            try {
                List<Location> locations = classRef.locationsOfLine(lineNumber);
                
                if (locations.isEmpty()) {
                    // Try nearby lines
                    for (int offset = 1; offset <= 2; offset++) {
                        locations = classRef.locationsOfLine(lineNumber + offset);
                        if (!locations.isEmpty()) break;
                        locations = classRef.locationsOfLine(lineNumber - offset);
                        if (!locations.isEmpty()) break;
                    }
                    if (locations.isEmpty()) continue;
                }
                
                Location location = locations.get(0);
                BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                bpReq.enable();
                
                breakpoints.computeIfAbsent(className, k -> new ArrayList<>()).add(bpReq);
            } catch (Exception e) {
                events.add(Map.of(
                    "type", "ERROR",
                    "message", "Failed to set breakpoint at " + className + ":" + lineNumber,
                    "error", e.getMessage()
                ));
            }
        }
    }

    /**
     * Starts the debugging session and collects events
     */
    public void run() throws Exception {
        EventQueue queue = vm.eventQueue();
        boolean running = true;

        while (running) {
            EventSet eventSet = queue.remove();
            
            for (Event event : eventSet) {
                if (event instanceof ClassPrepareEvent) {
                    ClassPrepareEvent cpe = (ClassPrepareEvent) event;
                    String className = cpe.referenceType().name();
                    
                    if (pendingBreakpoints.containsKey(className)) {
                        createBreakpoint(className, cpe.referenceType());
                    }
                } else if (event instanceof BreakpointEvent) {
                    BreakpointEvent bpEvent = (BreakpointEvent) event;
                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("type", "BREAKPOINT");
                    eventData.put("location", bpEvent.location().toString());
                    eventData.put("variables", captureVariables(bpEvent.thread()));
                    events.add(eventData);
                } else if (event instanceof ExceptionEvent) {
                    ExceptionEvent excEvent = (ExceptionEvent) event;
                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("type", "EXCEPTION");
                    eventData.put("location", excEvent.location().toString());
                    eventData.put("exception_type", excEvent.exception().referenceType().name());
                    eventData.put("message", excEvent.exception().toString());
                    eventData.put("variables", captureVariables(excEvent.thread()));
                    
                    // Capture stack trace
                    List<Map<String, String>> stackTrace = new ArrayList<>();
                    try {
                        for (StackFrame frame : excEvent.thread().frames()) {
                            Map<String, String> frameInfo = new HashMap<>();
                            frameInfo.put("class", frame.location().declaringType().name());
                            frameInfo.put("method", frame.location().method().name());
                            frameInfo.put("line", String.valueOf(frame.location().lineNumber()));
                            stackTrace.add(frameInfo);
                        }
                    } catch (IncompatibleThreadStateException e) {
                        // Handle thread state issues
                    }
                    eventData.put("stack_trace", stackTrace);
                    events.add(eventData);
                } else if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                    running = false;
                }
            }
            
            eventSet.resume();
        }
    }

    /**
     * Captures the current state of all visible variables in the given thread
     */
    private Map<String, String> captureVariables(ThreadReference thread) {
        Map<String, String> vars = new HashMap<>();
        try {
            if (thread.frameCount() > 0) {
                StackFrame frame = thread.frame(0);
                
                // Local variables
                for (LocalVariable var : frame.visibleVariables()) {
                    Value value = frame.getValue(var);
                    vars.put(var.name(), value != null ? value.toString() : "null");
                }
                
                // this reference if available
                ObjectReference thisObj = frame.thisObject();
                if (thisObj != null) {
                    for (Field field : thisObj.referenceType().fields()) {
                        Value value = thisObj.getValue(field);
                        vars.put("this." + field.name(), value != null ? value.toString() : "null");
                    }
                }
            }
        } catch (Exception e) {
            vars.put("ERROR", e.getMessage());
        }
        return vars;
    }

    /**
     * Returns all collected debug events as JSON
     */
    public String getEventsAsJson() {
        return new JSONArray(events).toString(2);
    }

    @Override
    public void close() {
        if (vm != null) {
            vm.dispose();
        }
    }
} 