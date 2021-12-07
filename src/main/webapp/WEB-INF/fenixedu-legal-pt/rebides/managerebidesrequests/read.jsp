<%@page import="org.fenixedu.legalpt.domain.rebides.report.RebidesRequestParameter"%>
<%@page import="org.fenixedu.legalpt.ui.rebides.RebidesRequestsController"%>
<%@page import="org.fenixedu.bennu.core.security.Authenticate"%>
<%@page import="org.fenixedu.academic.domain.person.RoleType"%>
<%@page import="org.fenixedu.legalpt.domain.report.LegalReportRequest"%>
<%@page import="org.fenixedu.legalpt.domain.report.LegalReportRequestParameters"%>
<%@page import="org.fenixedu.legalpt.domain.report.LegalReportParameterFile"%>
<%@page import="com.google.gson.Gson"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />


<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.RebidesRequests.read" />
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
   	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= RebidesRequestsController.SEARCH_URL %>">
        <spring:message code="label.back" />
    </a>
</div>

<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">

        <c:forEach items="${infoMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">

        <c:forEach items="${warningMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">

        <c:forEach items="${errorMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>

    </div>
</c:if>

<%
LegalReportRequest reportRequest = (LegalReportRequest) request.getAttribute("reportRequest");
RebidesRequestParameter parameter = reportRequest.getParametersAs(RebidesRequestParameter.class);
request.setAttribute("parameter", parameter);
%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.details" />
        </h3>
    </div>
    <div class="panel-body">
        <form method="post" class="form-horizontal">
            <table class="table">
                <tbody>                   
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RebidesRequests.institutionCode" />
                        </th>
                        <td>
                            <c:out value='${ parameter.institutionCode }' />
                        </td>
                    </tr>                    
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RebidesRequests.moment" />
                        </th>
                        <td>
                            <c:out value='${ parameter.moment }' />
                        </td>
                    </tr>                    
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RebidesRequests.interlocutorName" />
                        </th>
                        <td>
                            <c:out value='${ parameter.interlocutorName }' />
                        </td>
                    </tr>                    
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RebidesRequests.interlocutorEmail" />
                        </th>
                        <td>
                            <c:out value='${ parameter.interlocutorEmail }' />
                        </td>
                    </tr>                    
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RebidesRequests.filterEntriesWithErrors" />
                        </th>
                        <td>
                            <c:if test="${ parameter.filterEntriesWithErrors }">
                                <spring:message code="label.yes" />
                            </c:if>
                            <c:if test="${ not parameter.filterEntriesWithErrors }">
                                <spring:message code="label.no" />                                
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RebidesRequests.executionYear" />
                        </th>
                        <td>
                        	<c:out value="${empty parameter.executionYear ? '' : parameter.executionYear.qualifiedName}" />
                        </td>
                    </tr>
                
                </tbody>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
$(document).ready(function() {

});
</script>
