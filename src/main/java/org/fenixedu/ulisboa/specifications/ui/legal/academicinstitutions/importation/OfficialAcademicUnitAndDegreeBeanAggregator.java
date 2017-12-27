package org.fenixedu.ulisboa.specifications.ui.legal.academicinstitutions.importation;

import java.io.Serializable;
import java.util.List;

import org.fenixedu.bennu.IBean;

public class OfficialAcademicUnitAndDegreeBeanAggregator implements IBean, Serializable {

    private static final long serialVersionUID = 1L;

    private List<OfficialAcademicUnitBean> academicUnitBeanList;
    private List<OfficialDegreeDesignationBean> degreeDesignationBeanList;

    public OfficialAcademicUnitAndDegreeBeanAggregator(List<OfficialAcademicUnitBean> academicUnitBeanList,
            List<OfficialDegreeDesignationBean> degreeDesignationBeanList) {

        this.academicUnitBeanList = academicUnitBeanList;
        this.degreeDesignationBeanList = degreeDesignationBeanList;
    }

    public List<OfficialAcademicUnitBean> getAcademicUnitBeanList() {
        return academicUnitBeanList;
    }

    public List<OfficialDegreeDesignationBean> getDegreeDesignationBeanList() {
        return degreeDesignationBeanList;
    }

}
