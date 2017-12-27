<%@page import="org.fenixedu.ulisboa.specifications.ui.legal.academicinstitutions.importation.AcademicInstitutionsImportationController"%>
<%@page import="org.fenixedu.academic.domain.Country"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<script type="text/javascript" src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/select/1.2.0/js/dataTables.select.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/buttons.html5.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/1.2.2/css/buttons.dataTables.min.css" />


<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%-- ${portal.angularToolkit()} --%>
${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>



<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.AcademicInstitutionsImportationController.viewacademicunits.title" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-search" aria-hidden="true"></span>&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= AcademicInstitutionsImportationController.VIEW_ACADEMIC_UNITS_URL %>">
		<spring:message code="label.AcademicInstitutionsImportationController.viewacademicunits.title"/>
	</a>
	
	&nbsp;|&nbsp;
	<span class="glyphicon glyphicon-upload" aria-hidden="true"></span>&nbsp;
	<a onclick="javascript:processUpload();" href="#">
		<spring:message code="label.AcademicInstitutionsImportationController.upload.degree.designations.title"/>
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


<script type="text/javascript">
      function processUpload() {
        $('#uploadModal').modal('toggle')
      }
</script>

<div class="modal fade" id="uploadModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="uploadForm" method="POST" enctype="multipart/form-data"
            	action="${pageContext.request.contextPath}<%=AcademicInstitutionsImportationController.UPLOAD_DEGREE_DESIGNATIONS_FILE_URL %>" >
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.AcademicInstitutionsImportationController.upload.file.official.degree.designations.title" />
                    </h4>
                </div>
                <div class="modal-body">
                    <input type="file" name="officialDegreeDesignationsFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" />
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.cancel" />
                    </button>
                    <button id="uploadButton" class="btn btn-primary" type="submit">
                        <spring:message code="label.upload" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>

<h2><spring:message code="label.AcademicInstitutionsImportationController.viewdegreedesignations.title" /></h2>

<table class="table" id="officialDegreeDesignationsTable" >
	<thead>
		<tr>
			<th scope="row" class="col-xs-3"><spring:message code="label.AcademicInstitutionsImportationController.official.code" /></th>
			<th scope="row" class="col-xs-3"><spring:message code="label.AcademicInstitutionsImportationController.degree.designation.name" /></th>
			<th scope="row" class="col-xs-3"><spring:message code="label.AcademicInstitutionsImportationController.academic.units.number" /></th>
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
					<a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%= AcademicInstitutionsImportationController.VIEW_DEGREE_DESIGNATION_DETAIL_URL %>/${degree.externalId}">
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
