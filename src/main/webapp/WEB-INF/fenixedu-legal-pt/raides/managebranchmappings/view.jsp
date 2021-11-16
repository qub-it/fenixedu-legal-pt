<%@page import="org.fenixedu.legalpt.ui.raides.ManageBranchMappingsController"%>
<%@page import="org.fenixedu.legalpt.domain.mapping.LegalMappingEntry"%>
<%@page import="org.fenixedu.legalpt.domain.raides.mapping.BranchMappingType"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />

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

