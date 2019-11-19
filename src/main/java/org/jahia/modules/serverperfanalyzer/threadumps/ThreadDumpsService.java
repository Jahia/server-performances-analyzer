package org.jahia.modules.serverperfanalyzer.threadumps;


import org.apache.commons.io.FileUtils;
import org.jahia.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ThreadDumpsService {

    private static final Logger logger = LoggerFactory.getLogger(ThreadDumpsService.class);
    private static final ThreadDumpsService ourInstance = new ThreadDumpsService();

    private final Map<String, ThreadDumpsFileWrapper> threadDumps = new TreeMap<>();

    private ThreadDumpsService() {
    }

    public static ThreadDumpsService getInstance() {
        return ourInstance;
    }

    public Collection<ThreadDumpsFileWrapper> getThreadDumps() {
        refresh();
        return threadDumps.values();
    }

    synchronized private void refresh() {
        logger.debug("Refreshing the thread dumps from the file system");
        final Instant start = Instant.now();
        final File logsFolder = new File(System.getProperty("jahia.log.dir"));
        if (!logsFolder.exists()) {
            logger.error("The logs folder is not correctly configured");
            return;
        }
        final File threadsFolder = new File(logsFolder, "jahia-threads");
        if (!threadsFolder.exists()) {
            return;
        }
        final Collection<File> files = FileUtils.listFiles(threadsFolder, null, true);
        for (File file : files) {
            final String path = file.getPath();
            if (!threadDumps.containsKey(path)) {
                final ThreadDumpsFileWrapper parsedFile = ThreadDumpsParser.parse(file);
                parsedFile.setLabel(file.getParentFile().getName() + '/' + file.getName());
                threadDumps.put(path, parsedFile);
                logger.info(String.format("Loaded %s", parsedFile.getLabel()));
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(String.format("Refreshed the thread dumps from the file system in %s", DateUtils.formatDurationWords(Duration.between(start, Instant.now()).toMillis())));
    }
}
