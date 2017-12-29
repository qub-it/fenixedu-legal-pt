<%@page
	import="org.fenixedu.legalpt.ui.academicinstitutions.AcademicInstitutionsController"%>
<%@page import="org.fenixedu.academic.domain.Country"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../commons/angularInclude.jsp" />

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message
			code="label.AcademicInstitutionsController.viewdegreedesignationdetail.title" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
	<a class=""
		href="${pageContext.request.contextPath}<%= AcademicInstitutionsController.VIEW_DEGREE_DESIGNATIONS_URL %>">
		<spring:message code="label.back" />
	</a>
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
					${pageContext.request.contextPath} aria-hidden="true">&nbsp;</span>
				${message}
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
			<spring:message code="label.details" />
		</h3>
	</div>
	
	<div class="panel-body">
		<form method="post" class="form-horizontal">
			<table class="table">
				<tbody>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.DegreeDesignation.code" /></th>
						<td><c:out value="${degreeDesignation.code}" /></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.DegreeDesignation.description" /></th>
						<td><c:out value="${degreeDesignation.description}" /></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.DegreeDesignation.degreeClassification" /></th>
						<td><c:out value="${degreeDesignation.degreeClassification.code}" /></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
	
</div>


<table id="institutionsTable" class="table responsive table-bordered table-hover">
	<thead>
		<tr>
			<th><spring:message code="label.AcademicInstitutionsController.official.code" /></th>
			<th><spring:message code="label.AcademicInstitutionsController.academicUnit.name" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="institution" items="${degreeDesignation.institutionUnitSet}">
			<tr>
				<td><c:out value="${institution.code}"/></td>
				<td><c:out value="${institution.nameI18n.content}"/></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<script type="text/javascript">
	createDataTables('institutionsTable',true /*filterable*/, false /*show tools*/, true /*paging*/, "${pageContext.request.contextPath}","${datatablesI18NUrl}");
</script>
