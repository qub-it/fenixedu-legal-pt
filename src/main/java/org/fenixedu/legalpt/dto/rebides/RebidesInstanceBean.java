package org.fenixedu.legalpt.dto.rebides;

import org.fenixedu.bennu.IBean;
import org.fenixedu.legalpt.domain.rebides.RebidesInstance;

public class RebidesInstanceBean implements IBean {

    private String institutionCode;
    private String interlocutorName;
    private String interlocutorEmail;
    private String interlocutorPhone;
    private String passwordToZip;

    public RebidesInstanceBean(final RebidesInstance instance) {
        setInstitutionCode(instance.getInstitutionCode());
        setInterlocutorName(instance.getInterlocutorName());
        setInterlocutorEmail(instance.getInterlocutorEmail());
        setInterlocutorPhone(instance.getInterlocutorPhone());
        setPasswordToZip(instance.getPasswordToZip());

        loadDataSources();
    }

    private void loadDataSources() {
        //add logic to populate datasources here

    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getInterlocutorName() {
        return interlocutorName;
    }

    public void setInterlocutorName(String interlocutorName) {
        this.interlocutorName = interlocutorName;
    }

    public String getInterlocutorEmail() {
        return interlocutorEmail;
    }

    public void setInterlocutorEmail(String interlocutorEmail) {
        this.interlocutorEmail = interlocutorEmail;
    }

    public String getInterlocutorPhone() {
        return interlocutorPhone;
    }

    public void setInterlocutorPhone(String interlocutorPhone) {
        this.interlocutorPhone = interlocutorPhone;
    }

    public String getPasswordToZip() {
        return passwordToZip;
    }

    public void setPasswordToZip(String passwordToZip) {
        this.passwordToZip = passwordToZip;
    }

}
