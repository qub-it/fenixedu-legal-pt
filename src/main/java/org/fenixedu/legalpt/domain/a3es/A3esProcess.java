package org.fenixedu.legalpt.domain.a3es;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.exceptions.LegalPTDomainException;
import org.joda.time.DateTime;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class A3esProcess extends A3esProcess_Base {

    protected A3esProcess() {
        super();
        setRoot(Bennu.getInstance());
    }

    @Atomic
    public void delete() {
        setRoot(null);

        setPeriod(null);
        setDegreeCurricularPlan(null);

        super.deleteDomainObject();
    }

    protected void init(final A3esPeriod period, final String identifier, final String description,
            final DegreeCurricularPlan plan) {
        setPeriod(period);
        setIdentifier(identifier);
        setDescription(description);
        setDegreeCurricularPlan(plan);

        checkRules();
    }

    public void checkRules() {

        if (getPeriod() == null) {
            throw new LegalPTDomainException("error.A3esProcess.period.required");
        }

        if (StringUtils.isBlank(getIdentifier())) {
            throw new LegalPTDomainException("error.A3esProcess.identifier.required");
        }

        if (getDegreeCurricularPlan() == null) {
            throw new LegalPTDomainException("error.A3esProcess.degreeCurricularPlan.required");
        }

        if (find(getPeriod(), getIdentifier(), getDegreeCurricularPlan()).size() != 1) {
            throw new LegalPTDomainException("error.A3esProcess.duplicated");
        }
    }

    @Atomic
    static public A3esProcess create(final A3esPeriod period, final String identifier, final String description,
            final DegreeCurricularPlan plan) {

        final A3esProcess result = new A3esProcess();
        result.init(period, identifier, description, plan);
        return result;
    }

    @Atomic
    public A3esProcess edit(final String identifier, final String description, final DegreeCurricularPlan plan) {

        this.init(getPeriod(), identifier, description, plan);
        return this;
    }

    static public Set<A3esProcess> find(final A3esPeriod period, final String identifier, final DegreeCurricularPlan plan) {

        return period == null ? Sets.newHashSet() : period.getProcessSet().stream()

                .filter(i -> StringUtils.isBlank(identifier) || StringUtils.equalsIgnoreCase(i.getIdentifier(), identifier))

                .filter(i -> plan == null || i.getDegreeCurricularPlan() == plan)

                .collect(Collectors.toSet());
    }

    /**
     * for now, assuming that only one process type will occour only once each year
     */
    static public A3esPeriod getPeriodUnique(final ExecutionYear year, final A3esProcessType type) {
        final Set<A3esPeriod> periods = A3esPeriod.find(year, type, (DateTime) null, (DateTime) null);
        return periods.size() != 1 ? null : periods.iterator().next();
    }

    public ExecutionYear getExecutionYear() {
        return getPeriod().getExecutionYear();
    }

    public A3esProcessType getType() {
        return getPeriod().getType();
    }

    public String getName() {
        return String.format("%s/%s", getPeriod().getDescription(), getIdentifier());
    }

}
