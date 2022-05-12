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
								code="label.SasScholarshipCandidacy.institutionCode" /></th>
						<td><c:out value='${sasScholarshipCandidacy.institutionCode}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.institutionName" /></th>
						<td><c:out value='${sasScholarshipCandidacy.institutionName}' /></td>
					</tr>


					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.degreeCode" /></th>
						<td><c:out value='${sasScholarshipCandidacy.degreeCode}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.degreeName" /></th>
						<td><c:out value='${sasScholarshipCandidacy.degreeName}' /></td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.candidacyName" /></th>
						<td><c:out value='${sasScholarshipCandidacy.candidacyName}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.technicianName" /></th>
						<td><c:out
								value='${sasScholarshipCandidacy.technicianName}' /></td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.technicianEmail" /></th>
						<td><c:out value='${sasScholarshipCandidacy.technicianEmail}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.studentNumber" /></th>
						<td><c:out value='${sasScholarshipCandidacy.studentNumber}' /></td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.candidacyNumber" /></th>
						<td><c:out value='${sasScholarshipCandidacy.candidacyNumber}' /></td>
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
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.submissionDate" /></th>
						<td><c:out value='${sasScholarshipCandidacy.submissionDate}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.assignmentDate" /></th>
						<td><c:out value='${sasScholarshipCandidacy.assignmentDate}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.description" /></th>
						<td><c:out value='${sasScholarshipCandidacy.description}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.candidacyState" /></th>
						<td><c:out value='${sasScholarshipCandidacy.candidacyState.localizedName}' /></td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.gratuityAmount" /></th>
						<td><c:out value='${sasScholarshipCandidacy.gratuityAmount}' /></td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.cetQualificationOwner" /></th>
						<td><c:if test="${sasScholarshipCandidacy.cetQualificationOwner}"><spring:message code="label.true" /></c:if>
						<c:if test="${not sasScholarshipCandidacy.cetQualificationOwner}"><spring:message code="label.false" /></c:if>
						</td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.cstpQualificationOwner" /></th>
						<td><c:if test="${sasScholarshipCandidacy.cstpQualificationOwner}"><spring:message code="label.true" /></c:if>
						<c:if test="${not sasScholarshipCandidacy.cstpQualificationOwner}"><spring:message code="label.false" /></c:if></td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.phdQualificationOwner" /></th>
						<td><c:if test="${sasScholarshipCandidacy.phdQualificationOwner}"><spring:message code="label.true" /></c:if>
						<c:if test="${not sasScholarshipCandidacy.phdQualificationOwner}"><spring:message code="label.false" /></c:if></td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.degreeQualificationOwner" /></th>
						<td><c:if test="${sasScholarshipCandidacy.degreeQualificationOwner}"><spring:message code="label.true" /></c:if>
						<c:if test="${not sasScholarshipCandidacy.degreeQualificationOwner}"><spring:message code="label.false" /></c:if></td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.SasScholarshipCandidacy.masterQualificationOwner" /></th>
						<td><c:if test="${sasScholarshipCandidacy.masterQualificationOwner}"><spring:message code="label.true" /></c:if>
						<c:if test="${not sasScholarshipCandidacy.masterQualificationOwner}"><spring:message code="label.false" /></c:if></td>
					</tr>

				</tbody>
			</table>
		</form>
	</div>

<script>
	$(document).ready(function() {

	});
</script>
