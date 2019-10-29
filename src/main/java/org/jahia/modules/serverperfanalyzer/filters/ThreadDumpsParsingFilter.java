package org.jahia.modules.serverperfanalyzer.filters;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.jahia.modules.serverperfanalyzer.threadumps.ThreadDumpWrapper;
import org.jahia.modules.serverperfanalyzer.threadumps.ThreadDumpsParser;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ThreadDumpsParsingFilter extends AbstractFilter {

    private static final Logger logger = LoggerFactory.getLogger(ThreadDumpsParsingFilter.class);

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        final File logsFolder = new File(System.getProperty("jahia.log.dir"));
        if (!logsFolder.exists()) {
            logger.error("The logs folder is not correctly configured");
            return super.prepare(renderContext, resource, chain);
        }
        final File threadsFolder = new File(logsFolder, "jahia-threads");
        if (!threadsFolder.exists()) {
            chain.pushAttribute(renderContext.getRequest(), "availableFiles", ListUtils.EMPTY_LIST);
            return super.prepare(renderContext, resource, chain);
        }
        final Collection<File> files = FileUtils.listFiles(threadsFolder, null, true);
        final Map<String, List<ThreadDumpWrapper>> fileContents = new TreeMap<>();
        for (File file : files) {
            fileContents.put(file.getParentFile().getName() + '/' + file.getName(), ThreadDumpsParser.parse(file));
        }
        chain.pushAttribute(renderContext.getRequest(), "availableFiles", fileContents.keySet());
        chain.pushAttribute(renderContext.getRequest(), "fileContents", fileContents);

        return super.prepare(renderContext, resource, chain);
    }
}
