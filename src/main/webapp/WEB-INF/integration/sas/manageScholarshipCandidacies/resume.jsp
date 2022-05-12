<%@page
	import="org.fenixedu.ulisboa.integration.sas.domain.SasScholarshipCandidacy"%>
<%@page
	import="org.fenixedu.ulisboa.integration.sas.ui.spring.controller.manageScholarshipCandidacies.ScholarshipCandidaciesController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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



<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageScholarshipCandidacies.resume" />
		<small></small>
	</h1>
</div>

<!-- /.modal -->
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}/academic-administration/sas-scholarship-management/integration-sas-manageScholarshipCandidacies/"><spring:message
			code="label.event.back" /></a>
	
	
	<c:if test="${sasScholarshipCandidacy.executionYear.current}">
		 |&nbsp;&nbsp;
		
			<span class="glyphicon glyphicon-cog" aria-hidden="true"></span>&nbsp;<a
			class=""
			href="${pageContext.request.contextPath}<%=ScholarshipCandidaciesController.PROCESS_ENTRY_URL%>/${sasScholarshipCandidacy.externalId}"><spring:message
				code="label.event.process" /></a> &nbsp;|&nbsp;
				
		<span
			class="glyphicon glyphicon-zoom-in" aria-hidden="true"></span>&nbsp;<a
			class=""
			href="${pageContext.request.contextPath}<%=ScholarshipCandidaciesController.VIEW_LOG_URL%>/${sasScholarshipCandidacy.externalId}"><spring:message
				code="label.event.logs" /></a> &nbsp;|&nbsp;
				
		<span
			class="glyphicon glyphicon-export" aria-hidden="true"></span>&nbsp;<a
			class="" href="#"
			onclick="showConfirmation('<spring:message code="label.send" />','<spring:message code="message.confirm.send.candidacy" />', '<spring:message code="label.send" />', '${pageContext.request.contextPath}<%=ScholarshipCandidaciesController.SEND_ENTRY_URL%>/${sasScholarshipCandidacy.externalId}');"><spring:message
				code="label.event.send" /></a> &nbsp;|&nbsp;
				
		<span
			class="glyphicon glyphicon-remove" aria-hidden="true"></span>&nbsp;<a
			class="" href="#"
			onclick="showConfirmation('<spring:message code="label.delete" />','<spring:message code="message.confirm.delete.candidacy" />', '<spring:message code="label.delete" />', '${pageContext.request.contextPath}<%=ScholarshipCandidaciesController.DELETE_ENTRY_URL%>/${sasScholarshipCandidacy.externalId}');"><spring:message
				code="label.event.delete" /></a>
	</c:if>

</div>
<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<spring:message code="label.SasScholarshipCandidacy.mainInfo" />
		</h3>
	</div>
	<div class="panel-body">
		<form method="post" class="form-horizontal">
			<table class="table">
				<tbody>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.submissionDate" /></th>
						<td><joda:format
								value='${sasScholarshipCandidacy.submissionDate}'
								pattern='yyyy-MM-dd' /></td>
					</tr>

					<tr>
						<th><spring:message
								code="label.SasScholarshipCandidacy.studentNumber" /></th>
						<td>${sasScholarshipCandidacy.studentNumber}</td>
					</tr>

					<tr>
						<th><spring:message
								code="label.SasScholarshipCandidacy.candidacyName" /></th>
						<td><c:out value='${sasScholarshipCandidacy.candidacyName}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.fiscalNumber" /></th>
						<td><c:out value='${sasScholarshipCandidacy.fiscalNumber}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.docIdNumber" /></th>
						<td><c:out value='${sasScholarshipCandidacy.docIdNumber}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.docIdType" /></th>
						<td><c:out value='${sasScholarshipCandidacy.docIdType}' /></td>
					</tr>
					<tr>
						<th><spring:message
								code="label.SasScholarshipCandidacy.degreeName" /></th>
						<td>[${sasScholarshipCandidacy.degreeCode}]
							<c:choose>
								<c:when test="${not empty searchResult.registration.degree.presentationName}">
									${searchResult.registration.degree.presentationName}
								</c:when>
								<c:otherwise>${searchResult.degreeName}</c:otherwise>
							</c:choose>
							
							</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.importDate" /></th>
						<td><joda:format
								value='${sasScholarshipCandidacy.importDate}'
								pattern='yyyy-MM-dd' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.stateDate" /></th>
						<td><joda:format value='${sasScholarshipCandidacy.stateDate}'
								pattern='yyyy-MM-dd' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.exportDate" /></th>
						<td><joda:format
								value='${sasScholarshipCandidacy.exportDate}'
								pattern='yyyy-MM-dd' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.firstYear" /></th>
						<td><c:if test="${sasScholarshipCandidacy.firstYear}">
								<spring:message code="label.true" />
							</c:if> <c:if test="${not sasScholarshipCandidacy.firstYear}">
								<spring:message code="label.false" />
							</c:if></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipData.state" /></th>
						<td><c:choose>
								<c:when
									test="${sasScholarshipCandidacy.state.name == 'PENDING'}">
									<span>${sasScholarshipCandidacy.state.localizedName}</span>
								</c:when>

								<c:when
									test="${sasScholarshipCandidacy.state.name == 'PROCESSED'}">
									<span class="text-info">${sasScholarshipCandidacy.state.localizedName}</span>
								</c:when>
								<c:when
									test="${sasScholarshipCandidacy.state.name == 'PROCESSED_WARNINGS'}">
									<span class="text-warning">${sasScholarshipCandidacy.state.localizedName}</span>
								</c:when>

								<c:when
									test="${sasScholarshipCandidacy.state.name == 'PROCESSED_ERRORS'}">
									<span class="text-danger">${sasScholarshipCandidacy.state.localizedName}</span>
								</c:when>

								<c:when test="${sasScholarshipCandidacy.state.name == 'SENT'}">
									<span class="text-success">${sasScholarshipCandidacy.state.localizedName}</span>
								</c:when>

								<c:when
									test="${sasScholarshipCandidacy.state.name == 'MODIFIED'}">
									<span class="text-warning">${sasScholarshipCandidacy.state.localizedName}</span>
								</c:when>

								<c:when
									test="${sasScholarshipCandidacy.state.name == 'ANNULLED'}">
									<span class="text-mutted">${sasScholarshipCandidacy.state.localizedName}</span>
								</c:when>
							</c:choose> <c:if test="${sasScholarshipCandidacy.modified}">*</c:if></td>
					</tr>

				</tbody>
			</table>

		</form>
	</div>
</div>

<style>
.panel-default {
	border-top: 0px;
}
</style>


<div>
	<ul id="tabs" class="nav nav-tabs" data-tabs="tabs">

		<li class="active"><a href="#academicData" data-toggle="tab"><spring:message
					code="label.details.SasScholarshipData" /></a></li>
		<li><a href="#candidacy" data-toggle="tab"><spring:message
					code="label.details.SasScholarshipCandidacy" /></a></li>
	</ul>

	<div id="my-tab-content" class="tab-content">
		<div class="tab-pane active" id="academicData">
			<jsp:include page="readSasScholarshipData.jsp" />
		</div>
		<div class="tab-pane" id="candidacy">
			<jsp:include page="readSasScholarshipCandidacy.jsp" />
		</div>
	</div>
</div>



<script>
	function showConfirmation(title, message, confirmMessage, url) {

		bootbox.confirm({
			title : title,
			message : message,
			buttons : {
				cancel : {
					label : '<spring:message code="label.cancel" />'
				},
				confirm : {
					label : confirmMessage,
					className : 'btn-danger'
				}
			},
			callback : function(result) {
				if (result) {
					window.location.href = url;
				}
			}
		});
	}

	$(document).ready(function() {

	});
</script>
