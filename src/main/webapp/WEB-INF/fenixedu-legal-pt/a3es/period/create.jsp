<%@page import="org.fenixedu.legalpt.ui.a3es.A3esPeriodController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<jsp:include page="../../commons/angularInclude.jsp" />

<script type="text/javascript">
    angular
	    .module('angularAppA3esPeriod',
		    [ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
	    .controller(
		    'A3esPeriodController',
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
	
					$scope.object = ${periodBeanJson};
					$scope.form = {};
					$scope.form.object = $scope.object;
	
					$scope.postBack = createAngularPostbackFunction($scope);
					
					// Begin here of Custom Screen business JS - code
	
					$scope.onBeanChange = function(model) {
						$scope.postBack(model);
					}
					
					$scope.create = function() {
						$('#createForm').submit();
					}

			    } ]);
</script>


<form id="createForm" name='form' method="post" class="form-horizontal" ng-app="angularAppA3esPeriod"
	ng-controller="A3esPeriodController"
	action='${pageContext.request.contextPath}<%=A3esPeriodController.CREATE_URL%>'>

	<input type="hidden" name="postback" value='${pageContext.request.contextPath}<%=A3esPeriodController.CREATEPOSTBACK_URL%>' />
	<input name="bean" type="hidden" value="{{ object }}" />
	
	<%-- TITLE --%>
	<div class="page-header">
		<h1>
			<spring:message code="label.createA3esPeriod" />
			<small></small>
		</h1>
	</div>
	
	<%-- NAVIGATION --%>
	<div class="well well-sm" style="display: inline-block">
		<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esPeriodController.CONTROLLER_URL%>"><spring:message
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
					<spring:message code="label.A3esPeriod.executionYear" />
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
					<spring:message code="label.A3esPeriod.type" />
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
                    <spring:message code="label.A3esPeriod.fillInDateBegin" />
                </div>
                <div class="col-sm-2">
                	<input class="form-control" type="text" bennu-date-time="object.fillInDateBegin" required="true"/>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.A3esPeriod.fillInDateEnd" />
                </div>
                <div class="col-sm-2">
                	<input class="form-control" type="text" bennu-date-time="object.fillInDateEnd" required="true"/>
                </div>
            </div>
            
		</div>
		<div class="panel-footer">
			<button type="button" class="btn btn-primary" role="button" ng-click="create()"><spring:message code="label.create" /></button>
		</div>
	</div>

</form>
