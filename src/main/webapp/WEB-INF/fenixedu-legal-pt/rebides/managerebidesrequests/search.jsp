<%@page import="org.fenixedu.legalpt.ui.rebides.RebidesRequestsController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageRebidesRequests.search" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= RebidesRequestsController.CREATE_URL %>">
		<spring:message code="label.event.create" />
	</a>
</div>

<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">

        <c:forEach items="${infoMessages}" var="message">
            <p> <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>
    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">

        <c:forEach items="${warningMessages}" var="message">
            <p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>
    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">

        <c:forEach items="${errorMessages}" var="message">
            <p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>
    </div>
</c:if>


<c:choose>
	<c:when test="${not empty reportRequests}">
		<table id="simpletablename" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<th>
						<spring:message code="label.RebidesRequests.whenRequest" />
					</th>
					<th>
						<spring:message code="label.RebidesRequests.whenProcessed" />
					</th>
					<th>
						<spring:message code="label.RebidesRequests.requestor" />
					</th>
					<th>
						<spring:message code="label.RebidesRequests.pending" />
					</th>
					<th>
						<spring:message code="label.RebidesRequests.files" />
					</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="request" items="${reportRequests}">
					<tr>
						<td>
							<c:out value='${request.whenRequested.toString("yyyy-MM-dd HH:mm:ss")}' />
						</td>
						<td>
							<c:out value='${request.whenProcessed.toString("yyyy-MM-dd HH:mm:ss")}' />
						</td>
						<td>
							<c:out value="${request.requestor.name}" />
						</td>
						<td>
							<c:if test="${request.pending}">
								<spring:message code="label.true" />
							</c:if>
							<c:if test="${!request.pending}">
								<spring:message code="label.false" />
							</c:if>
						</td>
						<td>
							<c:forEach var="resultDataFile" items="${request.legalResultDataFile}">
								<p>
									<a href="${pageContext.request.contextPath}<%= RebidesRequestsController.DOWNLOAD_RESULT_FILE_URL %>/${resultDataFile.externalId}" >
										<c:out value="${resultDataFile.filename}" />
									</a>
									&nbsp;
									[<c:out value="${resultDataFile.type.localizedNameI18N.content}" />]
								</p>
							</c:forEach>
						</td>
						<td>
							<a class="btn btn-default btn-xs"
								href="${pageContext.request.contextPath}<%= RebidesRequestsController.READ_URL %>/${request.externalId}">
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
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>
</c:choose>

<script>
	$(document)
			.ready(
					function() {

						var table = $('#simpletablename')
								.DataTable(
										{
											language : {
												url : "${datatablesI18NUrl}",
											},
											"order": [[ 0, "desc" ]],
											"columnDefs" : [
												{ "width" : "25px", "targets" : 0 }, 
												{ "width" : "25px", "targets" : 1 }, 
												{ "width" : "54px", "targets" : 3 }
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

