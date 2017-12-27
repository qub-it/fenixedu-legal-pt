package org.fenixedu.ulisboa.specifications.ui.legal.academicinstitutions.importation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.fenixedu.bennu.IBean;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class OfficialDegreeDesignationBean implements IBean, Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String name;

    private Map<String, OfficialAcademicUnitBean> academicUnitBeansMap = Maps.newHashMap();

    public OfficialDegreeDesignationBean(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public void addAcademicUnit(final OfficialAcademicUnitBean bean) {
        if (academicUnitBeansMap.containsKey(bean.getCode())) {
            return;
        }

        this.academicUnitBeansMap.put(bean.getCode(), bean);
    }

    public List<OfficialAcademicUnitBean> getAcademicUnits() {
        return Lists.newArrayList(academicUnitBeansMap.values());
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
