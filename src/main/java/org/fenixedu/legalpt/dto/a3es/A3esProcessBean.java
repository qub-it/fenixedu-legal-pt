package org.fenixedu.legalpt.dto.a3es;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.legalpt.domain.a3es.A3esProcess;
import org.fenixedu.legalpt.services.a3es.process.A3esHarvestCoursesDataService;
import org.fenixedu.legalpt.services.a3es.process.A3esHarvestTeachersDataService;

public class A3esProcessBean extends A3esPeriodBean implements IBean {

    private A3esProcess process;

    private String identifier;
    private String name;
    private String description;

    private DegreeCurricularPlan degreeCurricularPlan;
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource;

    private Set<A3esCourseBean> coursesData;
    private Set<A3esTeacherBean> teachersData;

    private String user;
    private String password;
    private String formId;
    private List<String> selectedIds = new ArrayList<>();

    public A3esProcessBean() {
        updateDataSources();
    }

    public A3esProcessBean(final A3esProcess input) {
        setProcess(input);
        setPeriod(input.getPeriod());
        setExecutionYear(input.getPeriod().getExecutionYear());
        setType(input.getPeriod().getType());
        setIdentifier(input.getIdentifier());
        setName(input.getName());
        setDescription(input.getDescription());
        setDegreeCurricularPlan(input.getDegreeCurricularPlan());

        updateDataSources();
    }

    public A3esProcess getProcess() {
        return process;
    }

    public void setProcess(final A3esProcess input) {
        this.process = input;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String input) {
        this.identifier = input;
    }

    public String getName() {
        return name;
    }

    public void setName(final String input) {
        this.name = input;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String input) {
        this.description = input;
    }

    public String getDegreeCode() {
        String result = null;

        final DegreeCurricularPlan plan = getDegreeCurricularPlan();
        final Degree degree = plan == null ? null : plan.getDegree();
        final String ministryCode = degree == null ? null : degree.getMinistryCode();
        final String code = degree == null ? null : degree.getCode();

        if (ministryCode != null) {
            result = ministryCode;
        }

        if (code != null && !code.equals(ministryCode)) {
            result = result.isEmpty() ? code : (result + " [" + code + "]");
        }

        return result;
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return degreeCurricularPlan;
    }

    public void setDegreeCurricularPlan(final DegreeCurricularPlan input) {
        this.degreeCurricularPlan = input;
    }

    public List<TupleDataSourceBean> getDegreeCurricularPlanDataSource() {
        if (this.degreeCurricularPlanDataSource == null) {
            this.degreeCurricularPlanDataSource = new ArrayList<>();
        }

        return this.degreeCurricularPlanDataSource;
    }

    public Set<A3esCourseBean> getCoursesData() {
        if (this.coursesData == null) {
            this.coursesData = new HashSet<>();
        }

        return this.coursesData;
    }

    public Set<A3esTeacherBean> getTeachersData() {
        if (this.teachersData == null) {
            this.teachersData = new HashSet<>();
        }

        return this.teachersData;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public List<String> getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(final List<String> input) {
        this.selectedIds = input;
    }

    @Override
    public void updateDataSources() {
        super.updateDataSources();
        updateDegreeCurricularPlanDataSource();
    }

    private void updateDegreeCurricularPlanDataSource() {
        if (getDegreeCurricularPlanDataSource().isEmpty() && getExecutionYear() != null) {

            Degree.readBolonhaDegrees().stream().flatMap(d -> d.getDegreeCurricularPlansForYear(getExecutionYear()).stream())
                    .sorted(DegreeCurricularPlan.COMPARATOR_BY_PRESENTATION_NAME).map(x -> {

                        final TupleDataSourceBean tuple = new TupleDataSourceBean();
                        tuple.setId(x.getExternalId());
                        tuple.setText(x.getPresentationName());
                        return tuple;

                    }).collect(Collectors.toCollection(() -> getDegreeCurricularPlanDataSource()));
        }
    }

    public void updateCoursesData() {
        if (getCoursesData().isEmpty() && getExecutionYear() != null && getDegreeCurricularPlan() != null) {
            new A3esHarvestCoursesDataService(this);
        }
    }

    public void updateTeachersData() {
        if (getTeachersData().isEmpty() && getExecutionYear() != null && getDegreeCurricularPlan() != null) {
            new A3esHarvestTeachersDataService(this);
        }
    }

}
