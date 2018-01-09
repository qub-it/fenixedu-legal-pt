package org.fenixedu.legalpt.dto.rebides;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class TeacherBean implements Serializable {

    static final long serialVersionUID = 1L;

    protected IdentificationBean identification;
    protected CareerActivitiesBean careerActivities;
    protected List<QualificationBean> qualifications = Lists.newArrayList();

    public IdentificationBean getIdentification() {
        return identification;
    }

    public void setIdentification(IdentificationBean identification) {
        this.identification = identification;
    }

    public CareerActivitiesBean getCareerActivities() {
        return careerActivities;
    }

    public void setCareerActivities(CareerActivitiesBean careerActivities) {
        this.careerActivities = careerActivities;
    }

    public List<QualificationBean> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<QualificationBean> qualifications) {
        this.qualifications = qualifications;
    }

}
