<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="<%= request.getParameter("periodTableTitle") %>" /></h3>
		</div>
		<div class="panel-body">
			
			<table id="enrolledPeriodsTable_<%= request.getParameter("periodType") %>" class="table responsive table-bordered table-hover">
				<thead>
					<tr>
						<th><spring:message code="label.RaidesRequests.academicPeriod" /></th>
						<th><spring:message code="label.RaidesRequests.begin" /></th>
						<th><spring:message code="label.RaidesRequests.end" /></th>
						<th><spring:message code="label.RaidesRequests.enrolledInAcademicPeriod" /></th>
						<th><spring:message code="label.RaidesRequests.enrolmentEctsConstraint" /></th>
						<th><spring:message code="label.RaidesRequests.minEnrolmentEcts" /></th>
						<th><spring:message code="label.RaidesRequests.maxEnrolmentEcts" /></th>
						<th><spring:message code="label.RaidesRequests.enrolmentYearsConstraint" /></th>
						<th><spring:message code="label.RaidesRequests.minEnrolmentYears" /></th>
						<th><spring:message code="label.RaidesRequests.maxEnrolmentYears" /></th>
					</tr>
				</thead>
				
				<tbody>
					<tr ng-repeat="p in object.periods | filter:{periodInputType: '<%= request.getParameter("periodType") %>'}">
						<td>{{ p.academicPeriodQualifiedName }}</td>
						<td>{{ p.begin }}</td>
						<td>{{ p.end }}</td>
						<td>{{ p.enrolledInAcademicPeriod ? '<spring:message code="label.true" />': '<spring:message code="label.false" />' }}</td>
						<td>{{ p.enrolmentEctsConstraint ? '<spring:message code="label.true" />' : '<spring:message code="label.false" />' }}</td>
						<td>{{ p.minEnrolmentEcts }}</td>
						<td>{{ p.maxEnrolmentEcts }}</td>
						<td>{{ p.enrolmentYearsConstraint ? '<spring:message code="label.true" />' : '<spring:message code="label.false" />' }}</td>
						<td>{{ p.minEnrolmentYears }}</td>
						<td>{{ p.maxEnrolmentYears }}</td>
					</tr>
				</tbody>
			</table>
		</div>
		
		<div class="panel-footer">
			<a class="bnt btn-default btn-xs" href="#" ng-click="toggleCreateModal('<%= request.getParameter("periodType") %>')">
				<spring:message code="label.RaidesRequests.addPeriod" />
			</a>
			&nbsp;
			<a class="bnt btn-default btn-xs" href="#" ng-click="cleanPeriods('<%= request.getParameter("periodType") %>')">
				<spring:message code="label.RaidesRequests.cleanPeriods" />
			</a>
		</div>
	</div>	

    <div class="modal fade" id="enrolledPeriodsModal_<%= request.getParameter("periodType") %>">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.enrolledPeriod.createModal" />
                    </h4>
                </div>
                <div class="modal-body">
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.RaidesRequests.academicPeriod" />
                        </div>
                        <div class="col-sm-7">
                            <ui-select id="raidesRequests_executionYear"
                                ng-model="$parent.newPeriod.academicPeriod"
                                theme="bootstrap"> <ui-select-match allow-clear="true">
                            {{$select.selected.text}}
                            </ui-select-match> <ui-select-choices
                                repeat="executionYear.id as executionYear in executionYearDataSource | filter: $select.search">
                            <span
                                ng-bind-html="executionYear.text | highlight: $select.search"></span>
                            </ui-select-choices> </ui-select>
                        </div>
                        <div class="col-sm-3">
                            <span class="alert alert-warning btn-xs">
                                <spring:message code="warning.required.field" />
                            </span>
                        </div>                        
                    </div>                
                    <div class="form-group row">
                    	<div class="col-sm-2 control-label">
                    		<spring:message code="label.RaidesRequests.begin" />
                    	</div>
                    	<div class="col-sm-7">
                    		<input id="raidesRequests_begin" class="form-control" type="text" bennu-date="newPeriod.begin" />
                    	</div>
                        <div class="col-sm-3">
                            <span class="alert alert-warning btn-xs">
                                <spring:message code="warning.required.field" />
                            </span>
                        </div>                        
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.RaidesRequests.end" />
                        </div>
                        <div class="col-sm-7">
                            <input id="raidesRequests_end" class="form-control" type="text" bennu-date="newPeriod.end" />
                        </div>
                        <div class="col-sm-3">
                            <span class="alert alert-warning btn-xs">
                                <spring:message code="warning.required.field" />
                            </span>
                        </div>                        
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.RaidesRequests.enrolledInAcademicPeriod" />
                        </div>
                        <div class="col-sm-7">
                            <ui-select id="raidesRequests_enrolledInAcademicPeriod" name="field"
                                ng-model="newPeriod.enrolledInAcademicPeriod"
                                theme="bootstrap"> 
                                <ui-select-match allow-clear="true">
                                    {{$select.selected.name}}
                                </ui-select-match> 
                                <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                                    <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                                </ui-select-choices>
                            </ui-select> 
                        </div>
                        <div class="col-sm-3">
                            <span class="alert alert-warning btn-xs">
                                <spring:message code="warning.required.field" />
                            </span>
                        </div>                        
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.RaidesRequests.enrolmentEctsConstraint" />
                        </div>
                        <div class="col-sm-7">
                            <ui-select id="raidesRequests_enrolmentEctsConstraint" name="field"
                                ng-model="newPeriod.enrolmentEctsConstraint"
                                theme="bootstrap"> 
                                <ui-select-match allow-clear="true">
                                    {{$select.selected.name}}
                                </ui-select-match> 
                                <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                                    <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                                </ui-select-choices>
                            </ui-select> 
                        </div>
                    </div>
                    <div class="form-group row" ng-show="newPeriod.enrolmentEctsConstraint">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.RaidesRequests.minEnrolmentEcts" />
                        </div>
                        <div class="col-sm-7">
                            <input id="raidesRequests_minEnrolmentEcts" class="form-control" type="number" ng-model="newPeriod.minEnrolmentEcts" />
                        </div>
                        <div class="col-sm-3">
                            <span class="alert alert-warning btn-xs">
                                <spring:message code="warning.required.field" />
                            </span>
                        </div>                        
                    </div>
                    <div class="form-group row" ng-show="newPeriod.enrolmentEctsConstraint">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.RaidesRequests.maxEnrolmentEcts" />
                        </div>
                        <div class="col-sm-7">
                            <input id="raidesRequests_maxEnrolmentEcts" class="form-control" type="number" ng-model="newPeriod.maxEnrolmentEcts" />
                        </div>
                        <div class="col-sm-3">
                            <span class="alert alert-warning btn-xs">
                                <spring:message code="warning.required.field" />
                            </span>
                        </div>                        
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.RaidesRequests.enrolmentYearsConstraint" />
                        </div>
                        <div class="col-sm-7">
                            <ui-select id="raidesRequests_enrolmentYearsConstraint" name="field"
                                ng-model="newPeriod.enrolmentYearsConstraint"
                                theme="bootstrap"> 
                                <ui-select-match allow-clear="true">
                                    {{$select.selected.name}}
                                </ui-select-match> 
                                <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                                    <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                                </ui-select-choices>
                            </ui-select> 
                        </div>
                    </div>
                    <div class="form-group row" ng-show="newPeriod.enrolmentYearsConstraint">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.RaidesRequests.minEnrolmentYears" />
                        </div>
                        <div class="col-sm-7">
                            <input id="raidesRequests_minEnrolmentYears" class="form-control" type="number" ng-model="newPeriod.minEnrolmentYears" />
                        </div>
                        <div class="col-sm-3">
                            <span class="alert alert-warning btn-xs">
                                <spring:message code="warning.required.field" />
                            </span>
                        </div>                        
                    </div>
                    <div class="form-group row" ng-show="newPeriod.enrolmentYearsConstraint">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.RaidesRequests.maxEnrolmentYears" />
                        </div>
                        <div class="col-sm-7">
                            <input id="raidesRequests_maxEnrolmentYears" class="form-control" type="number" ng-model="newPeriod.maxEnrolmentYears" />
                        </div>
                        <div class="col-sm-3">
                            <span class="alert alert-warning btn-xs">
                                <spring:message code="warning.required.field" />
                            </span>
                        </div>                        
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="createButton" class="btn btn-primary" type="button" ng-click="addPeriod('<%= request.getParameter("periodType") %>')">
                        <spring:message code="label.event.create" />
                    </button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <!-- /.modal -->
 
 