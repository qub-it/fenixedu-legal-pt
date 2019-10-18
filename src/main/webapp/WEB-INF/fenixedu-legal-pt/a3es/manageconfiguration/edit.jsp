<%@page import="org.fenixedu.legalpt.ui.a3es.A3esConfigurationController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageA3esConfiguration.edit" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= A3esConfigurationController.READ_URL %>">
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
	action='${pageContext.request.contextPath}<%= A3esConfigurationController.EDIT_URL  %>'>

	<input name="bean" type="hidden" value="{{ object }}" />
	
	
	<div class="panel panel-default">
		<div class="panel-body">
		
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.A3esInstance.a3esUrl" />
				</div>

				<div class="col-sm-6">
					<input class="form-control" type="text" ng-model="object.a3esUrl" name="a3esUrl" />
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.A3esInstance.mobilityAgreements" />
				</div>

				<div class="col-sm-6">
					<ui-select id="mobilityAgreementsSelect" name="mobilityAgreements"
						ng-model="$parent.object.mobilityAgreements" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="registrationProtocol.id as registrationProtocol in object.registrationProtocolsDataSource | filter: $select.search">
							<span ng-bind-html="registrationProtocol.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.A3esInstance.studyCycleByDegree" />
				</div>

				<div class="col-sm-6">
                    <select id="studyCycleByDegree" name="studyCycleByDegree"
                        class="form-control" ng-model="object.studyCycleByDegree"
                        ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
                    </select>
				</div>
			</div>
						
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.A3esInstance.groupCourseProfessorshipByPerson" />
				</div>

				<div class="col-sm-6">
                    <select id="groupCourseProfessorshipByPerson" name="groupCourseProfessorshipByPerson"
                        class="form-control" ng-model="object.groupCourseProfessorshipByPerson"
                        ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
                    </select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.A3esInstance.groupPersonProfessorshipByCourse" />
				</div>

				<div class="col-sm-6">
                    <select id="groupPersonProfessorshipByCourse" name="groupPersonProfessorshipByCourse"
                        class="form-control" ng-model="object.groupPersonProfessorshipByCourse"
                        ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
                    </select>
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
