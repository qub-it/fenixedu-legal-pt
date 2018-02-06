<%@page import="org.fenixedu.legalpt.ui.settings.LegalSettingsController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageLegalSettings.read" />
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= LegalSettingsController.EDIT_URL %>">
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
							<spring:message code="label.LegalSettings.numberOfLessonWeeks" />
						</th>
						<td>
							<c:out value='${instance.numberOfLessonWeeks}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.LegalSettings.a3esURL" />
						</th>
						<td>
							<c:out value='${instance.a3esURL}' />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

