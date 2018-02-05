<%@page import="org.fenixedu.legalpt.dto.a3es.A3esPeriodBean"%>
<%@page import="org.fenixedu.legalpt.domain.a3es.A3esPeriod"%>
<%@page import="org.fenixedu.legalpt.ui.a3es.A3esPeriodController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
	
					$scope.postBack = createAngularPostbackFunction($scope);
	
					// Begin here of Custom Screen business JS - code
					
					$scope.delete = function() {	
						$scope.showConfirmation('${pageContext.request.contextPath}<%=A3esPeriodController.DELETE_URL%>${period.externalId}', 
								'<spring:message code="label.readA3esPeriod.confirmDelete" />', 
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


<div ng-app="angularAppA3esPeriod" ng-controller="A3esPeriodController">

	<%-- TITLE --%>
	<div class="page-header">
		<h1>
			<spring:message code="label.readA3esPeriod" />
			<small></small>
		</h1>
	</div>
	
	<%-- NAVIGATION --%>
	<div class="well well-sm" style="display: inline-block">
		<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esPeriodController.CONTROLLER_URL%>"><spring:message
				code="label.event.back" /></a>

		&nbsp;|&nbsp; <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esPeriodController.UPDATE_URL%>${period.externalId}"><spring:message
				code="label.event.update" /></a>

		&nbsp;|&nbsp; <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;<a class="" href="#" 
					ng-click="delete()"><spring:message code="label.event.delete" /></a>

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
							<td><joda:format value="${period.versioningCreationDate}" pattern="yyyy-MM-dd HH:mm" /></td>
						</tr>
						<tr>
							<th scope="row" class="col-xs-2"><spring:message code="label.A3esPeriod.executionYear" /></th>
							<td><c:out value="${period.executionYear.qualifiedName}"/></td>
						</tr>
	                    <tr>
	                        <th scope="row" class="col-xs-2"><spring:message code="label.A3esPeriod.type" /></th>
	                        <td><c:out value="${period.type.code}"/> - <c:out value="${period.type.localizedName.content}"/></td>
	                    </tr>
						<tr>
							<th scope="row" class="col-xs-2"><spring:message code="label.A3esPeriod.fillInDateBegin" /></th>
							<td><joda:format value="${period.fillInDateBegin}" pattern="yyyy-MM-dd HH:mm" /></td>
						</tr>
						<tr>
							<th scope="row" class="col-xs-2"><spring:message code="label.A3esPeriod.fillInDateEnd" /></th>
							<td><joda:format value="${period.fillInDateEnd}" pattern="yyyy-MM-dd HH:mm" /></td>
						</tr>
					</tbody>
				</table>
			</form>
		</div>
	</div>
	
</div>
