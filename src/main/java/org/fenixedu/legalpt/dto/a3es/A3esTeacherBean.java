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
    private Set<OtherTeachingService> otherTeachingServices;

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

    public Set<TeachingService> getTeachingServices() {
        return teachingServices;
    }

    public void setTeachingServices(final Set<TeachingService> teachingServices) {
        this.teachingServices = teachingServices;
    }

    public Set<OtherTeachingService> getOtherTeachingServices() {
        return otherTeachingServices;
    }

    public void setOtherTeachingServices(final Set<OtherTeachingService> otherTeachingServices) {
        this.otherTeachingServices = otherTeachingServices;
    }

    static public class AttainedDegree extends A3esAbstractBean {
    }

    static public class TeacherActivity extends A3esAbstractBean {
    }

    static public class TeachingService extends A3esAbstractBean {
    }

    static public class OtherTeachingService extends A3esAbstractBean {
    }

}
