<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<script src="${pageContext.request.contextPath}/static/integration/sas/js/bootbox.min.js"></script>
<script src="${pageContext.request.contextPath}/static/integration/sas/js/omnis.js"></script>

${portal.toolkit()}

<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.manageScholarshipsConfiguration.readSocialServicesConfiguration" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/integration/sas/managescholarshipsconfiguration/socialservicesconfiguration/update/"  ><spring:message code="label.event.update" /></a>
</div>
	<c:if test="${not empty infoMessages}">
				<div class="alert alert-info" role="alert">
					
					<c:forEach items="${infoMessages}" var="message"> 
						<p>${message}</p>
					</c:forEach>
					
				</div>	
			</c:if>
			<c:if test="${not empty warningMessages}">
				<div class="alert alert-warning" role="alert">
					
					<c:forEach items="${warningMessages}" var="message"> 
						<p>${message}</p>
					</c:forEach>
					
				</div>	
			</c:if>
			<c:if test="${not empty errorMessages}">
				<div class="alert alert-danger" role="alert">
					
					<c:forEach items="${errorMessages}" var="message"> 
						<p>${message}</p>
					</c:forEach>
					
				</div>	
			</c:if>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="label.details"/></h3>
	</div>
	<div class="panel-body">
<form method="post" class="form-horizontal">
<table class="table">
		<tbody>
<tr>
	<th scope="row" class="col-xs-3"><spring:message code="label.SocialServicesConfiguration.numberOfMonthsOfAcademicYear"/></th> 
	<td>
		<c:out value='${socialServicesConfiguration.numberOfMonthsOfAcademicYear}'/>
	</td> 
</tr>
<tr>
	<th scope="row" class="col-xs-3"><spring:message code="label.SocialServicesConfiguration.email"/></th> 
	<td>
		<c:out value='${socialServicesConfiguration.email}'/>
	</td> 
</tr>
<tr>
	<th scope="row" class="col-xs-3"><spring:message code="label.SocialServicesConfiguration.institutionCode"/></th> 
	<td>
		<c:out value='${socialServicesConfiguration.institutionCode}'/>
	</td> 
</tr>
<tr>
	<th scope="row" class="col-xs-3"><spring:message code="label.SocialServicesConfiguration.ingressionTypeWhichAreDegreeTransfer"/></th> 
	<td>
		<ul>
		<c:forEach items="${socialServicesConfiguration.ingressionTypeWhichAreDegreeTransfer}" var="element">
			<li>
				<c:out value="${element.localizedName}" /> 
			</li>
		</c:forEach>
		<ul>
	</td> 
</tr>
<tr>
	<th scope="row" class="col-xs-3"><spring:message code="label.SocialServicesConfiguration.ingressionTypesWithExternalData"/></th> 
	<td>
		<ul>
		<c:forEach items="${socialServicesConfiguration.ingressionTypesWithExternalData}" var="element">
			<li>
				<c:out value="${element.localizedName}" /> 
			</li>
		</c:forEach>
		<ul>
	</td> 
</tr>
<tr>
	<th scope="row" class="col-xs-3"><spring:message code="label.SocialServicesConfiguration.creditsReasonType"/></th> 
	<td>
		<ul>
		<c:forEach items="${socialServicesConfiguration.creditsReasonTypes}" var="element">
			<li>
				<c:out value="${element.reason.content}" /> 
			</li>
		</c:forEach>
		<ul>
	</td> 
</tr>
<tr>
	<th scope="row" class="col-xs-3"><spring:message code="label.SocialServicesConfiguration.statuteTypeSas"/></th> 
	<td>
		<c:out value="${socialServicesConfiguration.statuteTypeSas.name.content}" /> 
	</td> 
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
