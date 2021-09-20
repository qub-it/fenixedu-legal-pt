package org.fenixedu.legalpt.dto.a3es;

import java.util.Set;

@SuppressWarnings({ "serial" })
public class A3esTeacherBean extends A3esAbstractBean {

    private AttainedDegree attainedDegree;
    private Set<AttainedDegree> otherAttainedDegrees;
    private Set<ResearchCenter> researchCenters;
    private TeacherActivity primePublishedWork;
    private TeacherActivity primeProfessionalActivities;
    private TeacherActivity otherPublishedWork;
    private TeacherActivity otherProfessionalActivities;
    private Set<TeachingTraining> teachingTrainings;
    private Set<TeachingService> teachingServices;

    public AttainedDegree getAttainedDegree() {
        return attainedDegree;
    }

    public void setAttainedDegree(final AttainedDegree attainedDegree) {
        this.attainedDegree = attainedDegree;
    }

    public Set<AttainedDegree> getOtherAttainedDegrees() {
        return otherAttainedDegrees;
    }

    public void setOtherAttainedDegrees(final Set<AttainedDegree> otherAttainedDegrees) {
        this.otherAttainedDegrees = otherAttainedDegrees;
    }

    public Set<ResearchCenter> getResearchCenters() {
        return researchCenters;
    }

    public void setResearchCenters(final Set<ResearchCenter> researchCenters) {
        this.researchCenters = researchCenters;
    }

    public TeacherActivity getPrimePublishedWork() {
        return primePublishedWork;
    }

    public void setPrimePublishedWork(final TeacherActivity primePublishedWork) {
        this.primePublishedWork = primePublishedWork;
    }

    public TeacherActivity getPrimeProfessionalActivities() {
        return primeProfessionalActivities;
    }

    public void setPrimeProfessionalActivities(final TeacherActivity primeProfessionalActivities) {
        this.primeProfessionalActivities = primeProfessionalActivities;
    }

    public TeacherActivity getOtherPublishedWork() {
        return otherPublishedWork;
    }

    public void setOtherPublishedWork(final TeacherActivity otherPublishedWork) {
        this.otherPublishedWork = otherPublishedWork;
    }

    public TeacherActivity getOtherProfessionalActivities() {
        return otherProfessionalActivities;
    }

    public void setOtherProfessionalActivities(final TeacherActivity otherProfessionalActivities) {
        this.otherProfessionalActivities = otherProfessionalActivities;
    }

    public Set<TeachingTraining> getTeachingTrainings() {
        return teachingTrainings;
    }

    public void setTeachingTrainings(final Set<TeachingTraining> teachingTrainings) {
        this.teachingTrainings = teachingTrainings;
    }

    public Set<TeachingService> getTeachingServices() {
        return teachingServices;
    }

    public void setTeachingServices(final Set<TeachingService> teachingServices) {
        this.teachingServices = teachingServices;
    }

    static public class ResearchCenter extends A3esAbstractBean {
    }

    static public class AttainedDegree extends A3esAbstractBean {
    }

    static public class TeacherActivity extends A3esAbstractBean {
    }

    static public class TeachingTraining extends A3esAbstractBean {
    }

    static public class TeachingService extends A3esAbstractBean {
    }

}
