<%@page import="org.fenixedu.ulisboa.specifications.ui.legal.report.raides.RaidesConfigurationController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<script type="text/javascript" src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/select/1.2.0/js/dataTables.select.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/buttons.html5.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/1.2.2/css/buttons.dataTables.min.css" />


<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%-- ${portal.toolkit()} --%>

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageRaidesConfiguration.read" />
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">

	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= RaidesConfigurationController.EDIT_URL %>">
		<spring:message code="label.event.update" />
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
							<spring:message code="label.RaidesInstance.institutionCode" />
						</th>
						<td>
							<c:out value='${raidesInstance.institutionCode}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.interlocutorPhone" />
						</th>
						<td>
							<c:out value='${raidesInstance.interlocutorPhone}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.passwordToZip" />
						</th>
						<td>
							<c:out value='${raidesInstance.passwordToZip}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.defaultDistrictOfResidence" />
						</th>
						<td>	
							<c:out value='${raidesInstance.defaultDistrictOfResidence.name}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.sumEctsCreditsBetweenPlans" />
						</th>
						<td>
							<c:if test="${raidesInstance.sumEctsCreditsBetweenPlans}">
								<spring:message code="label.true" />
							</c:if>
							<c:if test="${not raidesInstance.sumEctsCreditsBetweenPlans}">
								<spring:message code="label.false" />
							</c:if>
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.reportGraduatedWithoutConclusionProcess" />
						</th>
						<td>
							<spring:message code='${raidesInstance.reportGraduatedWithoutConclusionProcess ? "label.true" : "label.false"}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.integratedMasterFirstCycleGraduatedReportOption" />
						</th>
						<td>
							<c:out value='${raidesInstance.integratedMasterFirstCycleGraduatedReportOption.localizedName.content}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.grantOwnerStatuteTypes" />
						</th>
						<td>
							<c:forEach var="statuteType" items="${raidesInstance.grantOwnerStatuteTypes}">
								<c:out escapeXml="false" value="${statuteType.code} - ${statuteType.name.content} <br/>" />
							</c:forEach>
						</td>
					</tr>				
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.enrolledAgreements" />
						</th>
						<td>
							<c:forEach var="each" items="${raidesInstance.enrolledAgreementsSet}">
								<c:out escapeXml="false" value="${each.code} - ${each.description.content} <br/>" />
							</c:forEach>
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.mobilityAgreements" />
						</th>
						<td>
							<c:forEach var="each" items="${raidesInstance.mobilityAgreementsSet}">
								<c:out escapeXml="false" value="${each.code} - ${each.description.content} <br/>" />
							</c:forEach>
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.degreeChangeIngressions" />
						</th>
						<td>
							<c:forEach var="each" items="${raidesInstance.degreeChangeIngressionsSet}">
								<c:out escapeXml="false" value="${each.code} - ${each.description.content} <br/>" />
							</c:forEach>
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.degreeTransferIngressions" />
						</th>
						<td>
							<c:forEach var="each" items="${raidesInstance.degreeTransferIngressionsSet}">
								<c:out escapeXml="false" value="${each.code} - ${each.description.content} <br/>" />
							</c:forEach>
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.RaidesInstance.generalAccessRegimeIngressions" />
						</th>
						<td>
							<c:forEach var="each" items="${raidesInstance.generalAccessRegimeIngressionsSet}">
								<c:out escapeXml="false" value="${each.code} - ${each.description.content} <br/>" />
							</c:forEach>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

