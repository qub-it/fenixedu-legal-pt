<%@page import="org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMappingEntry"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.BranchMappingType"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.legal.report.raides.ManageBranchMappingsController"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.managemobilityactivitytypes.MobilityActivityTypeController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
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
${portal.angularToolkit()}
<%--${portal.toolkit()}--%>

<link
	href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
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
<script
	src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<script
	src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script
	src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>

<div class="modal fade" id="deleteModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="deleteForm"
				action="${pageContext.request.contextPath}<%= ManageBranchMappingsController.DELETE_MAPPING_URL %>/${degreeCurricularPlan.externalId}" method="POST">
				
				<input id="mappingEntryId" type="hidden" name="mappingEntryId" value="" />
				
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirmation" />
					</h4>
				</div>
				<div class="modal-body">
					<p>
						<spring:message code="label.ManageBranchMappings.deleteMappingEntry.confirmDelete" />
					</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.close" />
					</button>
					<button id="deleteButton" class="btn btn-danger" type="submit">
						<spring:message code="label.delete" />
					</button>
				</div>
			</form>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<script>
	function showDeleteModal(externalId) {
		$("#mappingEntryId").attr('value', externalId);
		$("#deleteModal").modal('show');
	};
	
</script>


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.ManageBranchMappings.view" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= ManageBranchMappingsController.SEARCH_URL %>">
		<spring:message code="label.event.back" />
	</a>
	&nbsp;|&nbsp;
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= ManageBranchMappingsController.ADD_MAPPING_URL %>/${degreeCurricularPlan.externalId}">
		<spring:message code="label.event.create" />
	</a>
</div>

<h2>
	<c:out value="[${degreeCurricularPlan.degree.code}] ${degreeCurricularPlan.presentationName}" />
</h2>

<c:choose>
	<c:when test="${not empty mappingEntries}">
		<table id="simpleTable"
			class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<th><spring:message code="label.ManageBranchMappings.key" /></th>
					<th><spring:message code="label.ManageBranchMappings.value" /></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="entry" items="${mappingEntries}">
					<tr>
						<td><c:out value='<%= BranchMappingType.getInstance().getLocalizedNameKey(((LegalMappingEntry) pageContext.getAttribute("entry")).getMappingKey()).getContent() %>' /></td>
						<td><c:out value="${entry.mappingValue}" /></td>
						<td>
							<a class="btn btn-danger btn-xs" href="#" onclick="showDeleteModal(${entry.externalId}); return false;">
								<spring:message code="label.event.delete" />
							</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>
</c:choose>

<script>
	$(document).ready(function() {

	}); 
</script>

