package org.jahia.modules.serverperfanalyzer.threadumps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

public class ThreadDumpsFileWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ThreadDumpsFileWrapper.class);

    private List<ThreadDumpWrapper> threadDumps;
    private Instant parsingDate;
    private String label;

    public ThreadDumpsFileWrapper(List<ThreadDumpWrapper> threadDumps, Instant parsingDate) {
        this.threadDumps = threadDumps;
        this.parsingDate = parsingDate;
        label = parsingDate.toString();
    }

    public List<ThreadDumpWrapper> getThreadDumps() {
        return threadDumps;
    }

    public Instant getParsingDate() {
        return parsingDate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
