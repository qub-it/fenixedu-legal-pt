<%@page import="org.fenixedu.ulisboa.integration.sas.ui.spring.controller.manageScholarshipCandidacies.ScholarshipCandidaciesController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>


<script type="text/javascript" src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/select/1.2.0/js/dataTables.select.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/buttons.html5.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/1.2.2/css/buttons.dataTables.min.css" /> 


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

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

${portal.toolkit()}

<link href="//cdn.datatables.net/responsive/1.0.6/css/dataTables.responsive.css" rel="stylesheet" />
<script src="//cdn.datatables.net/responsive/1.0.6/js/dataTables.responsive.js"></script>
<link href="//cdn.datatables.net/tabletools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script	src="//cdn.datatables.net/tabletools/2.2.4/js/dataTables.tableTools.min.js"></script>
<link href="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.2/css/select2.min.css" rel="stylesheet" />
<script src="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.2/js/select2.min.js"></script>
<script	src="${pageContext.request.contextPath}/static/integration/sas/js/bootbox.min.js"></script>
<script	src="${pageContext.request.contextPath}/static/integration/sas/js/omnis.js"></script>







<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message
			code="label.manageScholarshipCandidacies.sasScholarshipDataChangeLogs" />
		<small></small>
	</h1>
</div>

<!-- /.modal -->
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<c:choose>
		<c:when test="${not empty sasScholarshipCandidacy}">
			<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
			<a class=""href="${pageContext.request.contextPath}<%=ScholarshipCandidaciesController.READ_SAS_SCHOLARSHIP_CANDIDACY_URL%>/${sasScholarshipCandidacy.externalId}"><spring:message code="label.event.back" /></a>
		</c:when>
		<c:otherwise>
			<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
			<a class=""href="${pageContext.request.contextPath}<%=ScholarshipCandidaciesController.CONTROLLER_URL%>/"><spring:message code="label.event.back" /></a>		
		</c:otherwise>
	
	</c:choose>


</div>


<c:choose>
	<c:when test="${not empty sasScholarshipDataChangeLogs}">
		<table id="searchSasScholarshipDataChangeLogsTable"
			class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
					<th><spring:message
							code="label.SasScholarshipDataChangeLog.date" /></th>
					<th><spring:message
							code="label.SasScholarshipDataChangeLog.studentNumber" /></th>
					<th><spring:message
							code="label.SasScholarshipDataChangeLog.studentName" /></th>
					<th><spring:message
							code="label.SasScholarshipDataChangeLog.description" /></th>
				</tr>
			</thead>
			<tbody>

			<c:forEach items="${sasScholarshipDataChangeLogs}" var="logResult">
				<tr>
					<td><joda:format value='${logResult.date}' pattern='yyyy-MM-dd HH:mm' /></td>
					<td>${logResult.studentNumber}</td>
					<td>${logResult.studentName}</td>
					<td>${logResult.description}</td>
				</tr>
			</c:forEach>

			</tbody>
		</table>
				
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<spring:message code="label.noResultsFound" />

		</div>

	</c:otherwise>
</c:choose>


<script>
	
	$(document).ready(function() {

		var table = $('#searchSasScholarshipDataChangeLogsTable').DataTable({
			language : {
				url : "${datatablesI18NUrl}",			
			},
			"columns": [
				{ data: 'date' },
				{ data: 'studentNumber' },
				{ data: 'studentName' },
				{ data: 'description' }
				
				],
			"order": [[ 0, "desc" ]],

			dom: '<"col-sm-5"l><"col-sm-3"f><"col-sm-3"B>rtip', //FilterBox = YES && ExportOptions = YES
			buttons: [
		        'copyHtml5',
		        'excelHtml5',
		        'csvHtml5',
		        'pdfHtml5'
			],
        	tableTools: {
            	sSwfPath: "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
        	}
			
		});
		
		table.columns.adjust().draw();
		
	}); 
</script>