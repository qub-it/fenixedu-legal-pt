<%@page import="org.fenixedu.legalpt.dto.a3es.A3esProcessBean"%>
<%@page import="org.fenixedu.legalpt.domain.a3es.A3esProcess"%>
<%@page import="org.fenixedu.legalpt.ui.a3es.A3esProcessController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
	
					$scope.postBack = createAngularPostbackFunction($scope);
	
					// Begin here of Custom Screen business JS - code
					
					$scope.update = function(){
						$('#updateForm').submit();
					}
				

			    } ]);
</script>


<form id="updateForm" name='form' method="post" class="form-horizontal" ng-app="angularAppA3esProcess"
	ng-controller="A3esProcessController"
	action='${pageContext.request.contextPath}<%=A3esProcessController.UPDATE_URL%>${process.externalId}'>

	<input type="hidden" name="postback" value='${pageContext.request.contextPath}<%=A3esProcessController.UPDATEPOSTBACK_URL%>${process.externalId}' />
	<input name="bean" type="hidden" value="{{ object }}" />

	<%-- TITLE --%>
	<div class="page-header">
		<h1>
			<spring:message code="label.updateA3esProcess" />
			<small></small>
		</h1>
	</div>
	
	<%-- NAVIGATION --%>
	<div class="well well-sm" style="display: inline-block">
		<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esProcessController.READ_URL%>${process.externalId}"><spring:message
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
					<spring:message code="label.creationDate" />
				</div>
				<div class="col-sm-6">
					<joda:format value="${process.versioningCreationDate}" pattern="yyyy-MM-dd HH:mm" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.A3esProcess.name" />
				</div>
				<div class="col-sm-6">
					<c:out value="${process.name}" />
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
			<button type="button" class="btn btn-primary" role="button" ng-click="update()"><spring:message code="label.submit" /></button>
		</div>
	</div>
</form>


