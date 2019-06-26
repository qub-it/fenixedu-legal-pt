<%@page import="org.fenixedu.legalpt.dto.a3es.A3esAbstractBean"%>
<%@page import="org.fenixedu.legalpt.dto.a3es.A3esProcessBean"%>
<%@page import="org.fenixedu.legalpt.domain.a3es.A3esProcess"%>
<%@page import="org.fenixedu.legalpt.ui.a3es.A3esProcessTeacherController"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../../../commons/angularInclude.jsp" />

<script type="text/javascript">
    angular
	    .module('angularAppA3esProcess',
		    [ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
	    .controller(
		    'A3esProcessTeacherController',
		    [
			    '$scope',
			    function($scope) {

			    	$scope.booleanvalues = [
						{
						    name : '<spring:message code="label.no"/>',
						    value : false
						},
						{
						    name : '<spring:message code="label.yes"/>',
						    value : true
						} ];
	
					$scope.object = ${processBeanJson};
	
					$scope.postBack = createAngularPostbackFunction($scope);
	
					// Begin here of Custom Screen business JS - code
					
					$scope.download = function(){
						$('#exportForm').attr('action', '${pageContext.request.contextPath}<%= A3esProcessTeacherController.TEACHERSDOWNLOAD_URL %>');
						$('#exportForm').submit();
					}					
					
			    } ]);
</script>


<div class="modal fade" id="confirmationModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="confirmationForm"	action="#" method="post">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirmation" />
					</h4>
				</div>
				<div class="modal-body">
					<p id="confirmationMessage"></p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.close" />
					</button>
					<button id="confirmationButton" class="btn btn-danger" type="submit">
						
					</button>
				</div>
			</form>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<form id="exportForm" name='form' method="post" class="form-horizontal" ng-app="angularAppA3esProcess"
	ng-controller="A3esProcessTeacherController"
	action='#'>

	<input type="hidden" name="postback" value='${pageContext.request.contextPath}<%=A3esProcessTeacherController.VIEWCOURSESPOSTBACK_URL%>${process.externalId}' />
	<input name="bean" type="hidden" value="{{ object }}" />

	<%-- TITLE --%>
	<div class="page-header">
		<h1>
			<spring:message code="label.teacherFiles" />
			<small></small>
		</h1>
	</div>
	
	<%-- NAVIGATION --%>
	<div class="well well-sm" style="display: inline-block">
		<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=A3esProcessTeacherController.READ_URL%>${process.externalId}"><spring:message
				code="label.event.back" /></a>
	</div>

	<c:if test="${not empty infoMessages}">
		<div class="alert alert-info" role="alert">
	
			<c:forEach items="${infoMessages}" var="message">
				<p>
					<span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span> ${message}
				</p>
			</c:forEach>
	
		</div>
	</c:if>
	<c:if test="${not empty warningMessages}">
		<div class="alert alert-warning" role="alert">
	
			<c:forEach items="${warningMessages}" var="message">
				<p>
					<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
				</p>
			</c:forEach>
	
		</div>
	</c:if>
	<c:if test="${not empty errorMessages}">
		<div class="alert alert-danger" role="alert">
	
			<c:forEach items="${errorMessages}" var="message">
				<p>
					<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
				</p>
			</c:forEach>
	
		</div>
	</c:if>
	
	<div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title">
				<spring:message code="label.details" />
			</h3>
		</div>
		
		<spring:message code="label.yes" var="yesLabel" />	
		<spring:message code="label.no" var="noLabel" />
	
		<div class="panel-body">
			<table class="table">
				<tbody>
					<tr>
						<th scope="row" class="col-xs-2"><spring:message code="label.creationDate" /></th>
						<td><joda:format value="${process.versioningCreationDate}" pattern="yyyy-MM-dd HH:mm" /></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-2"><spring:message code="label.A3esProcess.name" /></th>
						<td><c:out value="${process.name}"/></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-2"><spring:message code="label.A3esPeriod.degreeCurricularPlan" /></th>
						<td><c:out value="${process.planDescription}"/></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="panel panel-footer">
			<button type="button" class="btn btn-primary" role="button" ng-click="download()"><spring:message code="label.export" /></button>
		</div>
	</div>

	<c:choose>
		<c:when test="${not empty processBean.teachersData}">
			<spring:message code="label.yes" var="yesLabel"/>
			<spring:message code="label.no" var="noLabel"/>
		
			<table id="searchTable" class="table table-bordered table-hover" width="100%">
				<thead>
					<tr>
<%-- 						<th><spring:message code="label.firstTeacherService" /></th> --%>
						<th><spring:message code="label.name" /></th>
						<%-- Operations Column --%>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="iter" items="${processBean.teachersData}" varStatus="loop">
						<tr>
<%-- 							<td><c:out value="<%=((A3esAbstractBean)pageContext.getAttribute("iter")).getFieldUnique("firstTeacherService").getValue()%>"></c:out></td> --%>
							<td><c:out value="<%=((A3esAbstractBean)pageContext.getAttribute("iter")).getFieldUnique("name").getValue()%>"/></td>
							<td>
								<a class="btn btn-default btn-xs" onclick="function inline(){$('#detailsModal${loop.index}').modal('toggle')}; inline()"><spring:message code='label.view'/></a>
								
								<div class="modal fade" id="detailsModal${loop.index}">
									<div class="modal-dialog">
										<div class="modal-content">
											<div class="modal-header" style="border-bottom: none;">
												<button type="button" class="close" data-dismiss="modal"
													aria-label="Close">
													<span aria-hidden="true">&times;</span>
												</button>
												<h3 class="modal-title">
													<spring:message code="label.teacherFile" />
												</h3>
											</div>
											<div style="height: 500px; overflow: auto;">
												
												<h4><spring:message code="label.personalData" /></h3>
												<table class="table responsive table-bordered table-hover" width="100%">
													<tbody>
														<c:forEach var="field" items="${iter.fields}" varStatus="loop">
															<tr>
																<th scope="row" class="col-xs-4">${field.label}</th>
																<td><c:out value="${field.value}"/><c:if test="${not empty field.report}">
																	<c:choose>
																		<c:when test="${field.reportType == 'error'}"><c:set var="iconClass">glyphicon glyphicon-warning-sign</c:set><c:set var="reportClass">alert-danger</c:set></c:when>
																		<c:when test="${field.reportType == 'info'}"><c:set var="iconClass">glyphicon glyphicon-info-sign</c:set><c:set var="reportClass">alert-warning</c:set></c:when>
																		<c:otherwise><c:set var="iconClass"></c:set><c:set var="reportClass"></c:set></c:otherwise>
																	</c:choose>
																	<div class="${reportClass}" style="background-color: transparent; font-style: italic;"><p><span class="${iconClass}"></span> <small><c:out value="${field.report}"/></small></p></div>
																</c:if></td>
															</tr>
														</c:forEach>
														<c:forEach var="field" items="${iter.attainedDegree.fields}" varStatus="loop">
															<tr>
																<th scope="row" class="col-xs-4">${field.label}</th>
																<td><c:out value="${field.value}"/><c:if test="${not empty field.report}">
																	<c:choose>
																		<c:when test="${field.reportType == 'error'}"><c:set var="iconClass">glyphicon glyphicon-warning-sign</c:set><c:set var="reportClass">alert-danger</c:set></c:when>
																		<c:when test="${field.reportType == 'info'}"><c:set var="iconClass">glyphicon glyphicon-info-sign</c:set><c:set var="reportClass">alert-warning</c:set></c:when>
																		<c:otherwise><c:set var="iconClass"></c:set><c:set var="reportClass"></c:set></c:otherwise>
																	</c:choose>
																	<div class="${reportClass}" style="background-color: transparent; font-style: italic;"><p><span class="${iconClass}"></span> <small><c:out value="${field.report}"/></small></p></div>
																</c:if></td>
															</tr>
														</c:forEach>
													</tbody>
												</table>

												<h4><spring:message code="label.otherAcademicDegreesOrTitle" /></h4>
												<table class="table responsive table-bordered table-hover" width="100%">
													<tbody>
														<c:choose>
															<c:when test="${not empty iter.otherAttainedDegrees}">
																<c:forEach var="attainedDegree" items="${iter.otherAttainedDegrees}" varStatus="loop">
																	<c:forEach var="field" items="${attainedDegree.fields}" varStatus="loop">
																		<tr>
																			<th scope="row" class="col-xs-4">${field.label}</th>
																			<td><c:out value="${field.value}"/><c:if test="${not empty field.report}">
																				<c:choose>
																					<c:when test="${field.reportType == 'error'}"><c:set var="iconClass">glyphicon glyphicon-warning-sign</c:set><c:set var="reportClass">alert-danger</c:set></c:when>
																					<c:when test="${field.reportType == 'info'}"><c:set var="iconClass">glyphicon glyphicon-info-sign</c:set><c:set var="reportClass">alert-warning</c:set></c:when>
																					<c:otherwise><c:set var="iconClass"></c:set><c:set var="reportClass"></c:set></c:otherwise>
																				</c:choose>
																				<div class="${reportClass}" style="background-color: transparent; font-style: italic;"><p><span class="${iconClass}"></span> <small><c:out value="${field.report}"/></small></p></div>
																			</c:if></td>
																		</tr>
																	</c:forEach>
																</c:forEach>
															</c:when>
															<c:otherwise>
																<tr>
																	<th scope="row" class="col-xs-4">&nbsp;</th>
																	<td><small><div class="alert-danger" style="background-color: transparent; font-style: italic;"><p><span class="glyphicon glyphicon-exclamation-sign"></span> <spring:message code="label.field.missing" /></p></div></small></td>
																</tr>
															</c:otherwise>
														</c:choose>
													</tbody>
												</table>

												<h4><spring:message code="label.scientificActivity" /></h4>
												<small><em><p><spring:message code="label.scientificActivity.message" /></p></em></small>
												<table class="table responsive table-bordered table-hover" width="100%">
													<tbody>
														<c:choose>
															<c:when test="${not empty iter.primePublishedWork.fields}">
																<c:forEach var="field" items="${iter.primePublishedWork.fields}" varStatus="loop">
																	<tr>
																		<th scope="row" class="col-xs-4">${field.label}</th>
																		<td><c:out value="${field.value}"/><c:if test="${not empty field.report}">
																			<c:choose>
																				<c:when test="${field.reportType == 'error'}"><c:set var="iconClass">glyphicon glyphicon-warning-sign</c:set><c:set var="reportClass">alert-danger</c:set></c:when>
																				<c:when test="${field.reportType == 'info'}"><c:set var="iconClass">glyphicon glyphicon-info-sign</c:set><c:set var="reportClass">alert-warning</c:set></c:when>
																				<c:otherwise><c:set var="iconClass"></c:set><c:set var="reportClass"></c:set></c:otherwise>
																			</c:choose>
																			<div class="${reportClass}" style="background-color: transparent; font-style: italic;"><p><span class="${iconClass}"></span> <small><c:out value="${field.report}"/></small></p></div>
																		</c:if></td>																	
																	</tr>
																</c:forEach>
															</c:when>
															<c:otherwise>
																<tr>
																	<th scope="row" class="col-xs-4">&nbsp;</th>
																	<td><small><div class="alert-danger" style="background-color: transparent; font-style: italic;"><p><span class="glyphicon glyphicon-exclamation-sign"></span> <spring:message code="label.field.missing" /></p></div></small></td>
																</tr>
															</c:otherwise>
														</c:choose>
													</tbody>
												</table>

												<h4><spring:message code="label.developmentActivity" /></h4>
												<small><em><p><spring:message code="label.developmentActivity.message" /></p></em></small>
												<table class="table responsive table-bordered table-hover" width="100%">
													<tbody>
														<c:choose>
															<c:when test="${not empty iter.primeProfessionalActivities.fields}">
																<c:forEach var="field" items="${iter.primeProfessionalActivities.fields}" varStatus="loop">
																	<tr>
																		<th scope="row" class="col-xs-4">${field.label}</th>
																		<td><c:out value="${field.value}"/><c:if test="${not empty field.report}">
																			<c:choose>
																				<c:when test="${field.reportType == 'error'}"><c:set var="iconClass">glyphicon glyphicon-warning-sign</c:set><c:set var="reportClass">alert-danger</c:set></c:when>
																				<c:when test="${field.reportType == 'info'}"><c:set var="iconClass">glyphicon glyphicon-info-sign</c:set><c:set var="reportClass">alert-warning</c:set></c:when>
																				<c:otherwise><c:set var="iconClass"></c:set><c:set var="reportClass"></c:set></c:otherwise>
																			</c:choose>
																			<div class="${reportClass}" style="background-color: transparent; font-style: italic;"><p><span class="${iconClass}"></span> <small><c:out value="${field.report}"/></small></p></div>
																		</c:if></td>																	
																	</tr>
																</c:forEach>
															</c:when>
															<c:otherwise>
																<tr>
																	<th scope="row" class="col-xs-4">&nbsp;</th>
																	<td><small><div class="alert-danger" style="background-color: transparent; font-style: italic;"><p><span class="glyphicon glyphicon-exclamation-sign"></span> <spring:message code="label.field.missing" /></p></div></small></td>
																</tr>
															</c:otherwise>
														</c:choose>
													</tbody>
												</table>

												<h4><spring:message code="label.otherPublicationActivity" /></h4>
												<small><em><p><spring:message code="label.otherPublicationActivity.message" /></p></em></small>
												<table class="table responsive table-bordered table-hover" width="100%">
													<tbody>
														<c:choose>
															<c:when test="${not empty iter.otherPublishedWork.fields}">
																<c:forEach var="field" items="${iter.otherPublishedWork.fields}" varStatus="loop">
																	<tr>
																		<th scope="row" class="col-xs-4">${field.label}</th>
																		<td><c:out value="${field.value}"/><c:if test="${not empty field.report}">
																			<c:choose>
																				<c:when test="${field.reportType == 'error'}"><c:set var="iconClass">glyphicon glyphicon-warning-sign</c:set><c:set var="reportClass">alert-danger</c:set></c:when>
																				<c:when test="${field.reportType == 'info'}"><c:set var="iconClass">glyphicon glyphicon-info-sign</c:set><c:set var="reportClass">alert-warning</c:set></c:when>
																				<c:otherwise><c:set var="iconClass"></c:set><c:set var="reportClass"></c:set></c:otherwise>
																			</c:choose>
																			<div class="${reportClass}" style="background-color: transparent; font-style: italic;"><p><span class="${iconClass}"></span> <small><c:out value="${field.report}"/></small></p></div>
																		</c:if></td>
																	</tr>
																</c:forEach>
															</c:when>
															<c:otherwise>
																<tr>
																	<th scope="row" class="col-xs-4">&nbsp;</th>
																	<td><small><div class="alert-danger" style="background-color: transparent; font-style: italic;"><p><span class="glyphicon glyphicon-exclamation-sign"></span> <spring:message code="label.field.missing" /></p></div></small></td>
																</tr>
															</c:otherwise>
														</c:choose>
													</tbody>
												</table>

												<h4><spring:message code="label.otherProfessionalActivity" /></h4>
												<small><em><p><spring:message code="label.otherProfessionalActivity.message" /></p></em></small>
												<table class="table responsive table-bordered table-hover" width="100%">
													<tbody>
														<c:choose>
															<c:when test="${not empty iter.otherProfessionalActivities.fields}">
																<c:forEach var="field" items="${iter.otherProfessionalActivities.fields}" varStatus="loop">
																	<tr>
																		<th scope="row" class="col-xs-4">${field.label}</th>
																		<td><c:out value="${field.value}"/><c:if test="${not empty field.report}">
																			<c:choose>
																				<c:when test="${field.reportType == 'error'}"><c:set var="iconClass">glyphicon glyphicon-warning-sign</c:set><c:set var="reportClass">alert-danger</c:set></c:when>
																				<c:when test="${field.reportType == 'info'}"><c:set var="iconClass">glyphicon glyphicon-info-sign</c:set><c:set var="reportClass">alert-warning</c:set></c:when>
																				<c:otherwise><c:set var="iconClass"></c:set><c:set var="reportClass"></c:set></c:otherwise>
																			</c:choose>
																			<div class="${reportClass}" style="background-color: transparent; font-style: italic;"><p><span class="${iconClass}"></span> <small><c:out value="${field.report}"/></small></p></div>
																		</c:if></td>
																	</tr>
																</c:forEach>
															</c:when>
															<c:otherwise>
																<tr>
																	<th scope="row" class="col-xs-4">&nbsp;</th>
																	<td><small><div class="alert-danger" style="background-color: transparent; font-style: italic;"><p><span class="glyphicon glyphicon-exclamation-sign"></span> <spring:message code="label.field.missing" /></p></div></small></td>
																</tr>
															</c:otherwise>
														</c:choose>
													</tbody>
												</table>

												<h4><spring:message code="label.teachingServiceAllocation" /></h4>
												<table class="table responsive table-bordered table-hover" width="100%">
													<tbody>
														<c:choose>
															<c:when test="${not empty iter.teachingServices}">
																<c:forEach var="teachingService" items="${iter.teachingServices}" varStatus="loop">
																	<c:forEach var="field" items="${teachingService.fields}" varStatus="loop">
																		<tr>
																			<th scope="row" class="col-xs-4">${field.label}</th>
																			<td><c:out value="${field.value}"/><c:if test="${not empty field.report}">
																				<c:choose>
																					<c:when test="${field.reportType == 'error'}"><c:set var="iconClass">glyphicon glyphicon-warning-sign</c:set><c:set var="reportClass">alert-danger</c:set></c:when>
																					<c:when test="${field.reportType == 'info'}"><c:set var="iconClass">glyphicon glyphicon-info-sign</c:set><c:set var="reportClass">alert-warning</c:set></c:when>
																					<c:otherwise><c:set var="iconClass"></c:set><c:set var="reportClass"></c:set></c:otherwise>
																				</c:choose>
																				<div class="${reportClass}" style="background-color: transparent; font-style: italic;"><p><span class="${iconClass}"></span> <small><c:out value="${field.report}"/></small></p></div>
																			</c:if></td>
																		</tr>
																	</c:forEach>
																</c:forEach>
															</c:when>
															<c:otherwise>
																<tr>
																	<th scope="row" class="col-xs-4">&nbsp;</th>
																	<td><small><div class="alert-danger" style="background-color: transparent; font-style: italic;"><p><span class="glyphicon glyphicon-exclamation-sign"></span> <spring:message code="label.field.missing" /></p></div></small></td>
																</tr>
															</c:otherwise>
														</c:choose>
													</tbody>
												</table>

											</div>

											<div class="modal-footer">
												<button type="button" class="btn btn-default" data-dismiss="modal">
													<spring:message code="label.back" />
												</button>
											</div>
										</div>
										<!-- /.modal-content -->
									</div>
									<!-- /.modal-dialog -->
								</div>
								<!-- /.modal -->
								
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<script type="text/javascript">
				window.datatable = createDataTablesWithSortSwitch(
						'searchTable', 
						false /*filterable*/, 
						false /*show tools*/, 
						false /*paging*/, 
						"${pageContext.request.contextPath}","${datatablesI18NUrl}"
						);
			</script>
					
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

</form>
