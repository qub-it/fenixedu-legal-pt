package org.fenixedu.legalpt.domain.raides.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.bennu.IBean;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

public class RaidesRequestPeriodParameter implements Serializable, IBean {

    public static final Comparator<RaidesRequestPeriodParameter> COMPARATOR_BY_BEGIN_DATE =
            new Comparator<RaidesRequestPeriodParameter>() {

                @Override
                public int compare(final RaidesRequestPeriodParameter o1, final RaidesRequestPeriodParameter o2) {
                    return o1.getBegin().compareTo(o2.getBegin());
                }

            };

    private static final long serialVersionUID = 1L;

    private ExecutionYear academicPeriod;
    private LocalDate begin;
    private LocalDate end;
    private boolean enrolledInAcademicPeriod;
    private RaidesPeriodInputType periodInputType;
    private boolean enrolmentEctsConstraint;
    private BigDecimal minEnrolmentEcts;
    private BigDecimal maxEnrolmentEcts;
    private boolean enrolmentYearsConstraint;
    private Integer minEnrolmentYears;
    private Integer maxEnrolmentYears;
    private Set<DegreeType> degreeTypes = new HashSet<>();

    public RaidesRequestPeriodParameter(final ExecutionYear academicPeriod, final LocalDate begin, final LocalDate end,
            final boolean enrolledInAcademicPeriod, final RaidesPeriodInputType periodInputType,
            final boolean enrolmentEctsConstraint, final BigDecimal minEnrolmentEcts, final BigDecimal maxEnrolmentEcts,
            final boolean enrolmentYearsConstraint, final Integer minEnrolmentYears, final Integer maxEnrolmentYears) {

        setAcademicPeriod(academicPeriod);
        setBegin(begin);
        setEnd(end);
        setEnrolledInAcademicPeriod(enrolledInAcademicPeriod);
        setPeriodInputType(periodInputType);
        setEnrolmentEctsConstraint(enrolmentEctsConstraint);
        setMinEnrolmentEcts(minEnrolmentEcts);
        setMaxEnrolmentEcts(maxEnrolmentEcts);
        setEnrolmentYearsConstraint(enrolmentYearsConstraint);
        setMinEnrolmentYears(minEnrolmentYears);
        setMaxEnrolmentYears(maxEnrolmentYears);

        checkRules();
    }

    public RaidesRequestPeriodParameter() {
    }

    private void checkRules() {
        if (getAcademicPeriod() == null) {
            throw new IllegalArgumentException("error.RaidesReportPeriodInput.academicPeriod.required");
        }
        if (getBegin() == null) {
            throw new IllegalArgumentException("error.RaidesReportPeriodInput.begin.required");
        }
        if (getEnd() == null) {
            throw new IllegalArgumentException("error.RaidesReportPeriodInput.end.required");
        }
        if (getPeriodInputType() == null) {
            throw new IllegalArgumentException("error.RaidesReportPeriodInput.periodInputType.required");
        }
    }

    public Interval getInterval() {
        return new Interval(getBegin().toDateTimeAtStartOfDay(), getEnd().toDateTimeAtStartOfDay().plusDays(1).minusSeconds(1));
    }

    /* ****************
     * GETTERS & SETTERS
     * ****************
     */

    public ExecutionYear getAcademicPeriod() {
        return academicPeriod;
    }

    public void setAcademicPeriod(ExecutionYear academicPeriod) {
        this.academicPeriod = academicPeriod;
    }

    public LocalDate getBegin() {
        return begin;
    }

    public void setBegin(LocalDate begin) {
        this.begin = begin;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public boolean isEnrolledInAcademicPeriod() {
        return enrolledInAcademicPeriod;
    }

    public void setEnrolledInAcademicPeriod(boolean enrolledInAcademicPeriod) {
        this.enrolledInAcademicPeriod = enrolledInAcademicPeriod;
    }

    public RaidesPeriodInputType getPeriodInputType() {
        return periodInputType;
    }

    public void setPeriodInputType(RaidesPeriodInputType periodInputType) {
        this.periodInputType = periodInputType;
    }

    public boolean isEnrolmentEctsConstraint() {
        return enrolmentEctsConstraint;
    }

    public void setEnrolmentEctsConstraint(boolean enrolmentEctsConstraint) {
        this.enrolmentEctsConstraint = enrolmentEctsConstraint;
    }

    public BigDecimal getMinEnrolmentEcts() {
        return minEnrolmentEcts;
    }

    public void setMinEnrolmentEcts(BigDecimal minEnrolmentEcts) {
        this.minEnrolmentEcts = minEnrolmentEcts;
    }

    public BigDecimal getMaxEnrolmentEcts() {
        return maxEnrolmentEcts;
    }

    public void setMaxEnrolmentEcts(BigDecimal maxEnrolmentEcts) {
        this.maxEnrolmentEcts = maxEnrolmentEcts;
    }

    public boolean isEnrolmentYearsConstraint() {
        return enrolmentYearsConstraint;
    }

    public void setEnrolmentYearsConstraint(boolean enrolmentYearsConstraint) {
        this.enrolmentYearsConstraint = enrolmentYearsConstraint;
    }

    public Integer getMinEnrolmentYears() {
        return minEnrolmentYears;
    }

    public void setMinEnrolmentYears(Integer minEnrolmentYears) {
        this.minEnrolmentYears = minEnrolmentYears;
    }

    public Integer getMaxEnrolmentYears() {
        return maxEnrolmentYears;
    }

    public void setMaxEnrolmentYears(Integer maxEnrolmentYears) {
        this.maxEnrolmentYears = maxEnrolmentYears;
    }

    public void addDegreeTypes(final Collection<DegreeType> degreeTypes) {
        this.degreeTypes.addAll(degreeTypes);
    }
    
    public Set<DegreeType> getDegreeTypes() {
        return degreeTypes;
    }

    public RaidesRequestPeriodParameter copy() {
        final RaidesRequestPeriodParameter result = new RaidesRequestPeriodParameter();
        result.setAcademicPeriod(getAcademicPeriod());
        result.setBegin(getBegin());
        result.setEnd(getEnd());
        result.setEnrolledInAcademicPeriod(this.enrolledInAcademicPeriod);
        result.setEnrolmentEctsConstraint(this.enrolmentEctsConstraint);
        result.setEnrolmentYearsConstraint(this.enrolmentYearsConstraint);
        result.setMinEnrolmentEcts(getMinEnrolmentEcts());
        result.setMaxEnrolmentEcts(getMaxEnrolmentEcts());
        result.setMinEnrolmentYears(getMinEnrolmentYears());
        result.setMaxEnrolmentYears(getMaxEnrolmentYears());
        result.setPeriodInputType(getPeriodInputType());
        result.getDegreeTypes().addAll(getDegreeTypes());

        return result;
    }

}
