<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>


<link
	href="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.1/css/select2.min.css"
	rel="stylesheet" />
<script
	src="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.1/js/select2.min.js"></script>
<script
	src="${pageContext.request.contextPath}/static/integration/sas/js/bootbox.min.js"></script>
<script
	src="${pageContext.request.contextPath}/static/integration/sas/js/omnis.js"></script>

${portal.toolkit()}

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message
			code="label.manageScholarshipsConfiguration.updateSocialServicesConfiguration" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}/integration/sas/managescholarshipsconfiguration/socialservicesconfiguration/read/"><spring:message
			code="label.event.back" /></a>
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
<form method="post" class="form-horizontal">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.SocialServicesConfiguration.numberOfMonthsOfAcademicYear" />
				</div>

				<div class="col-sm-10">
					<select
						id="socialServicesConfiguration_numberOfMonthsOfAcademicYear"
						class="form-control" type="text"
						name="numberofmonthsofacademicyear">
						<c:set var="currentValue"
							value="${not empty param.numberofmonthsofacademicyear ? param.numberofmonthsofacademicyear : socialServicesConfiguration.numberOfMonthsOfAcademicYear }" />
						<c:forEach var="i" begin="1" end="12" step="1">
							<c:if test="${i == currentValue  }">
								<option selected>${i}</option>
							</c:if>
							<c:if test="${i != currentValue }">
								<option>${i}</option>
							</c:if>
						</c:forEach>
					</select>
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.SocialServicesConfiguration.email" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" name="email"
						value="${socialServicesConfiguration.email}" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.SocialServicesConfiguration.institutionCode" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" name="institutionCode"
						value="${socialServicesConfiguration.institutionCode}" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.SocialServicesConfiguration.ingressionTypeWhichAreDegreeTransfer" />
				</div>

				<div class="col-sm-4">
					<select
						id="socialServicesConfiguration_ingressionTypeWhichAreDegreeTransfer"
						class="js-example-basic-single form-control"
						name="ingressiontypewhicharedegreetransfer" multiple="multiple">
						<option value=""></option>
					</select>
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.SocialServicesConfiguration.ingressionTypesWithExternalData" />
				</div>

				<div class="col-sm-4">
					<select
						id="socialServicesConfiguration_ingressionTypesWithExternalData"
						class="js-example-basic-single form-control"
						name="ingressiontypeswithexternaldata" multiple="multiple">
						<option value=""></option>
					</select>
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.SocialServicesConfiguration.creditsReasonType" />
				</div>

				<div class="col-sm-4">
					<select id="socialServicesConfiguration_creditsReasonType"
						class="js-example-basic-single form-control"
						name="creditsReasonTypes" multiple="multiple">
						<option value=""></option>
					</select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.SocialServicesConfiguration.statuteTypeSas" />
				</div>

				<div class="col-sm-4">
					<select
						id="socialServicesConfiguration_statuteTypeSas"
						class="js-example-basic-single form-control"
						name="statuteTypeSas">
						<option value=""></option>
					</select>
				</div>
			</div>

		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button"
				value="<spring:message code="label.submit" />" />
		</div>
	</div>
</form>

<script>
$(document).ready(function() {
	
	statuteType_options = [
		<c:forEach items="${SocialServicesConfiguration_statuteType_options}" var="element"> 
			{
				text : "<c:out value='${element.code} - ${element.name.content}'/>", 
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	$("#socialServicesConfiguration_statuteTypeSas").select2(
		{
			data : statuteType_options,
		}	  
	);
	

	$("#socialServicesConfiguration_statuteTypeSas").select2().select2('val', "${socialServicesConfiguration.statuteTypeSas.externalId}");
	

	ingressionTypeWhichAreDegreeTransfer_options = [
		<c:forEach items="${SocialServicesConfiguration_ingressionTypeWhichAreDegreeTransfer_options}" var="element"> 
			{
				text : "<c:out value='${element.localizedName}'/>", 
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	$("#socialServicesConfiguration_ingressionTypeWhichAreDegreeTransfer").select2(
		{
			data : ingressionTypeWhichAreDegreeTransfer_options,
		}	  
		    );
		    
		    
    <%java.util.List hackingListIngressionTypeWhichAreDegreeTransfer = request
			.getParameterValues("ingressiontypewhicharedegreetransfer") != null
					? java.util.Arrays
							.asList(request.getParameterValues("ingressiontypewhicharedegreetransfer"))
					: new java.util.ArrayList();
	request.setAttribute("hackingListIngressionTypeWhichAreDegreeTransfer",
			hackingListIngressionTypeWhichAreDegreeTransfer);%>
    <c:if test="${not empty hackingListIngressionTypeWhichAreDegreeTransfer}">
                     $("#socialServicesConfiguration_ingressionTypeWhichAreDegreeTransfer").select2().select2('val', [
                                           <c:forEach items="${hackingListIngressionTypeWhichAreDegreeTransfer}" var="element" varStatus="status">
                                                   "<c:out value='${element}'/>"
                                                   <c:if test="${not status.last}">
                                                   ,
                                                   </c:if>
                                           </c:forEach>
                                           ]);
                     </c:if>
                 <c:if test="${empty hackingListIngressionTypeWhichAreDegreeTransfer && not empty socialServicesConfiguration.ingressionTypeWhichAreDegreeTransfer}">
                                         $("#socialServicesConfiguration_ingressionTypeWhichAreDegreeTransfer").select2().select2('val', [
                                         <c:forEach items="${socialServicesConfiguration.ingressionTypeWhichAreDegreeTransfer}" var="element" varStatus="status">
                                                 "<c:out value='${element.externalId}'/>"
                                                 <c:if test="${not status.last}">
                                                 ,
                                                 </c:if>
                                         </c:forEach>
                                         ]);
                 </c:if>
    <%-- End block for providing ingressionTypeWhichAreDegreeTransfer options --%>
    
    

	ingressionTypesWithExternalData_options = [
		<c:forEach items="${SocialServicesConfiguration_ingressionTypesWithExternalData_options}" var="element"> 
			{
				text : "<c:out value='${element.localizedName}'/>", 
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
		    

	$("#socialServicesConfiguration_ingressionTypesWithExternalData").select2(
			{
				data : ingressionTypesWithExternalData_options,
			}	  
			    );
			    
			    
	    <%java.util.List hackingListIngressionTypesWithExternalData = request
				.getParameterValues("ingressiontypeswithexternaldata") != null
						? java.util.Arrays
								.asList(request.getParameterValues("ingressiontypeswithexternaldata"))
						: new java.util.ArrayList();
		request.setAttribute("hackingListIngressionTypesWithExternalData",
		        hackingListIngressionTypesWithExternalData);%>
	    <c:if test="${not empty hackingListIngressionTypesWithExternalData}">
	                     $("#socialServicesConfiguration_ingressionTypesWithExternalData").select2().select2('val', [
	                                           <c:forEach items="${hackingListIngressionTypesWithExternalData}" var="element" varStatus="status">
	                                                   "<c:out value='${element}'/>"
	                                                   <c:if test="${not status.last}">
	                                                   ,
	                                                   </c:if>
	                                           </c:forEach>
	                                           ]);
	                     </c:if>
	                 <c:if test="${empty hackingListIngressionTypesWithExternalData && not empty socialServicesConfiguration.ingressionTypesWithExternalData}">
	                                         $("#socialServicesConfiguration_ingressionTypesWithExternalData").select2().select2('val', [
	                                         <c:forEach items="${socialServicesConfiguration.ingressionTypesWithExternalData}" var="element" varStatus="status">
	                                                 "<c:out value='${element.externalId}'/>"
	                                                 <c:if test="${not status.last}">
	                                                 ,
	                                                 </c:if>
	                                         </c:forEach>
	                                         ]);
	                 </c:if>
	    <%-- End block for providing IngressionTypesWithExternalData options --%>
	    
		    
		    
		    
		    creditsReasonType_options = [
				<c:forEach items="${SocialServicesConfiguration_creditsReasonType_options}" var="element"> 
					{
						text : "<c:out value='${element.reason.content}'/>", 
						id : "<c:out value='${element.externalId}'/>"
					},
				</c:forEach>
			];
			
			$("#socialServicesConfiguration_creditsReasonType").select2(
				{
					data : creditsReasonType_options,
				}	  
				    );
				    
				    <%java.util.List hackingListCreditsReasonType = request.getParameterValues("creditsReasonTypes") != null
					? java.util.Arrays.asList(request.getParameterValues("creditsReasonTypes"))
					: new java.util.ArrayList();
			request.setAttribute("hackingListCreditsReasonType", hackingListCreditsReasonType);%>
					
				    <c:if test="${not empty hackingListCreditsReasonType}">
		            	$("#socialServicesConfiguration_creditsReasonType").select2().select2('val', [
		                        <c:forEach items="${hackingListCreditsReasonType}" var="element" varStatus="status">
		                                "<c:out value='${element}'/>"
		                                <c:if test="${not status.last}">
		                                ,
		                                </c:if>
		                        </c:forEach>
		                        ]);
		            </c:if>
		            <c:if test="${empty hackingListCreditsReasonType && not empty socialServicesConfiguration.creditsReasonTypes}">
		                   $("#socialServicesConfiguration_creditsReasonType").select2().select2('val', [
		                   <c:forEach items="${socialServicesConfiguration.creditsReasonTypes}" var="element" varStatus="status">
		                           "<c:out value='${element.externalId}'/>"
		                           <c:if test="${not status.last}">
		                           ,
		                           </c:if>
		                   </c:forEach>
		                   ]);
		            </c:if>
				    <%-- End block for providing creditsReasonTypes options --%>
		
		    
		    
		    
		    
	});
	
	
	
	
</script>
