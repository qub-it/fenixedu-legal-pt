<%@page import="org.fenixedu.legalpt.ui.raides.RaidesRequestsController"%>
<%@page import="org.fenixedu.bennu.core.security.Authenticate"%>
<%@page import="org.fenixedu.academic.domain.person.RoleType"%>
<%@page import="org.fenixedu.legalpt.domain.report.LegalReportRequest"%>
<%@page import="org.fenixedu.legalpt.domain.raides.report.RaidesRequestParameter"%>
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
        <spring:message code="label.RaidesRequests.read" />
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
   	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= RaidesRequestsController.SEARCH_URL %>">
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
RaidesRequestParameter parameter = reportRequest.getParametersAs(RaidesRequestParameter.class);
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
                            <spring:message code="label.RaidesRequests.institution" />
                        </th>
                        <td>
                            <c:out value='${ parameter.institution.nameI18n.content }' />
                        </td>
                    </tr>                    
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.institutionCode" />
                        </th>
                        <td>
                            <c:out value='${ parameter.institutionCode }' />
                        </td>
                    </tr>                    
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.moment" />
                        </th>
                        <td>
                            <c:out value='${ parameter.moment }' />
                        </td>
                    </tr>
                    <%--
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.interlocutorName" />
                        </th>
                        <td>
                            <c:out value='${ parameter.interlocutorName }' />
                        </td>
                    </tr>                    
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.interlocutorEmail" />
                        </th>
                        <td>
                            <c:out value='${ parameter.interlocutorEmail }' />
                        </td>
                    </tr>
                    --%>
                    <% if (RoleType.MANAGER.isMember(Authenticate.getUser())) {%>
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.studentNumber" />
                        </th>
                        <td>
                            <c:out value='${ parameter.studentNumber }' />
                        </td>
                    </tr>
                    <% } %>                    
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.filterEntriesWithErrors" />
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
                            <spring:message code="label.RaidesRequests.graduatedExecutionYear" />
                        </th>
                        <td>
                        	<c:out value="${empty parameter.graduatedExecutionYear ? '' : parameter.graduatedExecutionYear.qualifiedName}" />
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.agreementsForEnrolled" />
                        </th>
                        <td>
                            <ul>
                            <c:forEach var="agreement" items="${ parameter.agreementsForEnrolled }">
                                <li>
                                    <c:out value='${ agreement.description.content }' />
                                </li>
                            </c:forEach>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.agreementsForMobility" />
                        </th>
                        <td>
                            <ul>
                            <c:forEach var="agreement" items="${ parameter.agreementsForMobility }">
                                <li>
                                    <c:out value='${ agreement.description.content }' />
                                </li>
                            </c:forEach>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.ingressionsForDegreeChange" />
                        </th>
                        <td>
                            <ul>
                            <c:forEach var="ingression" items="${ parameter.ingressionsForDegreeChange }">
                                <li>
                                    <c:out value='${ ingression.localizedName }' />
                                </li>
                            </c:forEach>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.ingressionsForDegreeTransfer" />
                        </th>
                        <td>
                            <ul>
                            <c:forEach var="ingression" items="${ parameter.ingressionsForDegreeTransfer }">
                                <li>
                                    <c:out value='${ ingression.localizedName }' />
                                </li>
                            </c:forEach>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.ingressionsForGeneralAccessRegime" />
                        </th>
                        <td>
                            <ul>
                            <c:forEach var="ingression" items="${ parameter.ingressionsForGeneralAccessRegime }">
                                <li>
                                    <c:out value='${ ingression.localizedName }' />
                                </li>
                            </c:forEach>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3">
                            <spring:message code="label.RaidesRequests.degrees" />
                        </th>
                        <td>
                            <ul>
                            <c:forEach var="degree" items="${ parameter.degrees }">
                                <li>
                                    <c:out value='[${ degree.code }] ${ degree.degreeTypeName } - ${ degree.nameI18N.content }' />
                                </li>
                            </c:forEach>
                            </ul>
                        </td>
                    </tr>                    
                </tbody>
            </table>
        </form>
    </div>
</div>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><spring:message code="title.RaidesRequests.periods" /></h3>
    </div>
    <div class="panel-body">
        <table id="periodsTable" class="table responsive table-bordered table-hover">
            <thead>
                <tr>
                    <th><spring:message code="label.RaidesRequests.periodType" /></th>
                    <th><spring:message code="label.RaidesRequests.academicPeriod" /></th>
                    <th style="width:80px"><spring:message code="label.RaidesRequests.begin" /></th>
                    <th style="width:80px"><spring:message code="label.RaidesRequests.end" /></th>
                    <th><spring:message code="label.RaidesRequests.enrolledInAcademicPeriod" /></th>
                    <th><spring:message code="label.RaidesRequests.enrolmentEctsConstraint" /></th>
                    <th><spring:message code="label.RaidesRequests.minEnrolmentEcts" /></th>
                    <th><spring:message code="label.RaidesRequests.maxEnrolmentEcts" /></th>
                    <th><spring:message code="label.RaidesRequests.enrolmentYearsConstraint" /></th>
                    <th><spring:message code="label.RaidesRequests.minEnrolmentYears" /></th>
                    <th><spring:message code="label.RaidesRequests.maxEnrolmentYears" /></th>
                </tr>
            </thead>                
            <tbody>
                <c:forEach var="period" items="${ parameter.periods }">
                    <tr>
                        <td>${ period.periodInputType.localizedName }</td>
                        <td>${ period.academicPeriodQualifiedName }</td>
                        <td>${ period.begin }</td>
                        <td>${ period.end }</td>
                        <td>
                            <c:if test="${ period.enrolledInAcademicPeriod }">
                                <spring:message code="label.yes" />
                            </c:if>
                            <c:if test="${ not period.enrolledInAcademicPeriod }">
                                <spring:message code="label.no" />
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${ period.enrolmentEctsConstraint }">
                                <spring:message code="label.yes" />
                            </c:if>
                            <c:if test="${ not period.enrolmentEctsConstraint }">
                                <spring:message code="label.no" />
                            </c:if>
                        </td>
                        <td>${ period.minEnrolmentEcts }</td>
                        <td>${ period.maxEnrolmentEcts }</td>
                        <td>
                            <c:if test="${ period.enrolmentYearsConstraint }">
                                <spring:message code="label.yes" />
                            </c:if>
                            <c:if test="${ not period.enrolmentYearsConstraint }">
                                <spring:message code="label.no" />
                            </c:if>
                        </td>
                        <td>${ period.minEnrolmentYears }</td>
                        <td>${ period.maxEnrolmentYears }</td>
                    </tr>                    
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script type="text/javascript">
$(document).ready(function() {

});
</script>
