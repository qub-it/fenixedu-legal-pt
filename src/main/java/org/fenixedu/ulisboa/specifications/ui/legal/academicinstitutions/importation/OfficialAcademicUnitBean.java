package org.fenixedu.ulisboa.specifications.ui.legal.academicinstitutions.importation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.SchoolUnit;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.IBean;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import pt.ist.fenixframework.Atomic;

public class OfficialAcademicUnitBean implements IBean, Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final int UNIT_CODE_COLUMN = 0;
    private static final int UNIT_NAME_COLUMN = 1;
    private static final int DEGREE_DESIGNATION_CODE_COLUMN = 2;
    private static final int DEGREE_DESIGNATION_NAME_COLUMN = 3;

    private String code;
    private String name;

    private Map<String, OfficialDegreeDesignationBean> degreeDesignationBeansMap = Maps.newHashMap();

    public OfficialAcademicUnitBean(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public void addDegreeDesignation(final OfficialDegreeDesignationBean degreeDesignationBean) {
        if (degreeDesignationBeansMap.containsKey(degreeDesignationBean.getCode())) {
            return;
        }

        this.degreeDesignationBeansMap.put(degreeDesignationBean.getCode(), degreeDesignationBean);
        degreeDesignationBean.addAcademicUnit(this);
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public List<OfficialDegreeDesignationBean> getDegreeDesignationsBeanList() {
        return Lists.newArrayList(degreeDesignationBeansMap.values());
    }

    public boolean isAcademicUnitRegistered() {
        return getExistingAcademicUnit() != null;
    }

    public Unit getExistingAcademicUnit() {
        return readAcademicUnit(getCode());
    }

    public boolean isNameDifferent() {
        return isAcademicUnitRegistered() && !getExistingAcademicUnit().getNameI18n().toLocalizedString()
                .getContent(ULisboaConstants.DEFAULT_LOCALE).equals(getName());
    }

    public boolean isDegreeDesignationsSizeDifferent() {
        return isAcademicUnitRegistered()
                && getExistingAcademicUnit().getDegreeDesignationSet().size() != getDegreeDesignationsBeanList().size();
    }

    private Unit readAcademicUnit(final String code) {
        return Unit.readAllUnits().stream().filter(u -> code.equals(u.getCode())).findAny().orElse(null);
    }

    public static void read(final List<List<String>> officialData, final List<OfficialAcademicUnitBean> academicUnitsOutput, 
            final List<OfficialDegreeDesignationBean> degreeDesignationsOutput){
        final Map<String, OfficialAcademicUnitBean> academicUnitsMap = Maps.newHashMap();
        final Map<String, OfficialDegreeDesignationBean> degreeDesigationsMap = Maps.newHashMap();

        for (final List<String> list : officialData.subList(1, officialData.size())) {
            final String unitCode = list.get(UNIT_CODE_COLUMN).trim();
            final String unitName = list.get(UNIT_NAME_COLUMN).trim();

            if (!academicUnitsMap.containsKey(unitCode)) {
                academicUnitsMap.put(unitCode, new OfficialAcademicUnitBean(unitCode, unitName));
            }

            final OfficialAcademicUnitBean officialAcademicUnitBean = academicUnitsMap.get(unitCode);

            // Process degree designations

            final String degreeCode = list.get(DEGREE_DESIGNATION_CODE_COLUMN).trim();
            final String degreeName = list.get(DEGREE_DESIGNATION_NAME_COLUMN).trim();

            if (!degreeDesigationsMap.containsKey(degreeCode)) {
                degreeDesigationsMap.put(degreeCode, new OfficialDegreeDesignationBean(degreeCode, degreeName));
            }

            officialAcademicUnitBean.addDegreeDesignation(degreeDesigationsMap.get(degreeCode));
        }
        
        academicUnitsOutput.clear();
        degreeDesignationsOutput.clear();
        
        academicUnitsOutput.addAll(academicUnitsMap.values());
        degreeDesignationsOutput.addAll(degreeDesigationsMap.values());
        
    }

    @Atomic
    public static void updateAcademicUnits(final List<OfficialAcademicUnitBean> officialAcademicUnitBeanList) {
        for (final OfficialAcademicUnitBean officialAcademicUnitBean : officialAcademicUnitBeanList) {
            if (!officialAcademicUnitBean.isAcademicUnitRegistered()) {
                SchoolUnit.createNewSchoolUnit(
                        new MultiLanguageString(ULisboaConstants.DEFAULT_LOCALE, officialAcademicUnitBean.getName()),
                        officialAcademicUnitBean.getName(), /* TODO */null, true, officialAcademicUnitBean.getCode(),
                        AcademicalInstitutionType.PUBLIC_HIGH_SCHOOL);
            } else if (officialAcademicUnitBean.isNameDifferent()) {
                officialAcademicUnitBean.getExistingAcademicUnit().edit(
                        new MultiLanguageString(ULisboaConstants.DEFAULT_LOCALE, officialAcademicUnitBean.getName()),
                        officialAcademicUnitBean.getExistingAcademicUnit().getAcronym());
            }
        }
    }
}
