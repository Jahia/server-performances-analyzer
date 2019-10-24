package org.jahia.modules.serverperfanalyzer.threadumps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ThreadDumpWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ThreadDumpWrapper.class);

    private String date, desc;
    private List<ThreadWrapper> threads = new ArrayList<>();

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

    public List<ThreadWrapper> getThreads() {
        return threads;
    }

    public List<ThreadWrapper> getThreadsByStackLenght() {
        List<ThreadWrapper> sortedThreads = new ArrayList<>(threads);
        sortedThreads.sort(new Comparator<ThreadWrapper>() {
            @Override
            public int compare(ThreadWrapper thread1, ThreadWrapper thread2) {
                if (thread1.getStack().size() == thread2.getStack().size())
                    return thread1.getName().compareTo(thread2.getName());
                return thread2.getStack().size() - thread1.getStack().size();
            }
        });
        return sortedThreads;
    }

    public void addThread(ThreadWrapper thread) {
        threads.add(thread);
    }
}
