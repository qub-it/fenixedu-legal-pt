<%@page import="org.fenixedu.legalpt.ui.raides.RaidesConfigurationController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />


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

