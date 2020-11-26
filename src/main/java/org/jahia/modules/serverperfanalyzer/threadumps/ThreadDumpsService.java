package org.jahia.modules.serverperfanalyzer.threadumps;

import org.apache.commons.io.FileUtils;
import org.jahia.settings.SettingsBean;
import org.jahia.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
                logger.info(String.format("Loaded %s", parse(file).getLabel()));
            } else if (file.lastModified() > threadDumps.get(path).getParsingDate().toEpochMilli()) {
                logger.info(String.format("Reloaded %s as it has been modified since the last time it was parsed", parse(file).getLabel()));
            }
        }

        if (useLabFeatures()) {
            // if the jahia-threads folder doesn't exist, this line can't be reached (TODO fixme)
            final File jahiaErrorsFolder = new File(logsFolder, "jahia-errors");
            final Instant jahiaErrorsParsingStart = Instant.now();
            if (jahiaErrorsFolder.exists()) {
                final Collection<File> errorFiles = FileUtils.listFiles(jahiaErrorsFolder, null, true);
                final Map<String, Set<ThreadDumpsFileWrapper>> parsedFiles = new HashMap<>();
                for (File errorFile : errorFiles) {
                    final ThreadDumpsFileWrapper parsedFile = ThreadDumpsParser.parse(errorFile);
                    for (ThreadDumpWrapper threadDump : parsedFile.getThreadDumps()) {
                        // TODO would be much better to fix that in the parser itself
                        threadDump.setDate(errorFile.getName());
                    }
                    parsedFile.setLabel(errorFile.getName());
                    final String folder = errorFile.getParentFile().getName();
                    if (!parsedFiles.containsKey(folder))
                        parsedFiles.put(folder, new TreeSet<>(Comparator.comparing(ThreadDumpsFileWrapper::getLabel)));
                    parsedFiles.get(folder).add(parsedFile);
                }
                for (String folder : parsedFiles.keySet()) {
                    final List<ThreadDumpWrapper> tdOfTheDay = new ArrayList<>();
                    final Set<ThreadDumpsFileWrapper> threadDumpsFileWrappers = parsedFiles.get(folder);
                    for (ThreadDumpsFileWrapper threadDumps : threadDumpsFileWrappers) {
                        tdOfTheDay.addAll(threadDumps.getThreadDumps());
                    }
                    final ThreadDumpsFileWrapper tdOfTheDayWrapper = new ThreadDumpsFileWrapper(tdOfTheDay, jahiaErrorsParsingStart);
                    tdOfTheDayWrapper.setLabel("jahia-errors/" + folder);
                    threadDumps.put("jahia-errors/" + folder, tdOfTheDayWrapper);
                }

            }
        }

        if (logger.isDebugEnabled())
            logger.debug(String.format("Refreshed the thread dumps from the file system in %s", DateUtils.formatDurationWords(Duration.between(start, Instant.now()).toMillis())));
    }

    private ThreadDumpsFileWrapper parse(File file) {
        final ThreadDumpsFileWrapper parsedFile = ThreadDumpsParser.parse(file);
        parsedFile.setLabel(file.getParentFile().getName() + '/' + file.getName());
        threadDumps.put(file.getPath(), parsedFile);
        return parsedFile;
    }

    /**
     * Enables features which are not production ready, or not "state of the art" developed
     *
     * @return
     */
    private boolean useLabFeatures() {
        final String property = System.getProperty("modules.serverPerfsAnalyzer.devMode");
        if (property != null) return Boolean.parseBoolean(property);

        return Boolean.parseBoolean(SettingsBean.getInstance().getPropertiesFile().getProperty("modules.serverPerfsAnalyzer.devMode"));
    }
}
