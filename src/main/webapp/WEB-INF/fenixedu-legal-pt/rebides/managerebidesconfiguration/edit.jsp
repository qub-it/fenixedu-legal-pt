<%@page import="org.fenixedu.legalpt.ui.rebides.RebidesConfigurationController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageRebidesConfiguration.edit" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= RebidesConfigurationController.READ_URL %>">
		<spring:message code="label.event.back" />
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

<script>

angular.module('angularApp', ['ngSanitize', 'ui.select']).controller('angularController', ['$scope', function($scope) {

 	$scope.object=${beanJson};
 	$scope.postBack = createAngularPostbackFunction($scope);
 	
	$scope.booleanvalues = [
    		{ name : '<spring:message code="label.no"/>', value : false },
    		{ name : '<spring:message code="label.yes"/>', value : true } 
	];
	                 	
 	
}]);
</script>

<form id="form" name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
	action='${pageContext.request.contextPath}<%= RebidesConfigurationController.EDIT_URL  %>'>

	<input name="bean" type="hidden" value="{{ object }}" />
	
	
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesInstance.institutionCode" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.institutionCode" name="institutionCode" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesInstance.interlocutorName" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorName" name="interlocutorName" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesInstance.interlocutorEmail" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="email" ng-model="object.interlocutorEmail" name="interlocutorEmail" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesInstance.interlocutorPhone" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorPhone" name="interlocutorPhone" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesInstance.passwordToZip" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.passwordToZip" name="passwordToZip" />
				</div>
			</div>			
			
		</div>
		
		<div class="panel-footer">
			<input type="submit" class="btn btn-default btn-xs" value="<spring:message code="label.submit" />" />
		</div>
	</div>	
	
</form>

<script>

$(document).ready(function() {
});

</script>
