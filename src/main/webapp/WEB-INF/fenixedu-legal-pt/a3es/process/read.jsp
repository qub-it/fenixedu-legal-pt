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
	
					$scope.postBack = createAngularPostbackFunction($scope);
	
					// Begin here of Custom Screen business JS - code
					
					$scope.delete = function() {	
						$scope.showConfirmation('${pageContext.request.contextPath}<%=A3esProcessController.DELETE_URL%>${process.externalId}', 
								'<spring:message code="label.readA3esProcess.confirmDelete" />', 
								'<spring:message code="label.remove" />');
					}
					
                    $scope.showConfirmation = function(url,message,actionText) {
                        $('#confirmationForm').attr('action', url);
                        $('#confirmationMessage').html(message);
                        $('#confirmationButton').html(actionText);
                        $('#confirmationModal').modal('toggle')
                    }
                    
			   }]);
</script>


<div class="modal fade" id="confirmationModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="confirmationForm"	action="#" method="post">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirmation" />
					</h4>
				</div>
				<div class="modal-body">
					<p id="confirmationMessage"></p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.close" />
					</button>
					<button id="confirmationButton" class="btn btn-danger" type="submit">
						
					</button>
				</div>
			</form>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<div ng-app="angularAppA3esProcess" ng-controller="A3esProcessController">

	<%-- TITLE --%>
	<div class="page-header">
		<h1>
			<spring:message code="label.readA3esProcess" />
			<small></small>
		</h1>
	</div>
	
	<%-- NAVIGATION --%>
	<div class="well well-sm" style="display: inline-block">
		<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esProcessController.CONTROLLER_URL%>"><spring:message
				code="label.event.back" /></a>

		&nbsp;|&nbsp; <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esProcessController.UPDATE_URL%>${process.externalId}"><spring:message
				code="label.event.update" /></a>

		&nbsp;|&nbsp; <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;<a class="" href="#" 
					ng-click="delete()"><spring:message code="label.event.delete" /></a>

		&nbsp;|&nbsp; <span class="glyphicon glyphicon-list" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esProcessController.READ_URL%>${process.externalId}/viewinfo/"><spring:message
				code="label.infoFiles" /></a>
				
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-user" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esProcessController.READ_URL%>${process.externalId}/viewteachers/"><spring:message
				code="label.teacherFiles" /></a>
				
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-book" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esProcessController.READ_URL%>${process.externalId}/viewcourses/"><spring:message
				code="label.courseFiles" /></a>
				
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
		
		<spring:message code="label.yes" var="yesLabel" />	
		<spring:message code="label.no" var="noLabel" />
	
		<div class="panel-body">
			<form method="post" class="form-horizontal">
				<table class="table">
					<tbody>
						<tr>
							<th scope="row" class="col-xs-2"><spring:message code="label.creationDate" /></th>
							<td><joda:format value="${process.versioningCreationDate}" pattern="yyyy-MM-dd HH:mm" /></td>
						</tr>
						<tr>
							<th scope="row" class="col-xs-2"><spring:message code="label.A3esProcess.name" /></th>
							<td><c:out value="${process.name}"/></td>
						</tr>
						<tr>
							<th scope="row" class="col-xs-2"><spring:message code="label.A3esPeriod.description" /></th>
							<td><c:out value="${process.description}"/></td>
						</tr>
						<tr>
							<th scope="row" class="col-xs-2"><spring:message code="label.A3esPeriod.degreeCurricularPlan" /></th>
							<td><c:out value="${process.planDescription}"/></td>
						</tr>
						<tr>
							<th scope="row" class="col-xs-2"><spring:message code="label.A3esPeriod.fillInDateBegin" /></th>
							<td><joda:format value="${process.period.fillInDateBegin}" pattern="yyyy-MM-dd HH:mm" /></td>
						</tr>
						<tr>
							<th scope="row" class="col-xs-2"><spring:message code="label.A3esPeriod.fillInDateEnd" /></th>
							<td><joda:format value="${process.period.fillInDateEnd}" pattern="yyyy-MM-dd HH:mm" /></td>
						</tr>
					</tbody>
				</table>
			</form>
		</div>
	</div>
	
</div>
