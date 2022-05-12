<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
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

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.manageScholarshipReportRequests.createScholarshipReportRequestStep2" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
<span class="glyphicon  glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/createscholarshipreportrequeststep2/backtostep1?firstyearofcycle=${param.firstyearofcycle}&contractualisation=${param.contractualisation}&executionyear=${param.executionyear}"><spring:message code="label.event.manageScholarshipReportRequests.backToStep1"  /></a>
	
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

<form method="post" class="form-horizontal" enctype="multipart/form-data">
<div class="panel panel-default">
  <div class="panel-body">
<div class="form-group row">
	<div class="col-sm-2 control-label"><spring:message code="label.ScholarshipReportRequest.executionYear"/></div> 
	<input type="hidden" name="executionyear" value="${executionyear.externalId}"/>
	<div class="col-sm-4">
		<span class="form-control">${executionyear.name}</span>
	</div>
</div>	

<div class="form-group row">
	<div class="col-sm-2 control-label"><spring:message code="label.ScholarshipReportRequest.contractualisation"/></div> 
	<div class="col-sm-2">
		<input type="hidden" name="contractualisation" value="${contractualisation}" />
		<span class="form-control">${contractualisation}</span>
	</div>
</div>

<div class="form-group row">
	<div class="col-sm-2 control-label"><spring:message code="label.ScholarshipReportRequest.firstYearOfCycle"/></div> 
	<div class="col-sm-2">
		<input type="hidden" name="firstyearofcycle" value="${firstyearofcycle}" />
		<span class="form-control">${firstyearofcycle}</span>
	</div>
</div>

<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.ScholarshipReportRequest.parameterFile"/></div> 

<div class="col-sm-4">
	 <input id="file" class="form-control" type="file" name="file" value='' accept=".xls"/>
</div>
</div>		
  </div>
  <div class="panel-footer">
		<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />"/>
		<a class="btn btn-danger" role="button" href="${pageContext.request.contextPath}/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/"  ><spring:message code="label.event.cancel" /></a>
	</div>
</div>
</form>

<script>
$(document).ready(function() {
	});
</script>
