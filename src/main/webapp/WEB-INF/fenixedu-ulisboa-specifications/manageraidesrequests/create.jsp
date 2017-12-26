
<%@page import="org.fenixedu.academic.domain.person.RoleType"%>
<%@page import="org.fenixedu.bennu.core.security.Authenticate"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesPeriodInputType"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.legal.report.raides.RaidesRequestsController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>

<spring:url var="datatablesUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl"
    value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl"
    value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css"
    href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%-- ${portal.toolkit()} --%>

<link
    href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dataTables.responsive.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/dataTables.responsive.js"></script>
<link
    href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link
    href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript"
    src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>

<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/angularjs-dropdown-multiselect.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/lodash.js/2.4.1/lodash.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageRaidesRequests.create" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= RaidesRequestsController.SEARCH_URL %>">
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

Array.prototype.contains = function(element){
    for(var i = 0; i < this.length; i++) {
        if(this[i] == element) {
            return i;
        }
    }
    return -1;
};

Array.prototype.containsId = function(elementId){
    for(var i = 0; i < this.length; i++) {
        if(this[i].id == elementId) {
            return i;
        }
    }
    return -1;
};

angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null
};

angular.isUndefinedOrNullOrEmpty = function(val) {
    return angular.isUndefinedOrNull(val) || val === "";
};

angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {

 	$scope.object=${beanJson};
 	$scope.degreeTypeDataSource = ${degreeTypeDataSource};
 	$scope.degreeDataSource = [];
 	$scope.executionYearDataSource = ${executionYearDataSource};
    $scope.getAcademicName = function(id) {
        var name;
        angular.forEach(
          	    $scope.executionYearDataSource,
                function(executionYear) {
                    if (executionYear.id == id) {
                        name = executionYear.text;
                    }
                }, id, name)
        return name;
    }
 	
 	$scope.postBack = createAngularPostbackFunction($scope);
 	
	$scope.booleanvalues = [
		{ name : '<spring:message code="label.no"/>', value : false },
		{ name : '<spring:message code="label.yes"/>', value : true} 
	];
	
	$scope.toggleCreateModal = function(periodType) {
	    var modalId = "#enrolledPeriodsModal_" + periodType;
        $(modalId).modal('toggle');
	};

	$scope.addPeriod = function(periodType) {
	    if($scope.isRequiredFieldsEmpty()) {
		    return;
	    };
	    if(angular.isUndefinedOrNull($scope.newPeriod.enrolmentEctsConstraint)) {
		    $scope.newPeriod.enrolmentEctsConstraint = false;
	    }	    
        if(angular.isUndefinedOrNull($scope.newPeriod.enrolmentYearsConstraint)) {
            $scope.newPeriod.enrolmentYearsConstraint = false;
        }       	    
	    var newPeriod = {};
	    newPeriod.academicPeriod = $scope.newPeriod.academicPeriod;
	    newPeriod.begin = $scope.newPeriod.begin;
	    newPeriod.end = $scope.newPeriod.end;
	    newPeriod.enrolledInAcademicPeriod = $scope.newPeriod.enrolledInAcademicPeriod;
	    newPeriod.periodInputType = periodType;
	    newPeriod.enrolmentEctsConstraint = $scope.newPeriod.enrolmentEctsConstraint;
	    if(newPeriod.enrolmentEctsConstraint) {
		    newPeriod.minEnrolmentEcts = $scope.newPeriod.minEnrolmentEcts;
		    newPeriod.maxEnrolmentEcts = $scope.newPeriod.maxEnrolmentEcts;
	    } else {
		    newPeriod.minEnrolmentEcts = 0;
            newPeriod.maxEnrolmentEcts = 0;
        }
	    newPeriod.enrolmentYearsConstraint = $scope.newPeriod.enrolmentYearsConstraint;
	    if(newPeriod.enrolmentYearsConstraint) {
		   newPeriod.minEnrolmentYears = $scope.newPeriod.minEnrolmentYears;
		   newPeriod.maxEnrolmentYears = $scope.newPeriod.maxEnrolmentYears;		
	    } else {
           newPeriod.minEnrolmentYears = 0;
           newPeriod.maxEnrolmentYears = 0;        
	    }
	    newPeriod.academicPeriodQualifiedName = $scope.getAcademicName(newPeriod.academicPeriod);
	    $scope.object.periods.push(newPeriod);
	    $scope.toggleCreateModal(periodType);
	};
	
	$scope.isRequiredFieldsEmpty = function () {
	    var isEmpty = false;
	    if($scope.newPeriod.enrolmentEctsConstraint) {
		    isEmpty = angular.isUndefinedOrNull($scope.newPeriod.minEnrolmentEcts) ||
 	           angular.isUndefinedOrNull($scope.newPeriod.maxEnrolmentEcts);
	    }
	    if($scope.newPeriod.enrolmentYearsConstraint) {
            isEmpty = angular.isUndefinedOrNull($scope.newPeriod.minEnrolmentYears) ||
               angular.isUndefinedOrNull($scope.newPeriod.maxEnrolmentYears);		
	    }
	    isEmpty = isEmpty || angular.isUndefinedOrNull($scope.newPeriod.academicPeriod) ||
            angular.isUndefinedOrNullOrEmpty($scope.newPeriod.begin) ||
            angular.isUndefinedOrNullOrEmpty($scope.newPeriod.end) ||
            angular.isUndefinedOrNull($scope.newPeriod.enrolledInAcademicPeriod);	    
	    return isEmpty;
	};
	
	$scope.isDegreeFieldEmpty = function () {
	    return angular.isUndefinedOrNull($scope.selectedDegree);
	};
	
 	$scope.cleanPeriods = function(periodType) {
		var i = $scope.object.periods.length;
		while(i--) {
			if($scope.object.periods[i].periodInputType === periodType) {
				$scope.object.periods.splice(i, 1);					
			}
		}
 	}
 	
 	$scope.onDegreeTypeChange = function () {
 	    var index = $scope.degreeTypeDataSource.containsId($scope.selectedDegreeType);
 	    $scope.selectedDegree = undefined;
 	    if(index == -1) {
 		    $scope.degreeDataSource = [];
 	    } else {
 		    $scope.degreeDataSource = $scope.degreeTypeDataSource[index].degreesDataSource;
 	    }
 	};
 	
 	$scope.addDegree = function () {
 	    var index = $scope.object.degrees.contains($scope.selectedDegree);
 	    if(index == -1) {
 		    $scope.object.degrees.push($scope.selectedDegree);
 	    }
 	};
 	
 	$scope.openDeleteModal = function (degreeIndex) {
 	    $scope.deleteDegreeIndex = degreeIndex;
 	    $('#deleteDegreeModal').modal('toggle');
 	}

    $scope.deleteDegree = function(degreeIndex) {
        $scope.object.degrees.splice(degreeIndex, 1);        
    };
 	
 	$scope.getDegreeTypeName = function(degreeId) {
        var name;
        angular.forEach(
                $scope.degreeTypeDataSource,
                function(degreeType) {
                    angular.forEach(
                	    degreeType.degreesDataSource,
                	    function(degree) {
                		    if(degree.id == degreeId) {
                			    name = degreeType.text;
                		    }                		
                	    }, degreeId, degreeType, name);
                }, degreeId, name);
        return name;
 	};
 	
 	$scope.getDegreeName = function(degreeId) {
        var name;
        angular.forEach(
                $scope.degreeTypeDataSource,
                function(degreeType) {
                    angular.forEach(
                        degreeType.degreesDataSource,
                        function(degree) {
                            if(degree.id == degreeId) {
                                name = degree.text;
                            }                       
                        }, degreeId, name);
                }, degreeId, name);
        return name; 	    
 	};
 	
 	$scope.submitForm = function() {
		$('#form').submit();
	}
 	
 	
}]);
</script>

<form id="form" name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
	action='${pageContext.request.contextPath}<%= RaidesRequestsController.CREATE_URL  %>'>

	<input name="bean" type="hidden" value="{{ object }}" />
	
	<jsp:include page="create_period_table.jsp">
		<jsp:param value="title.RaidesRequests.periodInputType.enrolled" name="periodTableTitle" />
		<jsp:param value="<%= RaidesPeriodInputType.ENROLLED.name() %>" name="periodType" />
	</jsp:include>

	<jsp:include page="create_period_table.jsp">
		<jsp:param value="title.RaidesRequests.periodInputType.graduated" name="periodTableTitle" />
		<jsp:param value="<%= RaidesPeriodInputType.GRADUATED.name() %>" name="periodType" />
	</jsp:include>

	<jsp:include page="create_period_table.jsp">
		<jsp:param value="title.RaidesRequests.periodInputType.internationalMobility" name="periodTableTitle" />
		<jsp:param value="<%= RaidesPeriodInputType.INTERNATIONAL_MOBILITY.name() %>" name="periodType" />
	</jsp:include>
	
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.agreementsForEnrolled" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="agreementsForEnrolledSelect" name="agreementsForEnrolled"
						ng-model="$parent.object.agreementsForEnrolled" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="registrationProtocol.id as registrationProtocol in object.registrationProtocolsDataSource | filter: $select.search">
							<span ng-bind-html="registrationProtocol.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
		</div>
	</div>	

	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.agreementsForMobility" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="agreementsForEnrolledSelect" name="agreementsForMobility"
						ng-model="$parent.object.agreementsForMobility" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="registrationProtocol.id as registrationProtocol in object.registrationProtocolsDataSource | filter: $select.search">
							<span ng-bind-html="registrationProtocol.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
		</div>
	</div>
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.ingressionsForDegreeChange" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="ingressionsForDegreeChangeSelect" name="ingressionsForDegreeChange"
						ng-model="$parent.object.ingressionsForDegreeChange" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.ingressionTypesDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
		</div>
	</div>
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.ingressionsForDegreeTransfer" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="ingressionsForDegreeTransferSelect" name="ingressionsForDegreeTransfer"
						ng-model="$parent.object.ingressionsForDegreeTransfer" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.ingressionTypesDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
		</div>
	</div>
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.ingressionsForGeneralAccessRegime" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="ingressionsForGeneralAccessRegimeSelect" name="ingressionsForGeneralAccessRegime"
						ng-model="$parent.object.ingressionsForGeneralAccessRegime" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.ingressionTypesDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
		</div>
	</div>
	
      <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title"><spring:message code="label.RaidesRequests.degrees" /></h3>
        </div>
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-1"></div>
                <div class="col-sm-1 control-label"><spring:message code="label.RaidesRequests.degreeType"/></div>
                <div class="col-sm-6">
                    <ui-select id="raidesRequest_degreeType" class="" name="degreetype" ng-model="$parent.selectedDegreeType" on-select="onDegreeTypeChange()" theme="bootstrap" ng-disabled="disabled" >
                                    <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
                                    <ui-select-choices repeat="degreeType.id as degreeType in degreeTypeDataSource | filter: $select.search">
                                            <span ng-bind-html="degreeType.text | highlight: $select.search"></span>
                                    </ui-select-choices>
                            </ui-select>                
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-1"></div>
                <div class="col-sm-1 control-label"><spring:message code="label.RaidesRequests.degree"/></div>
                <div class="col-sm-6">
                    <ui-select id="raidesRequest_degree" class="" name="degree" ng-model="$parent.selectedDegree" theme="bootstrap" >
                                    <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
                                    <ui-select-choices repeat="degree.id as degree in degreeDataSource | filter: $select.search">
                                            <span ng-bind-html="degree.text | highlight: $select.search"></span>
                                    </ui-select-choices>
                            </ui-select>                
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2"></div>
                <div class="col-sm-1">
                    <button type="button" class="btn btn-primary" ng-disabled="isDegreeFieldEmpty()" ng-click="addDegree()">
                        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                    </button>
                </div>
            </div>
        </div>
        
        <div class="panel panel-body">
            <div class="col-sm-8">
                <table id="raidesRequest_degree" class="table responsive table-bordered table-hover" width="100%" >
                    <thead>
                        <tr>
                            <th style="width:30%"><spring:message code="label.RaidesRequests.degreeType" /></th>
                            <th style="width:50%"><spring:message code="label.RaidesRequests.degree" /></th>
                            <th style="width:20%"></th>
                        </tr>
                    </thead>            
                    <tbody>
                        <tr ng-repeat="degree in object.degrees">
                            <td>{{ getDegreeTypeName(degree) }}</td>
                            <td>{{ getDegreeName(degree) }}</td>
                            <td>
                            	<button type="button" class="bnt btn-danger btn-xs" role="button" ng-click="deleteDegree($index)">
                           		    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                    &nbsp;
                            		<spring:message code="label.remove" />
                            	</button>
                            </td>
                        </tr>
                    </tbody>
                </table>   
            </div>
        </div>
    </div>
    
    <div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.institution" />
				</div>

				<div class="col-sm-10">
					{{ object.institutionName }}
				</div>
				
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.institutionCode" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.institutionCode" name="code" />
				</div>
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.moment" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.moment" name="code" />
				</div>
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.interlocutorName" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorName" name="code" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.interlocutorPhone" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorPhone" name="interlocutorphone" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.interlocutorEmail" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorEmail" name="code" />
				</div>
			</div>
			<% if (RoleType.MANAGER.isMember(Authenticate.getUser())) {%>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.studentNumber" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.studentNumber" name="studentNumber" />
				</div>
			</div>
			<% } %>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.filterEntriesWithErrors" />
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
					<spring:message code="label.RaidesRequests.graduatedExecutionYear" />
				</div>
				
				<div class="col-sm-10">
					<ui-select	id="graduatedExecutionYear" name="graduatedExecutionYear" ng-model="$parent.object.graduatedExecutionYear" theme="bootstrap">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices	repeat="executionYear.id as executionYear in object.executionYearsDataSource | filter: $select.search">
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
