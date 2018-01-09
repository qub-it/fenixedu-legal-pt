package org.fenixedu.legalpt.dto.rebides;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class RebidesBean implements Serializable {

    static final long serialVersionUID = 1L;

    protected ExtractionInfoBean extractionInfo;
    protected List<TeacherBean> teachers = Lists.newArrayList();

    public ExtractionInfoBean getExtractionInfo() {
        return extractionInfo;
    }

    public void setExtractionInfo(ExtractionInfoBean extractionĨnfo) {
        this.extractionInfo = extractionĨnfo;
    }

    public List<TeacherBean> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherBean> teachers) {
        this.teachers = teachers;
    }

}
