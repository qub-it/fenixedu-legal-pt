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
	<h1><spring:message code="label.manageScholarshipReportRequests.createScholarshipReportRequestStep1" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/integration/sas/managescholarshipreportrequests/scholarshipreportrequest/"  ><spring:message code="label.event.back" /></a></div>
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

<form method="post" class="form-horizontal">
<div class="panel panel-default">
  <div class="panel-body">
		<div class="form-group row">
		<div class="col-sm-2 control-label"><spring:message code="label.ScholarshipReportRequest.executionYear"/></div> 
		
		<div class="col-sm-4">
			<%-- Relation to side 1 drop down rendered in input --%>
				 <select id="scholarshipReportRequest_executionYear" class="js-example-basic-single" name="executionyear">
				 <option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%> 
				</select>
						</div>
		</div>		
		<div class="form-group row">
			<div class="col-sm-2 control-label"><spring:message code="label.ScholarshipReportRequest.contractualisation"/></div> 
			
			<div class="col-sm-2">
				<select id="scholarshipReportRequest_contractualisation" name="contractualisation" class="form-control">
					<option value="false"><spring:message code="label.no"/></option>
					<option value="true"><spring:message code="label.yes"/></option>				
				</select>
				<script>
					$("#scholarshipReportRequest_contractualisation").val('<c:out value='${not empty param.contractualisation ? param.contractualisation : scholarshipReportRequest.contractualisation }'/>');
				</script>	
			</div>
		</div>
		<div class="form-group row" id="firstYearOfCycleField">
			<div class="col-sm-2 control-label"><spring:message code="label.ScholarshipReportRequest.firstYearOfCycle"/></div>
			<div class="col-sm-2">
				<select id="scholarshipReportRequest_firstYearOfCycle" name="firstyearofcycle" class="form-control">
				<option value="false"><spring:message code="label.no"/></option>
				<option value="true"><spring:message code="label.yes"/></option>				
				</select>
				<script>
					$("#scholarshipReportRequest_firstYearOfCycle").val('<c:out value='${not empty param.firstyearofcycle ? param.firstyearofcycle : scholarshipReportRequest.firstYearOfCycle }'/>');
				</script>	
			</div>
		</div>
  </div>
  <div class="panel-footer">
		<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.nextStep" />"/>
	</div>
</div>
</form>

<script>

function showHideFirstYearOfCycleField(value){
	if (value == 'true') {				  
	  	$('#firstYearOfCycleField').hide();
	  	$('#scholarshipReportRequest_firstYearOfCycle').val('false');
	  }
	  else {
	  	$('#firstYearOfCycleField').show();
	  }
	
}

$(document).ready(function() {

		<%-- Block for providing executionYear options --%>
		<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
		executionYear_options = [
			<c:forEach items="${ScholarshipReportRequest_executionYear_options}" var="element"> 
				{
					text : "<c:out value='${element.name}'/>",  
					id : "<c:out value='${element.externalId}'/>"
				},
			</c:forEach>
		];
		
		$("#scholarshipReportRequest_executionYear").select2(
			{
				data : executionYear_options,
			}	  
	    );
	    
	    
	    
	    $("#scholarshipReportRequest_executionYear").select2().select2('val', '<c:out value='${param.executionyear}'/>');
	
		<%-- End block for providing executionYear options --%>
		
		
		$('#scholarshipReportRequest_contractualisation').on('change', function() {
			showHideFirstYearOfCycleField(this.value);	  
		})
		
		showHideFirstYearOfCycleField(${param.contractualisation != null && param.contractualisation ? "'true'" : "'false'"});
	
	
	});
</script>
