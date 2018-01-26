<%@page import="org.fenixedu.legalpt.ui.a3es.A3esProcessController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<jsp:include page="../../commons/angularInclude.jsp" />

<script type="text/javascript">
    angular
	    .module('angularAppA3esProcess',
		    [ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
	    .controller(
		    'A3esProcessController',
		    [
			    '$scope',
			    function($scope) {

					$scope.booleanvalues = [
						{
						    name : '<spring:message code="label.no"/>',
						    value : false
						},
						{
						    name : '<spring:message code="label.yes"/>',
						    value : true
						} ];
	
					$scope.object = ${processBeanJson};
					$scope.form = {};
					$scope.form.object = $scope.object;
	
					$scope.postBack = createAngularPostbackFunction($scope);
					
					// Begin here of Custom Screen business JS - code
	
					$scope.onExecutionYearChange = function(model) {					
						$scope.object.degreeCurricularPlan = '';
						$scope.onBeanChange(model);
					}
					
					$scope.onBeanChange = function(model) {
						$scope.postBack(model);
					}
					
					$scope.create = function() {
						$('#createForm').submit();
					}

			    } ]);
</script>


<form id="createForm" name='form' method="post" class="form-horizontal" ng-app="angularAppA3esProcess"
	ng-controller="A3esProcessController"
	action='${pageContext.request.contextPath}<%=A3esProcessController.CREATE_URL%>'>

	<input type="hidden" name="postback" value='${pageContext.request.contextPath}<%=A3esProcessController.CREATEPOSTBACK_URL%>' />
	<input name="bean" type="hidden" value="{{ object }}" />
	
	<%-- TITLE --%>
	<div class="page-header">
		<h1>
			<spring:message code="label.createA3esProcess" />
			<small></small>
		</h1>
	</div>
	
	<%-- NAVIGATION --%>
	<div class="well well-sm" style="display: inline-block">
		<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esProcessController.CONTROLLER_URL%>"><spring:message
				code="label.event.back" /></a>
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

	<div class="panel panel-default">
		<div class="panel-body">
		
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.A3esProcess.executionYear" />
				</div>
				<div class="col-sm-2">
					<ui-select	id="executionYearSelect" name="executionYear" ng-model="$parent.object.executionYear" theme="bootstrap" on-select="onExecutionYearChange($model)" on-remove="onExecutionYearChange($model)">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices	repeat="executionYear.id as executionYear in object.executionYearDataSource | filter: $select.search">
							<span ng-bind-html="executionYear.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.A3esProcess.type" />
				</div>
				<div class="col-sm-6">
					<ui-select	id="typeSelected" name="type" ng-model="$parent.object.type" theme="bootstrap">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices	repeat="type.id as type in object.typeDataSource | filter: $select.search">
							<span ng-bind-html="type.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.A3esProcess.identifier" />
				</div>
				<div class="col-sm-6">
					<input id="identifier" name="identifier" ng-model="object.identifier" class="form-control" type="text"/>
				</div>
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.A3esPeriod.description" />
				</div>
				<div class="col-sm-6">
					<input id="description" name="description" ng-model="object.description" class="form-control" type="text"/>
				</div>
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.A3esPeriod.degreeCurricularPlan" />
				</div>
				<div class="col-sm-6">
					<ui-select	id="degreeCurricularPlanSelect" name="degreeCurricularPlan" ng-model="$parent.object.degreeCurricularPlan" theme="bootstrap">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices	repeat="degreeCurricularPlan.id as degreeCurricularPlan in object.degreeCurricularPlanDataSource | filter: $select.search">
							<span ng-bind-html="degreeCurricularPlan.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>
				</div>
			</div>
			            
		</div>
		<div class="panel-footer">
			<button type="button" class="btn btn-primary" role="button" ng-click="create()"><spring:message code="label.create" /></button>
		</div>
	</div>

</form>
