package org.jahia.modules.serverperfanalyzer.threadumps;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jahia.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadDumpsParser {

    private static final Logger logger = LoggerFactory.getLogger(ThreadDumpsParser.class);
    private static final String THREAD_HEADER = "\\W*\\\"(.+)\\\"\\W+nid=(\\d+)\\W+state=(.+)\\W+\\[\\]\\W*";

    public static ThreadDumpsFileWrapper parse(File file) {
        final Instant start = Instant.now();
        final List<ThreadDumpWrapper> threadDumps = new ArrayList<>();
        try {
            final Pattern threadHeaderPattern = Pattern.compile(THREAD_HEADER);
            ThreadDumpWrapper currentTD = new ThreadDumpWrapper();
            ThreadWrapper currentThread = null;
            final List<String> lines = FileUtils.readLines(file);
            for (String line : lines) {
                if (StringUtils.isBlank(line)) continue;

                if (line.trim().equals("<EndOfDump>")) {
                    threadDumps.add(currentTD);
                    currentTD = new ThreadDumpWrapper();
                    currentThread = null;
                    continue;
                }
                final Matcher matcher = threadHeaderPattern.matcher(line);
                if (matcher.matches()) {
                    currentThread = new ThreadWrapper(matcher.group(1), matcher.group(2), matcher.group(3));
                    currentTD.addThread(currentThread);
                    continue;
                }
                if (currentThread == null) {
                    if (!currentTD.hasDate()) {
                        currentTD.setDate(line);
                        continue;
                    }
                    if (!currentTD.hasDesc()) {
                        currentTD.setDesc(line);
                        continue;
                    }
                    logger.error("Don't know what to do with this line {}", line);
                }

                if (currentThread != null) {
                    if (!currentThread.hasExtendedState()) currentThread.setExtendedState(line);
                    else currentThread.appendToStack(line);
                    continue;
                }
                logger.error("Don't know what to do with this line {}", line);
            }

        } catch (IOException e) {
            logger.error("Impossible to read the file", e);
        }

        if (logger.isDebugEnabled())
            logger.debug(String.format("Parsed %s in %s", file.getPath(), DateUtils.formatDurationWords(Duration.between(start, Instant.now()).toMillis())));


        return new ThreadDumpsFileWrapper(threadDumps, start);
    }

}
