package org.fenixedu.legalpt.services.rebides.process;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Coordinator;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.TeacherAuthorization;
import org.fenixedu.legalpt.domain.LegalReportContext;
import org.fenixedu.legalpt.domain.rebides.mapping.RebidesMappingType;
import org.fenixedu.legalpt.dto.rebides.CareerActivitiesBean;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.joda.time.YearMonthDay;

public class CareerActivitiesService {

    private final TeacherAuthorization contract;
    private final Teacher teacher;
    private final YearMonthDay lastDayOfTheYear;
    private final ExecutionYear executionYear;
    private LegalReport report;

    public static class Department {
        public static final String OTHER_DEPARTMENT = "00";
        public static final String NO_DEPARTMENT = "99";
    }

    public static class UnitHL {
        public static final Integer ANUAL = 1;
        public static final Integer SEMESTER = 2;
    }

    public CareerActivitiesService(final LegalReport report, Teacher teacher, final YearMonthDay lastDayOfTheYear,
            ExecutionYear executionYear) {
        this.report = report;

        this.teacher = teacher;
        this.lastDayOfTheYear = lastDayOfTheYear;
        this.executionYear = executionYear;
        this.contract = teacher.getLatestTeacherAuthorizationInInterval(lastDayOfTheYear.toInterval()).orElse(null);
    }

    public CareerActivitiesBean getCareerActivitiesData() {
        final CareerActivitiesBean bean = new CareerActivitiesBean();

        fillDepartment(bean); // optional
        fillOtherDepartment(bean); // optional
        fillCategory(bean); // required
        fillGuest(bean); // required
        fillEquated(bean); // required   //actingTeacher?
        fillAptitudeTests(bean); // optional
        fillAggregationTests(bean); // optional
        fillExpertTests(bean); // optional
        fillServiceRegime(bean); // required
        fillPartialRegime(bean); // optional
        fillBindingRegime(bean); // required
        fillContractWageLevel(bean); // optional
        fillResearchPercentage(bean); // required
        fillTeaching(bean); // required
        fillTeachingHours(bean); // required (if teaching=true)
        fillTeachingHoursUnit(bean); // required (if teachingWorkingHours != empty)
        fillRootUnitPresident(bean); // required
        fillRootUnitVicePrincipal(bean); // required
        fillRootUnitPresident(bean); // required
        fillUnitCoordinator(bean); // required
        fillCoordenatorGeneralCouncil(bean); // required
        fillManagementCouncilActivities(bean); // required
        fillScientificCouncilActivities(bean); // required
        fillPedagogicCouncilActivitiesl(bean); // required
        fillCoachingOrResearchProjectActivities(bean); // required
        fillOtherActivity(bean); // required
        fillOtherActivityDescription(bean); // optional (if actoutra = true)

        return bean;
    }

    private void fillDepartment(CareerActivitiesBean bean) {
        if (contract.getDepartment() != null && StringUtils.isNotEmpty(contract.getDepartment().getName())) {
            if (LegalMapping.find(report, RebidesMappingType.DEPARTMENT).translate(contract.getDepartment()) != null) {
                String department = LegalMapping.find(report, RebidesMappingType.DEPARTMENT).translate(contract.getDepartment());
                if (RebidesService.validateMaxFieldSize(teacher, "label.department", department, RebidesService.LIMIT_2CHARS)) {
                    bean.setDepartment(department);
                }
            } else {
                bean.setDepartment(CareerActivitiesService.Department.OTHER_DEPARTMENT);
            }
        } else {
            bean.setDepartment(CareerActivitiesService.Department.NO_DEPARTMENT);
        }
    }

    private void fillOtherDepartment(CareerActivitiesBean bean) {
        if (bean.getDepartment().equals(CareerActivitiesService.Department.OTHER_DEPARTMENT)) {
            String otherDepartmentName = contract.getDepartment().getName();
            if (RebidesService.validateMaxFieldSize(teacher, "label.otherDepartment", otherDepartmentName,
                    RebidesService.LIMIT_160CHARS)) {
                bean.setOtherDepartment(otherDepartmentName);
            }
        }
    }

    private void fillCategory(CareerActivitiesBean bean) {
        if (contract.getTeacherCategory() != null) {
            if (LegalMapping.find(report, RebidesMappingType.CONTRACT_CATEGORY)
                    .translate(contract.getTeacherCategory()) != null) {
                String contractCategory =
                        LegalMapping.find(report, RebidesMappingType.CONTRACT_CATEGORY).translate(contract.getTeacherCategory());
                if (RebidesService.validateMaxFieldSize(teacher, "Contract.contractCategory", contractCategory,
                        RebidesService.LIMIT_2CHARS)) {
                    bean.setCategory(contractCategory);
                }
            } else {
                LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                        RebidesService.createMissingMappingMessage("Contract.contractCategory",
                                contract.getTeacherCategory().getName().getContent()));
            }
        } else {
            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                    RebidesService.createMissingFieldMessage("Contract.contractCategory"));
        }
    }

    private void fillGuest(CareerActivitiesBean bean) {
        //TODO: check semantics
        bean.setGuest(!contract.isContracted());
    }

    private void fillEquated(CareerActivitiesBean bean) {
        bean.setSchoolStaff(contract.isContracted());
    }

    private void fillAptitudeTests(CareerActivitiesBean bean) {
        final String aptitudeTests = getAptitudeTests();
        if (StringUtils.isNotEmpty(getAptitudeTests())) {
            if (RebidesService.validateMaxFieldSize(teacher, "CareerSituation.aptitude", aptitudeTests,
                    RebidesService.LIMIT_160CHARS)) {
                bean.setAptitudeTests(aptitudeTests);
            }
        }
    }

    private String getAptitudeTests() {
        // TODO: implement
        return null;
    }

    private void fillAggregationTests(CareerActivitiesBean bean) {
        final String aggregationTests = getAggregationTests();
        if (StringUtils.isNotEmpty(aggregationTests)) {
            if (RebidesService.validateMaxFieldSize(teacher, "CareerSituation.aggregation", aggregationTests,
                    RebidesService.LIMIT_160CHARS)) {
                bean.setAggregationTests(aggregationTests);
            }
        }
    }

    private String getAggregationTests() {
        // TODO: implement
        return null;
    }

    private void fillExpertTests(CareerActivitiesBean bean) {
        final String expertTests = getExpertTests();
        if (StringUtils.isNotEmpty(expertTests)) {
            if (RebidesService.validateMaxFieldSize(teacher, "CareerSituation.expert", expertTests,
                    RebidesService.LIMIT_160CHARS)) {
                bean.setExpertTests(expertTests);
            }
        }
    }

    private String getExpertTests() {
        // TODO: implement
        return null;
    }

    private void fillServiceRegime(CareerActivitiesBean bean) {
//        if (contract.getContractServiceRegime() != null) {
//            if (LegalMapping.find(report, RebidesMappingType.CONTRACT_SERVICE_REGIME)
//                    .translate(contract.getContractServiceRegime()) != null) {
//                String serviceRegime = Mapping.find(report, RebidesMappingType.CONTRACT_SERVICE_REGIME)
//                        .translate(contract.getContractServiceRegime());
//                if (RebidesService.validateMaxFieldSize(teacher, "Contract.contractServiceRegime", serviceRegime,
//                        RebidesService.LIMIT_1CHARS)) {
//                    bean.setServiceRegime(serviceRegime);
//                }
//            } else {
//                LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
//                        RebidesService.createMissingMappingMessage("Contract.contractServiceRegime",
//                                contract.getContractServiceRegime().getName().getContent()));
//            }
//        } else {
//            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
//                    RebidesService.createMissingFieldMessage("Contract.contractServiceRegime"));
//        }
    }

    private void fillPartialRegime(CareerActivitiesBean bean) {
//        if (contract.getContractServiceRegime() != null
//                && QubStringUtil.isNotEmpty(contract.getContractServiceRegime().getName().getContent())) {
//
//            if (contract.getContractServiceRegime().getRequiresWorkPercentage()) {
//
//                if (contract.getWorkPercentage() != null) {
//                    if (RebidesService.validatePercentageField(teacher, "label.partialRegime", contract.getWorkPercentage(),
//                            RebidesService.LIMIT_0PERCENTAGE, RebidesService.LIMIT_99PERCENTAGE)) {
//                        bean.setPartialRegime(String.valueOf(contract.getWorkPercentage()));
//                    }
//                } else {
//                    LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
//                            RebidesService.createMissingFieldMessage("label.partialRegime"));
//                }
//            }
//        }
    }

    private void fillBindingRegime(CareerActivitiesBean bean) {
//        if (contract.getContractBindingRegime() != null) {
//            if (Mapping.find(report, RebidesMappingType.CONTRACT_BINDING_REGIME)
//                    .translate(contract.getContractBindingRegime()) != null) {
//                String bindingRegime = Mapping.find(report, RebidesMappingType.CONTRACT_BINDING_REGIME)
//                        .translate(contract.getContractBindingRegime());
//                if (RebidesService.validateMaxFieldSize(teacher, "Contract.contractBindingRegime", bindingRegime,
//                        RebidesService.LIMIT_2CHARS)) {
//                    bean.setBindingRegime(bindingRegime);
//                }
//            } else {
//                LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
//                        RebidesService.createMissingMappingMessage("Contract.contractBindingRegime",
//                                contract.getContractBindingRegime().getName().getContent()));
//            }
//        } else {
//            LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
//                    RebidesService.createMissingFieldMessage("Contract.contractBindingRegime"));
//        }
    }

    private void fillContractWageLevel(CareerActivitiesBean bean) {
//        if (contract.getContractWageLevel() != null) {
//            if (LegalMapping.find(report, RebidesMappingType.CONTRACT_WAGE_LEVEL)
//                    .translate(contract.getContractWageLevel()) != null) {
//
//                String contractWageLevel = LegalMapping.find(report, RebidesMappingType.CONTRACT_WAGE_LEVEL)
//                        .translate(contract.getContractWageLevel());
//
//                if (RebidesService.validateMaxFieldSize(teacher, "Contract.contractWageLevel", contractWageLevel,
//                        RebidesService.LIMIT_3CHARS)) {
//                    bean.setContractWageLevel(contractWageLevel);
//                }
//            } else {
//                LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
//                        RebidesService.createMissingMappingMessage("Contract.contractWageLevel",
//                                contract.getContractWageLevel().getName().getContent()));
//            }
//        }
    }

    private void fillResearchPercentage(CareerActivitiesBean bean) {
//        Integer researchPercentage = contract.getResearchPercentage() == null ? 0 : contract.getResearchPercentage();
//        RebidesService.validatePercentageField(teacher, "Contract.researchPercentage", researchPercentage,
//                RebidesService.LIMIT_0PERCENTAGE, RebidesService.LIMIT_100PERCENTAGE);
//        bean.setResearchPercentage(new BigDecimal(researchPercentage));
    }

    private void fillTeaching(CareerActivitiesBean bean) {
//        bean.setTeaching(teacher.getProfessorships(executionYear).size() > 0
//                && professorshipTeachingHours(bean).compareTo(BigDecimal.ZERO) > 0);
    }

    private void fillTeachingHours(CareerActivitiesBean bean) {
//        if (bean.getTeaching()) {
//            Double teachingHours = 0.0;
//            for (Professorship professorship : teacher.getProfessorships(executionYear)) {
//                teachingHours += professorship.getShifProfessorshipsHours();
//            }
//            bean.setTeachingHoursPercentage(professorshipTeachingHours(bean));
//        }
    }

    private BigDecimal professorshipTeachingHours(final CareerActivitiesBean bean) {
        Double teachingHours = 0.0;
//        for (Professorship professorship : teacher.getProfessorships(executionYear)) {
//            teachingHours += professorship.getShifProfessorshipsHours();
//        }

        return new BigDecimal(teachingHours);
    }

    private void fillTeachingHoursUnit(CareerActivitiesBean bean) {
        if (bean.getTeaching() != null && bean.getTeaching()) {
            bean.setTeachingHoursUnit(UnitHL.ANUAL);
        }
    }

    private void fillRootUnitPrincipal(CareerActivitiesBean bean) {
        //Reitor ou presidente ou órgão correspondente
        bean.setRootUnitPresident(isRootUnitPresident());
    }

    private Boolean isRootUnitPresident() {
        // TODO: implement
        return false;
    }

    private void fillRootUnitVicePrincipal(CareerActivitiesBean bean) {
        //Vice-reitor ou vice-presidente ou órgão correspondente
        bean.setRootUnitVicePresident(isRootUnitVicePresident());
    }

    private Boolean isRootUnitVicePresident() {
        // TODO: implement
        return false;
    }

    private void fillRootUnitPresident(CareerActivitiesBean bean) {
        //Diretor ou presidente da unidade orgânica - ISA = President of Management Council  
        bean.setUnitPresident(isUnitPresident());
    }

    private Boolean isUnitPresident() {
        // TODO: implement
        return false;
    }

    private void fillUnitCoordinator(CareerActivitiesBean bean) {
        //Atividades de coordenação ou direção de centro de investigação, departamento ou equivalente - Presidente do departamento / coordenador de curso / centro de investigação
        bean.setUnitCoordinator(isUnitCoordinator());
    }

    private Boolean isUnitCoordinator() {
        // TODO: implement
        return false;
    }

    private boolean isDegreeCoordinator() {
        for (final Coordinator coordinator : teacher.getPerson().getCoordinatorsSet()) {
            if (coordinator.getExecutionDegree().getExecutionYear() == executionYear) {
                return true;
            }
        }

        return false;
    }

    private void fillCoordenatorGeneralCouncil(CareerActivitiesBean bean) {
        //Atividades de conselho geral ou órgão correspondente - President, vice-president, members...
        bean.setCoordenatorGeneralCouncil(isGeneralCouncilCoordinator());
    }

    private Boolean isGeneralCouncilCoordinator() {
        // TODO: implement
        return false;
    }

    private void fillManagementCouncilActivities(CareerActivitiesBean bean) {
        //Atividades de conselho de gestão ou órgão correspondente - Vice-president, members...
        bean.setManagementCouncilActivities(hasManagementCouncilActivities());

    }

    private Boolean hasManagementCouncilActivities() {
        // TODO: implement
        return false;
    }

    private void fillScientificCouncilActivities(CareerActivitiesBean bean) {
        //Atividades de conselho de gestão ou órgão correspondente - President, vice-president, members...
        bean.setScientificCouncilActivities(hasScientificCouncilActivities());
    }

    private Boolean hasScientificCouncilActivities() {
        // TODO: implement
        return false;
    }

    private void fillPedagogicCouncilActivitiesl(CareerActivitiesBean bean) {
        //Atividades de conselho pedagógico - Presidente, vice (membros não são considerados cargos) - RoleType PedagogicalCouncil) 
        bean.setPedagogicCouncilActivities(hasPedagogicCouncilActivities());
    }

    private Boolean hasPedagogicCouncilActivities() {
        // TODO: implement
        return false;
    }

    private void fillCoachingOrResearchProjectActivities(CareerActivitiesBean bean) {
        //Atividades de formação ou em projetos de investigação
        bean.setCoachingOrResearchProjectActivities(hasCoachingOrResearchProjectActivities());

    }

    private Boolean hasCoachingOrResearchProjectActivities() {
        // TODO: implement
        return false;
    }

    private void fillOtherActivity(CareerActivitiesBean bean) {
        //Outra atividade
        bean.setOtherActivity(hasOtherActivity());

    }

    private Boolean hasOtherActivity() {
        // TODO implement
        return false;
    }

    private void fillOtherActivityDescription(CareerActivitiesBean bean) {
        //Descrição da outra atividade
        if (bean.getOtherActivity() != null && bean.getOtherActivity()) {
            //String otherActivityDescription = teacher.getPerson().getCareerSituation().getOtherActivityDescription();

            if (StringUtils.isNotEmpty(getOtherActivityDescription())) {

                if (RebidesService.validateMaxFieldSize(teacher, "CareerSituation.otherActivityDescription",
                        getOtherActivityDescription(), RebidesService.LIMIT_255CHARS)) {
                    bean.setOtherActivityDescription(getOtherActivityDescription());
                }

            } else {
                LegalReportContext.addError(RebidesService.createSubjectForReport(teacher),
                        RebidesService.createMissingFieldMessage("CareerSituation.otherActivityDescription"));
            }
        }
    }

    private String getOtherActivityDescription() {
        //TODO: implement
        return null;
    }

}
