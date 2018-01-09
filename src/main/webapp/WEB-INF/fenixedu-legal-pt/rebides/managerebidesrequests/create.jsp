<%@page import="org.fenixedu.legalpt.ui.rebides.RebidesRequestsController"%>
<%@page import="org.fenixedu.academic.domain.person.RoleType"%>
<%@page import="org.fenixedu.bennu.core.security.Authenticate"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>

<jsp:include page="../../commons/angularInclude.jsp" />

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageRebidesRequests.create" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= RebidesRequestsController.SEARCH_URL %>">
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

angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {

 	$scope.object=${beanJson};

 	$scope.executionYearDataSource = ${executionYearDataSource};
 	
 	$scope.postBack = createAngularPostbackFunction($scope);
 	
	$scope.booleanvalues = [
		{ name : '<spring:message code="label.no"/>', value : false },
		{ name : '<spring:message code="label.yes"/>', value : true} 
	];
 	
 	$scope.submitForm = function(e) {
		$('#form').submit();
	}
 	
 	
}]);
</script>

<form id="form" name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
	action='${pageContext.request.contextPath}<%= RebidesRequestsController.CREATE_URL  %>'>

	<input name="bean" type="hidden" value="{{ object }}" />
	
    
    <div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesRequests.institutionCode" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.institutionCode" name="institutionCode" required="required" />
				</div>
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesRequests.moment" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.moment" name="moment" />
				</div>
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesRequests.interlocutorName" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorName" name="interlocutorName" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesRequests.interlocutorPhone" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorPhone" name="interlocutorPhone" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesRequests.interlocutorEmail" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="email" ng-model="object.interlocutorEmail" name="interlocutorEmail" />
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesRequests.filterEntriesWithErrors" />
				</div>
				
				<div class="col-sm-10">
                    <select 
                        name="filterEntriesWithErrors" class="form-control"
                        ng-model="object.filterEntriesWithErrors"
                        ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
                    </select>
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RebidesRequests.executionYear" />
				</div>
				
				<div class="col-sm-10">
					<ui-select	id="executionYear" name="executionYear" ng-model="$parent.object.executionYear" theme="bootstrap">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices	repeat="executionYear.id as executionYear in executionYearDataSource | filter: $select.search">
							<span ng-bind-html="executionYear.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>
				</div>
			</div>			
			
			
		</div>
		<div class="panel-footer">
			<button type="button" class="btn btn-primary" role="button" ng-click="submitForm()"><spring:message code="label.submit" /></button>
		</div>
	</div>	
	
	
</form>

<script>

$(document).ready(function() {
});

</script>
