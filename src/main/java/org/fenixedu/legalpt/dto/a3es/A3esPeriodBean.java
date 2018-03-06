package org.fenixedu.legalpt.dto.a3es;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.legalpt.domain.a3es.A3esPeriod;
import org.fenixedu.legalpt.domain.a3es.A3esProcessType;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;

public class A3esPeriodBean implements IBean {

    private A3esPeriod period;

    private ExecutionYear executionYear;
    private List<TupleDataSourceBean> executionYearDataSource;

    private A3esProcessType type;
    private List<TupleDataSourceBean> typeDataSource;

    private DateTime fillInDateBegin;
    private DateTime fillInDateEnd;

    public A3esPeriodBean() {
        updateDataSources();
    }

    public A3esPeriodBean(final A3esPeriod input) {
        setPeriod(input);
        setExecutionYear(input.getExecutionYear());
        setType(input.getType());
        setFillInDateBegin(input.getFillInDateBegin());
        setFillInDateEnd(input.getFillInDateEnd());

        updateDataSources();
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(final ExecutionYear input) {
        this.executionYear = input;
    }

    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        if (this.executionYearDataSource == null) {
            this.executionYearDataSource = new ArrayList<>();
        }

        return this.executionYearDataSource;
    }

    public A3esProcessType getType() {
        return type;
    }

    public void setType(final A3esProcessType input) {
        this.type = input;
    }

    public List<TupleDataSourceBean> getTypeDataSource() {
        if (this.typeDataSource == null) {
            this.typeDataSource = new ArrayList<>();
        }

        return typeDataSource;
    }

    public DateTime getFillInDateBegin() {
        return fillInDateBegin;
    }

    public void setFillInDateBegin(final DateTime input) {
        this.fillInDateBegin = input;
    }

    public DateTime getFillInDateEnd() {
        return fillInDateEnd;
    }

    public void setFillInDateEnd(final DateTime input) {
        this.fillInDateEnd = input;
    }

    public A3esPeriod getPeriod() {
        return period;
    }

    public void setPeriod(final A3esPeriod input) {
        this.period = input;
    }

    public void updateDataSources() {
        updateExecutionYearDataSource();
        updateTypeDataSource();
    }

    private void updateExecutionYearDataSource() {
        if (getExecutionYearDataSource().isEmpty()) {

            ExecutionYear.readNotClosedExecutionYears().stream().sorted(ExecutionYear.COMPARATOR_BY_BEGIN_DATE.reversed())
                    .map(x -> {

                        final TupleDataSourceBean tuple = new TupleDataSourceBean();
                        tuple.setId(x.getExternalId());
                        tuple.setText(x.getQualifiedName());
                        return tuple;

                    }).collect(Collectors.toCollection(() -> getExecutionYearDataSource()));
        }
    }

    private void updateTypeDataSource() {
        if (getTypeDataSource().isEmpty()) {

            Lists.newArrayList(A3esProcessType.values()).stream().map(x -> {

                final TupleDataSourceBean tuple = new TupleDataSourceBean();
                tuple.setId(x.name());
                tuple.setText(x.getCode() + " - " + x.getLocalizedName().getContent());
                return tuple;

            }).collect(Collectors.toCollection(() -> getTypeDataSource()));
        }
    }

}
