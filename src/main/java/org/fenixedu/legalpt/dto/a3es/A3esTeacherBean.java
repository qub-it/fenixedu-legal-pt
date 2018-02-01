package org.fenixedu.legalpt.dto.a3es;

import java.util.Set;

@SuppressWarnings({ "serial" })
public class A3esTeacherBean extends A3esAbstractBean {

    private AttainedDegree attainedDegree;
    private Set<AttainedDegree> otherAttainedDegrees;
    private TeacherActivity primePublishedWork;
    private TeacherActivity primeProfessionalActivities;
    private TeacherActivity otherPublishedWork;
    private TeacherActivity otherProfessionalActivities;
    private Set<TeachingService> teachingServices;

    public AttainedDegree getAttainedDegree() {
        return attainedDegree;
    }

    public void setAttainedDegree(AttainedDegree attainedDegree) {
        this.attainedDegree = attainedDegree;
    }

    public Set<AttainedDegree> getOtherAttainedDegrees() {
        return otherAttainedDegrees;
    }

    public void setOtherAttainedDegrees(Set<AttainedDegree> otherAttainedDegrees) {
        this.otherAttainedDegrees = otherAttainedDegrees;
    }

    public TeacherActivity getPrimePublishedWork() {
        return primePublishedWork;
    }

    public void setPrimePublishedWork(TeacherActivity primePublishedWork) {
        this.primePublishedWork = primePublishedWork;
    }

    public TeacherActivity getPrimeProfessionalActivities() {
        return primeProfessionalActivities;
    }

    public void setPrimeProfessionalActivities(TeacherActivity primeProfessionalActivities) {
        this.primeProfessionalActivities = primeProfessionalActivities;
    }

    public TeacherActivity getOtherPublishedWork() {
        return otherPublishedWork;
    }

    public void setOtherPublishedWork(TeacherActivity otherPublishedWork) {
        this.otherPublishedWork = otherPublishedWork;
    }

    public TeacherActivity getOtherProfessionalActivities() {
        return otherProfessionalActivities;
    }

    public void setOtherProfessionalActivities(TeacherActivity otherProfessionalActivities) {
        this.otherProfessionalActivities = otherProfessionalActivities;
    }

    public Set<TeachingService> getTeachingServices() {
        return teachingServices;
    }

    public void setTeachingServices(Set<TeachingService> teachingServices) {
        this.teachingServices = teachingServices;
    }

    static public class AttainedDegree extends A3esAbstractBean {
    }

    static public class TeacherActivity extends A3esAbstractBean {
    }

    static public class TeachingService extends A3esAbstractBean {
    }

}
