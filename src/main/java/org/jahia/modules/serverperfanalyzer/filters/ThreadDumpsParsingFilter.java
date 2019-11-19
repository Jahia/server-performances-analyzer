package org.jahia.modules.serverperfanalyzer.filters;

import org.jahia.modules.serverperfanalyzer.threadumps.ThreadDumpsFileWrapper;
import org.jahia.modules.serverperfanalyzer.threadumps.ThreadDumpsService;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ThreadDumpsParsingFilter extends AbstractFilter {

    private static final Logger logger = LoggerFactory.getLogger(ThreadDumpsParsingFilter.class);

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        final Collection<ThreadDumpsFileWrapper> fileContents = ThreadDumpsService.getInstance().getThreadDumps();
        chain.pushAttribute(renderContext.getRequest(), "fileContents", fileContents);
        return super.prepare(renderContext, resource, chain);
    }
}
