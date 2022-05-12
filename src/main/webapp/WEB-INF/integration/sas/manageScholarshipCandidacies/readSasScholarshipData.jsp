<%@page import="org.fenixedu.ulisboa.integration.sas.ui.spring.controller.manageScholarshipCandidacies.ScholarshipCandidaciesController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<spring:url var="datatablesUrl"
	value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl"
	value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl"
	value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl"
	value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<link
	href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link
	href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>

					
<div class="panel panel-default">
	<%-- <div class="panel-heading">
		<h3 class="panel-title">
			<spring:message code="label.details" />
		</h3>
	</div> --%>
	<div class="panel-body">
		<form method="post" class="form-horizontal">
			<table class="table">
				<tbody>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.cycleIngressionYear" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.cycleIngressionYear}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.curricularYear" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.curricularYear}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.lastAcademicActDateLastYear" /></th>
						<td><c:out
								value='${sasScholarshipCandidacy.sasScholarshipData.lastAcademicActDateLastYear}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.enrolmentDate" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.enrolmentDate}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.firstMonthExecutionYear" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.firstMonthExecutionYear}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.numberOfDegreeCurricularYears" /></th>
						<td><c:out
								value='${sasScholarshipCandidacy.sasScholarshipData.numberOfDegreeCurricularYears}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.numberOfEnrolledECTS" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.numberOfEnrolledECTS}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.numberOfApprovedEctsLastYear" /></th>
						<td><c:out
								value='${sasScholarshipCandidacy.sasScholarshipData.numberOfApprovedEctsLastYear}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.numberOfEnrolledEctsLastYear" /></th>
						<td><c:out
								value='${sasScholarshipCandidacy.sasScholarshipData.numberOfEnrolledEctsLastYear}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.numberOfEnrolmentsYears" /></th>
						<td><c:out
								value='${sasScholarshipCandidacy.sasScholarshipData.numberOfEnrolmentsYears}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.cycleNumberOfEnrolmentsYearsInIntegralRegime" /></th>
						<td><c:out
								value='${sasScholarshipCandidacy.sasScholarshipData.cycleNumberOfEnrolmentsYearsInIntegralRegime}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.numberOfMonthsExecutionYear" /></th>
						<td><c:out
								value='${sasScholarshipCandidacy.sasScholarshipData.numberOfMonthsExecutionYear}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.numberOfDegreeChanges" /></th>
						<td><c:out
								value='${sasScholarshipCandidacy.sasScholarshipData.numberOfDegreeChanges}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.observations" /></th>
						<td>
							<c:out value="${fn:replace(sasScholarshipCandidacy.sasScholarshipData.observations, '.', '.<br/>')}" escapeXml="false"/>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.hasMadeDegreeChangeOnCurrentYear" /></th>
						<td>
							<c:choose>
							   <c:when test = "${sasScholarshipCandidacy.sasScholarshipData.hasMadeDegreeChangeOnCurrentYear}">
							      <spring:message code="label.true" />
							   </c:when>
							   <c:otherwise>
							     <spring:message code="label.false" />
							   </c:otherwise>
							</c:choose>
						
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.regime" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.regime}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.cetQualificationOwner" /></th>
						<td>
							<c:choose>
							   <c:when test = "${sasScholarshipCandidacy.sasScholarshipData.cetQualificationOwner}">
							      <spring:message code="label.true" />
							   </c:when>
							   <c:otherwise>
							     <spring:message code="label.false" />
							   </c:otherwise>
							</c:choose>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.ctspQualificationOwner" /></th>
						<td>
							<c:choose>
							   <c:when test = "${sasScholarshipCandidacy.sasScholarshipData.ctspQualificationOwner}">
							      <spring:message code="label.true" />
							   </c:when>
							   <c:otherwise>
							     <spring:message code="label.false" />
							   </c:otherwise>
							</c:choose>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.phdQualificationOwner" /></th>
						<td>
							<c:choose>
							   <c:when test = "${sasScholarshipCandidacy.sasScholarshipData.phdQualificationOwner}">
							      <spring:message code="label.true" />
							   </c:when>
							   <c:otherwise>
							     <spring:message code="label.false" />
							   </c:otherwise>
							</c:choose>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.degreeQualificationOwner" /></th>
						<td>
							<c:choose>
							   <c:when test = "${sasScholarshipCandidacy.sasScholarshipData.degreeQualificationOwner}">
							      <spring:message code="label.true" />
							   </c:when>
							   <c:otherwise>
							     <spring:message code="label.false" />
							   </c:otherwise>
							</c:choose>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.masterQualificationOwner" /></th>
						<td>
							<c:choose>
							   <c:when test = "${sasScholarshipCandidacy.sasScholarshipData.masterQualificationOwner}">
							      <spring:message code="label.true" />
							   </c:when>
							   <c:otherwise>
							     <spring:message code="label.false" />
							   </c:otherwise>
							</c:choose>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.lastEnrolmentYear" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.lastEnrolmentYear}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.gratuityAmount" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.gratuityAmount}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.enroled" /></th>
						<td>
							<c:choose>
							   <c:when test = "${sasScholarshipCandidacy.sasScholarshipData.enroled}">
							      <spring:message code="label.true" />
							   </c:when>
							   <c:otherwise>
							     <spring:message code="label.false" />
							   </c:otherwise>
							</c:choose>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.numberOfApprovedEcts" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.numberOfApprovedEcts}' /></td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.ingressionRegimeCode" /></th>
						<td><c:out value='${sasScholarshipCandidacy.sasScholarshipData.ingressionRegime}' /></td>
					</tr>

				</tbody>
			</table>

		</form>
	</div>
</div>

<script>
	$(document).ready(function() {

	});
</script>
