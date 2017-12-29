<%@page import="org.fenixedu.legalpt.ui.academicinstitutions.AcademicInstitutionsController"%>
<%@page import="org.fenixedu.academic.domain.Country"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<jsp:include page="../commons/angularInclude.jsp" />


<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.AcademicInstitutionsController.viewdegreedesignations.title" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-search" aria-hidden="true"></span>&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= AcademicInstitutionsController.VIEW_ACADEMIC_UNITS_URL %>">
		<spring:message code="label.AcademicInstitutionsController.viewacademicunits.title"/>
	</a>
	
</div>

<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"${pageContext.request.contextPath}
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>


<h2><spring:message code="label.AcademicInstitutionsController.viewdegreedesignations.title" /></h2>

<table class="table" id="officialDegreeDesignationsTable" >
	<thead>
		<tr>
			<th scope="row" class="col-xs-3"><spring:message code="label.AcademicInstitutionsController.official.code" /></th>
			<th scope="row" class="col-xs-3"><spring:message code="label.AcademicInstitutionsController.degree.designation.name" /></th>
			<th scope="row" class="col-xs-3"><spring:message code="label.AcademicInstitutionsController.academic.units.number" /></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="degree" items="${degreeDesignations}">
			<tr>
				<td><c:out value="${degree.code}" /></td>
				<td><c:out value="${degree.description}" /></td>
				<td><c:out value="${degree.institutionUnitSet.size()}" /></td>
				<td>
					<a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%= AcademicInstitutionsController.VIEW_DEGREE_DESIGNATION_DETAIL_URL %>/${degree.externalId}">
						<spring:message code="label.view" />
					</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>



<script>

$(document).ready(function() {
	function dataTables(tableid, showsearchbox, showtools,pagination, pagecontext,i18nurl, columnDefs) {
		var dom = "";
		if (showsearchbox == true && showtools == true) {
			dom = '<"col-sm-5"l><"col-sm-3"f><"col-sm-3"B>rtip'; //FilterBox = YES && ExportOptions = YES
		} else if (showsearchbox == true && showtools == false) {
			dom = '<"col-sm-6"l><"col-sm-6"f>rtip'; // FilterBox = YES && ExportOptions = NO
		} else if (showsearchbox == false && showtools == true) {
			dom = 'T<"clear">lrtip'; // FilterBox = NO && ExportOptions = YES
		} else {
			dom = '<"col-sm-6"l>rtip'; // FilterBox = NO && ExportOptions = NO
		}
		var table = $('#'+tableid)
				.DataTable({language : {
					url : i18nurl,			
				},
				"bDeferRender" : true,
				"bPaginate" : pagination,
				"dom" : dom, 
                 buttons: [
                     'copyHtml5',
                     'excelHtml5',
                     'csvHtml5',
                     'pdfHtml5'
                 ],
				"tableTools" : {
					"sSwfPath" : pagecontext + "/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
				},
				"columnDefs": columnDefs
		});
		
		table.columns.adjust().draw();
		
		$('#' + tableid +' tbody').on('click', 'tr', function() {
			$(this).toggleClass('selected');
		});
	}
	
	dataTables(
			"officialDegreeDesignationsTable",
			true,
			true,
			true,
			"${pageContext.request.contextPath}",
			"${datatablesI18NUrl}",
			[ { "width": "5%", "targets": 0 }, { "width": "5%", "targets": 2 }, { "width": "5%", "targets": 3 } ]);

});

</script>
