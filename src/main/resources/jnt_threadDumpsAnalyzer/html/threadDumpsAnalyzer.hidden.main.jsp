<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%@ taglib prefix="fmit" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="currentUser" type="org.jahia.services.usermanager.JahiaUser"--%>
<%--@elvariable id="currentAliasUser" type="org.jahia.services.usermanager.JahiaUser"--%>
<%--@elvariable id="file" type="org.jahia.modules.serverperfanalyzer.threadumps.ThreadDumpsFileWrapper"--%>
<%--@elvariable id="tdump" type="org.jahia.modules.serverperfanalyzer.threadumps.ThreadDumpWrapper"--%>
<%--@elvariable id="thread" type="org.jahia.modules.serverperfanalyzer.threadumps.ThreadWrapper"--%>

<template:addResources type="javascript" resources="jquery.min.js,jquery-ui.min.js"/>
<template:addResources type="css" resources="jquery-ui.smoothness.css,jquery-ui.smoothness-jahia.css,serverPerfs.css"/>
<c:set var="pageSize" value="10" />

<h2><fmt:message key="label.threadDumpsAnalyzer.availableFiles"/></h2>
<ul class="availableFiles">
<c:forEach items="${fileContents}" var="file" varStatus="status">
    <c:if test="${(empty param.file and status.first) or param.file eq status.index}">
        <c:set var="linkStyle"> class="selectedFile"</c:set>
    </c:if>
    <c:choose>
        <c:when test="${fn:length(file.threadDumps) le pageSize}">
            <c:url var="detailsURL" value="${url.edit}">
                <c:param name="file" value="${status.index}"/>
            </c:url>
            <li><a href="${detailsURL}" ${linkStyle}>${file.label}</a></li>
        </c:when>
        <c:otherwise>
            <li>
                <span>${file.label} (${fn:length(file.threadDumps)} threads)</span>
                <ul>
                <c:forEach begin="0" end="${fn:length(file.threadDumps)/pageSize-1}" varStatus="statusPages">
                    <c:url var="detailsURL" value="${url.edit}">
                        <c:param name="file" value="${status.index}"/>
                        <c:param name="page" value="${statusPages.index}" />
                    </c:url>
                    <c:if test="${(empty param.file and status.first) or param.file eq status.index}">
                        <c:set var="linkStyle"> class="selectedFile"</c:set>
                    </c:if>
                    <c:if test="${(empty param.file and status.first) or param.file eq status.index}">
                        <c:if test="${(empty param.page and statusPages.first) or param.page eq statusPages.index}">
                            <c:set var="linkPageStyle"> class="selectedFile"</c:set>
                        </c:if>
                    </c:if>
                    <li><a href="${detailsURL}" ${linkPageStyle}>Thread dumps ${statusPages.index*pageSize+1} to ${statusPages.count * pageSize}</a></li>
                    <c:remove var="linkPageStyle" />
                </c:forEach>
                </ul>
            </li>
        </c:otherwise>
    </c:choose>
    <c:remove var="linkStyle" />
</c:forEach>
</ul>

<c:choose>
    <c:when test="${empty param.page}">
        <c:set var="pagerBegin" value="0" />
        <c:set var="pagerEnd" value="${pageSize-1}" />
    </c:when>
    <c:otherwise>
        <c:set var="pagerBegin" value="${param.page*pageSize}" />
        <c:set var="pagerEnd" value="${param.page*pageSize + pageSize-1}" />
    </c:otherwise>
</c:choose>

<div id="tabs-${currentNode.identifier}">
    <ul>
        <c:forEach items="${fileContents}" var="file" begin="${param.file}" end="${param.file}">
            <c:forEach items="${file.threadDumps}" var="tdump" varStatus="status" begin="${pagerBegin}" end="${pagerEnd}">
                <c:set var="tdumpIndex" value="${status.index+1}" />
                <li><a href="#tabs-${currentNode.identifier}-${tdumpIndex}" title="${tdump.date}">${tdumpIndex}</a>
                </li>
            </c:forEach>
        </c:forEach>
    </ul>
    <c:forEach items="${fileContents}" var="file" begin="${param.file}" end="${param.file}">
        <c:forEach items="${file.threadDumps}" var="tdump" varStatus="status" begin="${pagerBegin}" end="${pagerEnd}">
            <c:set var="tdumpIndex" value="${status.index+1}" />
            <div id="tabs-${currentNode.identifier}-${tdumpIndex}">
                <div id="accordion-${currentNode.identifier}-${tdumpIndex}">
                    <c:forEach items="${tdump.threadsByStackLenght}" var="thread">
                        <c:choose>
                            <c:when test="${fn:length(thread.stack) ge longThreadThreshold}">
                                <c:set var="length"> <span class="runningThread">(stack length:${fn:length(thread.stack)})</span></c:set>
                            </c:when>
                            <c:otherwise>
                                <c:set var="length"> (stack length:${fn:length(thread.stack)})</c:set>
                            </c:otherwise>
                        </c:choose>
                        <h3>${thread.name} ${length}</h3>
                        <div>
                            <c:url var="analyzeURL" value="${url.edit}">
                                <c:param name="file" value="${param.file}" />
                                <c:param name="nid" value="${thread.nid}" />
                                <c:param name="td" value="${tdumpIndex}" />
                            </c:url>
                            <a href="${analyzeURL}" target="_blank" class="threadDetails">"${thread.name}" nid=${thread.nid} state=${thread.state} []</a><br/>
                            ${thread.extendedState}<br/>
                            <div class="stacktrace">
                                <c:forEach items="${thread.stack}" var="stackLine" begin="0" end="9">
                                    ${stackLine}<br/>
                                </c:forEach>
                                ...
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <template:addResources>
                <script>
                    jQuery(function () {
                        jQuery("#accordion-${currentNode.identifier}-${tdumpIndex}").accordion({
                            collapsible: true,
                            heightStyle: "content"
                        });
                    });
                </script>
                </template:addResources>
            </div>
        </c:forEach>
    </c:forEach>
</div>

<template:addResources>
    <script>
        jQuery(function () {
            jQuery("#tabs-${currentNode.identifier}").tabs();
        });
    </script>
</template:addResources>