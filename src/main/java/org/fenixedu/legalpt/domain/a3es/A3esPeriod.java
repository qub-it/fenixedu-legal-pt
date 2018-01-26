package org.fenixedu.legalpt.domain.a3es;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.legalpt.domain.exceptions.LegalPTDomainException;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class A3esPeriod extends A3esPeriod_Base {

    protected A3esPeriod() {
        super();
        setRoot(Bennu.getInstance());
    }

    @Atomic
    public void delete() {
        setRoot(null);

        setExecutionYear(null);

        super.deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(final Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);

        if (!getProcessSet().isEmpty()) {
            blockers.add(LegalPTUtil.bundle("error.A3esPeriod.has.A3esProcess"));
        }
    }

    protected void init(final ExecutionYear year, final A3esProcessType type, final DateTime fillInDateBegin,
            final DateTime fillInDateEnd) {

        setType(type);
        setExecutionYear(year);
        setFillInDateBegin(fillInDateBegin);
        setFillInDateEnd(fillInDateEnd);

        checkRules();
    }

    public void checkRules() {

        if (getExecutionYear() == null) {
            throw new LegalPTDomainException("error.A3esPeriod.executionYear.required");
        }

        if (getType() == null) {
            throw new LegalPTDomainException("error.A3esPeriod.type.required");
        }

        if (find(getExecutionYear(), getType(), (DateTime) null, (DateTime) null).size() != 1) {
            throw new LegalPTDomainException("error.A3esPeriod.duplicated");
        }

        if (getFillInDateBegin() == null) {
            throw new LegalPTDomainException("error.A3esPeriod.fillInDateBegin.required");
        }

        if (getFillInDateEnd() == null) {
            throw new LegalPTDomainException("error.A3esPeriod.fillInDateEnd.required");
        }

        if (getFillInDateInterval() == null) {
            throw new LegalPTDomainException("error.A3esPeriod.fillInInterval.inconsistent");
        }
    }

    @Atomic
    static public A3esPeriod create(final ExecutionYear year, final A3esProcessType type, final DateTime fillInDateBegin,
            final DateTime fillInDateEnd) {

        final A3esPeriod result = new A3esPeriod();
        result.init(year, type, fillInDateBegin, fillInDateEnd);
        return result;
    }

    @Atomic
    public A3esPeriod edit(final DateTime fillInDateBegin, final DateTime fillInDateEnd) {

        this.init(getExecutionYear(), getType(), fillInDateBegin, fillInDateEnd);
        return this;
    }

    static public Set<A3esPeriod> find(final ExecutionYear year, final A3esProcessType type, final DateTime fillInDateBegin,
            final DateTime fillInDateEnd) {

        return year == null ? Sets.newHashSet() : year.getA3esPeriodSet().stream()

                .filter(i -> type == null || i.getType() == type)

                .filter(i -> fillInDateBegin == null || i.getFillInDateBegin().isAfter(fillInDateBegin))

                .filter(i -> fillInDateEnd == null || i.getFillInDateEnd().isBefore(fillInDateEnd))

                .collect(Collectors.toSet());
    }

    public Interval getFillInDateInterval() {
        Interval result = null;

        try {
            result = new Interval(getFillInDateBegin(), getFillInDateEnd());
        } catch (final Throwable t) {
        }

        return result;
    }

    public Boolean isInFillingPeriod() {
        return getFillInDateInterval().containsNow();
    }

    public String getDescription() {
        final ExecutionYear year = getExecutionYear();
        final String yearName = year.getQualifiedName();
        return String.format("%s/%s%s", getType().getCode(), yearName.substring(2, 4), yearName.substring(7));
    }

}
