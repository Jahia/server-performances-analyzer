package org.jahia.modules.serverperfanalyzer.threadumps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ThreadWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ThreadWrapper.class);

    private String name, nid, state, extendedState;
    private List<String> stack = new ArrayList<>();

    public ThreadWrapper(String name, String nid, String state) {
        this.name = name;
        this.nid = nid;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getExtendedState() {
        return extendedState;
    }

    public boolean hasExtendedState() {
        return extendedState != null;
    }

    public void setExtendedState(String extendedState) {
        this.extendedState = extendedState;
    }

    public List<String> getStack() {
        return stack;
    }

    public void appendToStack(String line) {
        stack.add(line);
    }
}
