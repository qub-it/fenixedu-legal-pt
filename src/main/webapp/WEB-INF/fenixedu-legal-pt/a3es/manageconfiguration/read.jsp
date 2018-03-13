<%@page import="org.fenixedu.legalpt.ui.a3es.A3esConfigurationController"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageA3esConfiguration.read" />
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">

	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> &nbsp; <a class=""
		href="${pageContext.request.contextPath}<%= A3esConfigurationController.EDIT_URL %>"> <spring:message
			code="label.event.update" />
	</a>
</div>

<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
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

		<spring:message code="label.yes" var="yesLabel" />
		<spring:message code="label.no" var="noLabel" />
	
		<form method="post" class="form-horizontal">
			<table class="table">
				<tbody>

					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.A3esInstance.mobilityAgreements" /></th>
						<td><c:forEach var="each" items="${instance.mobilityAgreementsSet}">
								<c:out escapeXml="false" value="${each.code} - ${each.description.content} <br/>" />
							</c:forEach>
						</td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-4"><spring:message code="label.A3esInstance.studyCycleByDegree" /></th>
						<td>
							<c:out value="${instance.studyCycleByDegree ? yesLabel : noLabel }"></c:out>
						</td>
					</tr>
										
					<tr>
						<th scope="row" class="col-xs-4"><spring:message code="label.A3esInstance.groupCourseProfessorshipByPerson" /></th>
						<td>
							<c:out value="${instance.groupCourseProfessorshipByPerson ? yesLabel : noLabel }"></c:out>
						</td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-4"><spring:message code="label.A3esInstance.groupPersonProfessorshipByCourse" /></th>
						<td>
							<c:out value="${instance.groupPersonProfessorshipByCourse ? yesLabel : noLabel }"></c:out>
						</td>
					</tr>

				</tbody>
			</table>
		</form>
	</div>
</div>
