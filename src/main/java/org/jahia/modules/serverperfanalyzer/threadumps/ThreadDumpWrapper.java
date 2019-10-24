package org.jahia.modules.serverperfanalyzer.threadumps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ThreadDumpWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ThreadDumpWrapper.class);

    private String date, desc;
    private Map<String, ThreadWrapper> threads = new HashMap<>();

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean hasDate() {
        return date != null;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean hasDesc() {
        return desc != null;
    }

    public Map<String, ThreadWrapper> getThreads() {
        return threads;
    }

    public void addThread(ThreadWrapper thread) {
        threads.put(thread.getNid(), thread);
    }
}
