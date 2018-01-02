<%@page import="org.fenixedu.legalpt.ui.raides.ManageLegalMappingController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />


<script type="text/javascript">
function openCreateModal() {
    $("#createModal").modal("toggle");
}

function openDeleteModal(externalId) {
    url = "${pageContext.request.contextPath}<%= ManageLegalMappingController.DELETE_URL %>/" + externalId;
    $("#deleteForm").attr("action", url);
    $('#deleteModal').modal('toggle');
}
</script>

<div class="modal fade" id="createModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="createLegalMappingForm" action="${pageContext.request.contextPath}<%= ManageLegalMappingController.CREATE_URL%>" method="POST">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                    aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">
                    <spring:message code="label.LegalMapping.createModal" />
                </h4>
            </div>
            <div class="modal-body">
                <div class="form-group row">
                    <div class="col-sm-2 control-label">
                        <spring:message code="label.LegalMappingType.name"/>
                    </div>
                    <div class="col-sm-10 control-label">
                        <select id="selectedType" name="selectedType" class="form-control">
                            <c:forEach var="legalMappingType" items="${ possibleLegalMappingTypes }">
                                <option value="${ legalMappingType.name() }"><c:out value="${ legalMappingType.name.content }" /></option>
                            </c:forEach>
                        </select>
                    </div>                
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    <spring:message code="label.close" />
                </button>
                <button id="createButton" class="btn btn-primary" type="submit">
                    <spring:message code="label.event.create" />
                </button>
            </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal --> 

<div class="modal fade" id="deleteModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="deleteForm" action="#" method="POST">
                <div class="modal-header">
                    <button type="button" class="close"
                        data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.manageLegalMapping.searchLegalMapping.confirmDelete" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default"
                        data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger"
                        type="submit">
                        <spring:message code="label.delete" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.manageLegalMapping.search" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="#" onClick="openCreateModal()">
        <spring:message code="label.event.create" />
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

//    $scope.object= angular.fromJson('${searchLegalMappingBeanJson}');;
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
            { name : '<spring:message code="label.no"/>', value : false },
            { name : '<spring:message code="label.yes"/>', value : true } 
    ];
    
}]);
</script>

<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
    action='#'>

    <input name="postback" type="hidden" value="#" />
    <input name="bean" type="hidden" value="{{ object }}" />

    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.LegalMapping.list" />
            </h3>
        </div>
        <c:choose>
            <c:when test="${ not empty legalMappings }">
                <div class="panel panel-body">
                    <table id="searchLegalMappingsTable"
                      class="table responsive table-bordered table-hover">
                        <thead>
                            <tr>
                                <th>
                                    <spring:message code="label.LegalMapping.name" />
                                </th>
                                <!-- operation column -->
                                <th style="width: 25%"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="legalMapping" items="${ legalMappings }">
                            <tr>
                                <td><c:out value="${ legalMapping.nameI18N.content }" /></td>
                                <td>
                                    <a class="btn btn-default btn-xs"
                                        href="${pageContext.request.contextPath}<%= ManageLegalMappingController.SEARCH_VIEW_URL %>/${ legalMapping.externalId }">
                                            <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>
                                            &nbsp;
                                            <spring:message code='label.view' />
                                    </a>
                                    <a class="btn btn-danger btn-xs" onClick="openDeleteModal('${legalMapping.externalId}')">
                                            <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                            &nbsp;
                                            <spring:message code='label.delete' />
                                    </a>
                                </td>
                            </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="panel panel-body">
                    <div class="alert alert-warning" role="alert">
                        <p>
                            <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                            <spring:message code="label.noResultsFound" />
                        </p>
                    </div>
                </div>    
            </c:otherwise>
        </c:choose>    
    </div>
</form>

<script>
$(document).ready( function() {
    var table = $('#searchLegalMappingsTable').DataTable({
	    language : {
		    url : "${datatablesI18NUrl}",
		},
		"columnDefs" : [
		     //54
			{
			"width" : "180px",
			"targets" : 1
			} ],
		//Documentation: https://datatables.net/reference/option/dom
		//"dom" : '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
		//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
		"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
		//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
		"tableTools" : {
			"sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
		}
	});
	table.columns.adjust().draw();

	$('#simpletablename tbody').on('click',
			'tr', function() {
				$(this).toggleClass('selected');
			});

});
</script>

