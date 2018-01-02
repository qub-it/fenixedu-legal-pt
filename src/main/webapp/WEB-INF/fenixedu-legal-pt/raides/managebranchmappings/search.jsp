<%@page import="org.fenixedu.legalpt.ui.raides.ManageBranchMappingsController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.ManageBranchMappings.search" />
		<small></small>
	</h1>
</div>

<c:choose>
	<c:when test="${not empty dcpList}">
		<table id="simpleTable"
			class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<th><spring:message code="label.ManageBranchMappings.degreeType" /></th>
					<th><spring:message code="label.ManageBranchMappings.degree" /></th>
					<th><spring:message code="label.ManageBranchMappings.degreeCurricularPlan" /></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="dcp" items="${dcpList}">
					<tr>
						<td><c:out value="${dcp.degreeType.name.content}" /></td>
						<td><c:out value="[${dcp.degree.code}] ${dcp.degree.presentationNameI18N.content}" /></td>
						<td><c:out value="${dcp.name}" /></td>
						<td>
							<a  class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%= ManageBranchMappingsController.VIEW_URL %>/${dcp.externalId}">
								<spring:message code='label.view' />
							</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>
</c:choose>

<script>

	$(document).ready(function() {

		var table = $('#simpleTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},

		"columnDefs": [
		//54
		               { "width": "54px", "targets": 3 } 
		             ],

	//Documentation: https://datatables.net/reference/option/dom
		//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
		//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
		//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        dom: '<"col-sm-5"l><"col-sm-3"f><"col-sm-3"B>rtip', //FilterBox = YES && ExportOptions = YES
        buttons: [
            'copyHtml5',
            'excelHtml5',
            'csvHtml5',
            'pdfHtml5'
        ],
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"        	
        }
	});
	table.columns.adjust().draw();
		
	$('#simpleTable tbody').on( 'click', 'tr', function () {
		$(this).toggleClass('selected');
	} );
		  
	}); 
</script>

