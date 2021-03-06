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
<c:set var="currentPageURL" value="${url.base}${renderContext.mainResource.path}" />

<c:forEach items="${fileContents}" var="file" begin="${param.file}" end="${param.file}">
    <h3>${file.label}</h3>
    <div id="accordion-${currentNode.identifier}">
    <c:forEach items="${file.threadDumps}" var="tdump" varStatus="status">
            <c:forEach items="${tdump.threads}" var="thread">
                <c:if test="${thread.nid eq param.nid}">
                    <c:choose>
                        <c:when test="${fn:length(thread.stack) ge longThreadThreshold}">
                            <c:set var="length"> <span class="runningThread"> (stack length:${fn:length(thread.stack)})</span></c:set>
                        </c:when>
                        <c:otherwise>
                            <c:set var="length"> (stack length:${fn:length(thread.stack)})</c:set>
                        </c:otherwise>
                    </c:choose>
                    <h3>${thread.name} ${length} - Thread dump #${status.count}</h3>
                    <div>
                        <c:url var="analyzeURL" value="${currentPageURL}">
                            <c:param name="file" value="${param.file}" />
                            <c:param name="nid" value="${thread.nid}" />
                            <c:param name="td" value="${status.count}" />
                        </c:url>
                        <a href="${analyzeURL}" target="_self" class="threadDetails">"${thread.name}" nid=${thread.nid} state=${thread.state} []</a><br/>
                            ${thread.extendedState}<br/>
                        <c:forEach items="${thread.stack}" var="stackLine">
                            &nbsp;&nbsp;&nbsp;&nbsp;${stackLine}<br/>
                        </c:forEach>
                    </div>
                </c:if>
            </c:forEach>
    </c:forEach>
    </div>
    <template:addResources>
        <script>
            jQuery(function () {
                jQuery("#accordion-${currentNode.identifier}").accordion({
                    <c:if test="${not empty param.td}">
                    active: ${param.td -1},
                    </c:if>
                    collapsible: true,
                    heightStyle: "content"
                });
            });
        </script>
    </template:addResources>
</c:forEach>