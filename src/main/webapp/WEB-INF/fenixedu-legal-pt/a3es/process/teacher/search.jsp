<%@page import="org.fenixedu.legalpt.dto.a3es.A3esProcessBean"%>
<%@page import="org.fenixedu.legalpt.domain.a3es.A3esProcess"%>
<%@page import="org.fenixedu.legalpt.ui.a3es.A3esProcessTeacherController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../../../commons/angularInclude.jsp" />

<script type="text/javascript">
    angular
	    .module('angularAppA3esProcess',
		    [ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
	    .controller(
		    'A3esProcessTeacherController',
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
	
					$scope.onBeanChange = function(model) {
						
						$scope.object.type = '';
						$scope.object.fillInDateBegin = '';
						$scope.object.fillInDateEnd = '';
						
						$scope.postBack(model);
					}
					
					$scope.search  = function() {
						$('#searchForm').submit();
					}

			    } ]);
</script>


<form id="searchForm" name='form' method="post" class="form-horizontal" 
	ng-app="angularAppA3esProcess" ng-controller="A3esProcessTeacherController"
	action="${pageContext.request.contextPath}<%=A3esProcessTeacherController.SEARCH_URL%>">

	<input type="hidden" name="postback" value='${pageContext.request.contextPath}<%=A3esProcessTeacherController.SEARCHPOSTBACK_URL%>${process.externalId}' />
	<input name="bean" type="hidden" value="{{ object }}" />
	
	<%-- TITLE --%>
	<div class="page-header">
		<h1>
			<spring:message code="label.searchA3esProcess" />
			<small></small>
		</h1>
	</div>
	
	<%-- NAVIGATION --%>
	<div class="well well-sm" style="display: none">
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
						<ui-select	id="executionYearSelect" name="executionYear" ng-model="$parent.object.executionYear" theme="bootstrap" on-select="onBeanChange($model)" on-remove="onBeanChange($model)">
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
			</div>
			<div class="panel-footer">
				<button type="button" class="btn btn-primary" ng-click="search()"><spring:message code="label.search" /></button> 
			</div>
	</div>
	
	<c:choose>
		<c:when test="${not empty searchResults}">
			<spring:message code="label.yes" var="yesLabel"/>
			<spring:message code="label.no" var="noLabel"/>
		
			<table id="searchTable" class="table table-bordered table-hover" width="100%">
				<thead>
					<tr>
						<th><spring:message code="label.creationDate" /></th>
						<th><spring:message code="label.A3esProcess.name" /></th>
						<th><spring:message code="label.A3esPeriod.degreeCurricularPlan" /></th>
						<%-- Operations Column --%>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="searchResult" items="${searchResults}">
					<tr>
						<td><joda:format value="${searchResult.versioningCreationDate}" pattern="yyyy-MM-dd HH:mm" /></td>
						<td><c:out value="${searchResult.name}" /></td>
						<td><c:out value="${searchResult.planDescription}" /></td>
						<td>
							<a  class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%=A3esProcessTeacherController.SEARCH_TO_VIEW_ACTION_URL%>${searchResult.externalId}"><spring:message code='label.view'/></a>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<script type="text/javascript">
				createDataTables('searchTable',true /*filterable*/, false /*show tools*/, true /*paging*/, "${pageContext.request.contextPath}","${datatablesI18NUrl}");
			</script>
					
		</c:when>
		
		<c:otherwise>
			<div class="alert alert-warning" role="alert">
	
				<p>
					<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
					<spring:message code="label.noResultsFound" />
				</p>
	
			</div>
	
		</c:otherwise>
	</c:choose>

</form>
