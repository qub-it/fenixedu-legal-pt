<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css"/>
<link rel="stylesheet" href="${datatablesCssUrl}"/>
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json"/>

<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link href="//cdn.datatables.net/responsive/1.0.4/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="//cdn.datatables.net/responsive/1.0.4/js/dataTables.responsive.js"></script>
<link href="//cdn.datatables.net/tabletools/2.2.3/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="//cdn.datatables.net/tabletools/2.2.3/js/dataTables.tableTools.min.js"></script>
<link href="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.1/css/select2.min.css" rel="stylesheet" />
<script src="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.1/js/select2.min.js"></script>
<script src="${pageContext.request.contextPath}/static/integration/sas/js/bootbox.min.js"></script>
<script src="${pageContext.request.contextPath}/static/integration/sas/js/omnis.js"></script>

${portal.toolkit()}

<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.manageScholarshipReportRequests.searchScholarshipReportRequest" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/createscholarshipreportrequeststep1"   ><spring:message code="label.event.create" /></a>
</div>
	<c:if test="${not empty infoMessages}">
				<div class="alert alert-info" role="alert">
					
					<c:forEach items="${infoMessages}" var="message"> 
						<p>${message}</p>
					</c:forEach>
					
				</div>	
			</c:if>
			<c:if test="${not empty warningMessages}">
				<div class="alert alert-warning" role="alert">
					
					<c:forEach items="${warningMessages}" var="message"> 
						<p>${message}</p>
					</c:forEach>
					
				</div>	
			</c:if>
			<c:if test="${not empty errorMessages}">
				<div class="alert alert-danger" role="alert">
					
					<c:forEach items="${errorMessages}" var="message"> 
						<p>${message}</p>
					</c:forEach>
					
				</div>	
			</c:if>


<script type="text/javascript">
	  function processDelete(externalId) {
	    url = "${pageContext.request.contextPath}/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/search/delete/" + externalId;
	    $("#deleteForm").attr("action", url);
	    $('#deleteModal').modal('toggle')
	  }
</script>


<div class="modal fade" id="deleteModal">
  <div class="modal-dialog">
    <div class="modal-content">
    <form id ="deleteForm" action="#" method="POST">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title"><spring:message code="label.confirmation"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code = "label.manageScholarshipReportRequests.searchScholarshipReportRequest.confirmDelete"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code = "label.close"/></button>
        <button id="deleteButton" class ="btn btn-danger" type="submit"> <spring:message code = "label.delete"/></button>
      </div>
      </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->



<c:choose>
	<c:when test="${not empty searchscholarshipreportrequestResultsDataSet}">
		<table id="searchscholarshipreportrequestTable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
<th><spring:message code="label.ScholarshipReportRequest.whenRequested"/></th>
<th><spring:message code="label.ScholarshipReportRequest.whenProcessed"/></th>
<th><spring:message code="label.ScholarshipReportRequest.contractualisation"/></th>
<th><spring:message code="label.ScholarshipReportRequest.firstYearOfCycle"/></th>
<th><spring:message code="label.ScholarshipReportRequest.executionYear"/></th>
<th><spring:message code="label.ScholarshipReportRequest.parameterFile"/></th>
<th><spring:message code="label.ScholarshipReportRequest.resultFile"/></th>
<th><spring:message code="label.ScholarshipReportRequest.error"/></th>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
				<div class="alert alert-warning" role="alert">
					
					<spring:message code="label.noResultsFound"/>
					
				</div>	
		
	</c:otherwise>
</c:choose>

<script>
	var searchscholarshipreportrequestDataSet = [
			<c:forEach items="${searchscholarshipreportrequestResultsDataSet}" var="searchResult">
				
				<spring:url var="parameterFileDownloadLink" value="/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/downloadFile/${searchResult.parameterFile.externalId}"/>
		    	<spring:url var="resultFileDownloadLink" value="/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/downloadFile/${searchResult.resultFile.externalId}"/>
			    
				<%-- Field access / formatting  here CHANGE_ME --%>
				{
				"DT_RowId" : '<c:out value='${searchResult.externalId}'/>',
"whenrequested" : "<joda:format value='${searchResult.whenRequested}' pattern='yyyy-MM-dd HH:mm' />",
"whenprocessed" : "<joda:format value='${searchResult.whenProcessed}' pattern='yyyy-MM-dd HH:mm' />",
"contractualisation" : "<c:if test="${searchResult.contractualisation}"><spring:message code="label.true" /></c:if><c:if test="${not searchResult.contractualisation}"><spring:message code="label.false" /></c:if>",
"firstyearofcycle" : "<c:if test="${searchResult.firstYearOfCycle}"><spring:message code="label.true" /></c:if><c:if test="${not searchResult.firstYearOfCycle}"><spring:message code="label.false" /></c:if>",
"executionyear" : "<c:out value='${searchResult.executionYear.name}'/>",
"parameterfile" : "<a href='${parameterFileDownloadLink}'>Download</a>",
"error" : '${searchResult.error.content}',
<c:choose>
<c:when test="${empty searchResult.resultFile}">
"resultfile" : ""},
</c:when>
<c:otherwise>
"resultfile" : "<a href='${resultFileDownloadLink}'>Download</a>"},    
</c:otherwise>
</c:choose>


            </c:forEach>
    ];
	
	$(document).ready(function() {

	


		var table = $('#searchscholarshipreportrequestTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
		"columns": [
			{ data: 'whenrequested' },
			{ data: 'whenprocessed' },
			{ data: 'contractualisation' },
			{ data: 'firstyearofcycle' },
			{ data: 'executionyear' },
			{ data: 'parameterfile' },
			{ data: 'resultfile' },
			{ data: 'error' }
			
		],
		"data" : searchscholarshipreportrequestDataSet,
		"order": [[ 0, "desc" ]],

"dom": '<"col-sm-6"l><"col-sm-6"f>rtip',

        "tableTools": {
            "sSwfPath": "//cdn.datatables.net/tabletools/2.2.3/swf/copy_csv_xls_pdf.swf"
        }
		});
		table.columns.adjust().draw();
		
		  $('#searchscholarshipreportrequestTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		  
	}); 
</script>

