package org.fenixedu.legalpt.dto;

import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.domain.legal.settings.LegalSettings;

public class LegalSettingsBean implements IBean {

    private Integer numberOfLessonWeeks;
    private String a3esURL;

    public LegalSettingsBean(final LegalSettings instance) {
        setNumberOfLessonWeeks(instance.getNumberOfLessonWeeks());
        setA3esURL(instance.getA3esURL());

        loadDataSources();
    }

    public Integer getNumberOfLessonWeeks() {
        return numberOfLessonWeeks;
    }

    public void setNumberOfLessonWeeks(final Integer input) {
        this.numberOfLessonWeeks = input;
    }

    public String getA3esURL() {
        return a3esURL;
    }

    public void setA3esURL(final String input) {
        this.a3esURL = input;
    }

    private void loadDataSources() {
        //add logic to populate datasources here
    }

}
