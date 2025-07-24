package org.fenixedu.ulisboa.integration.sas.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.student.personaldata.EducationLevelType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.integration.sas.util.SasPTUtil;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

public class EducationLevelTypeMapping extends EducationLevelTypeMapping_Base {

    private static final String TECHNICAL_SPECIALIZATION = "TECHNICAL_SPECIALIZATION";
    private static final String DEGREE = "DEGREE";
    private static final String DEGREE_PRE_BOLOGNA = "DEGREE_PRE_BOLOGNA";
    private static final String BACHELOR_DEGREE_PRE_BOLOGNA = "BACHELOR_DEGREE_PRE_BOLOGNA";
    private static final String FIRST_CYCLE_INTEGRATED_MASTER_DEGREE = "FIRST_CYCLE_INTEGRATED_MASTER_DEGREE";
    private static final String MASTER_DEGREE = "MASTER_DEGREE";
    private static final String MASTER_DEGREE_PRE_BOLOGNA = "MASTER_DEGREE_PRE_BOLOGNA";
    private static final String MASTER_DEGREE_INTEGRATED = "MASTER_DEGREE_INTEGRATED";
    private static final String DOCTORATE_DEGREE = "DOCTORATE_DEGREE";
    private static final String DOCTORATE_DEGREE_PRE_BOLOGNA = "DOCTORATE_DEGREE_PRE_BOLOGNA";

    protected EducationLevelTypeMapping() {
        super();
        super.setBennu(Bennu.getInstance());
    }

    public static EducationLevelTypeMapping create(EducationLevelType educationLevelType, DegreeType degreeType) {
        EducationLevelTypeMapping educationLevelTypeMapping = new EducationLevelTypeMapping();
        educationLevelTypeMapping.setEducationLevelType(educationLevelType);
        educationLevelTypeMapping.setDegreeType(degreeType);
        return educationLevelTypeMapping;
    }

    public void edit(EducationLevelType educationLevelType, DegreeType degreeType) {
        setEducationLevelType(educationLevelType);
        setDegreeType(degreeType);
    }

    @Override
    public void setEducationLevelType(final EducationLevelType educationLevelType) {
        if (educationLevelType == null) {
            throw new RuntimeException(SasPTUtil.bundle("error.educationLevel.cannot.be.null"));
        }
        super.setEducationLevelType(educationLevelType);
    }

    @Override
    public void setDegreeType(final DegreeType degreeType) {
        if (degreeType == null) {
            throw new RuntimeException(SasPTUtil.bundle("error.degreeType.cannot.be.null"));
        }
        if (degreeType != getDegreeType() && find(degreeType).isPresent()) {
            throw new RuntimeException(SasPTUtil.bundle("error.degreeType.already.has.associated.education.level",
                    degreeType.getName().getContent()));
        }
        super.setDegreeType(degreeType);
    }

    public static Optional<EducationLevelTypeMapping> find(DegreeType degreeType) {
        return Optional.ofNullable(degreeType).map(DegreeType::getEducationLevelTypeMapping);
    }

    public static Stream<EducationLevelTypeMapping> findAll() {
        return Bennu.getInstance().getEducationLevelTypeMappingsSet().stream();
    }

    public void delete() {
        super.setDegreeType(null);
        super.setEducationLevelType(null);
        setBennu(null);
        super.deleteDomainObject();
    }

    public static void registerEvents() {

        //The EducationLevelTypeMapping must be deleted when a degree type is removed
        FenixFramework.getDomainModel().registerDeletionListener(DegreeType.class, new DeletionListener<DegreeType>() {

            @Override
            public void deleting(DegreeType object) {
                Optional.ofNullable(object.getEducationLevelTypeMapping()).ifPresent(EducationLevelTypeMapping::delete);
            }
        });
    }

    private static final List<String> CTSP_SCHOOL_LEVELS = new ArrayList<>();
    private static final List<String> CET_SCHOOL_LEVELS = List.of(TECHNICAL_SPECIALIZATION);
    private static final List<String> DEGREE_SCHOOL_LEVELS = List.of(DEGREE, DEGREE_PRE_BOLOGNA, BACHELOR_DEGREE_PRE_BOLOGNA, FIRST_CYCLE_INTEGRATED_MASTER_DEGREE);
    private static final List<String> MASTER_DEGREE_SCHOOL_LEVELS = List.of(MASTER_DEGREE, MASTER_DEGREE_PRE_BOLOGNA, MASTER_DEGREE_INTEGRATED);
    private static final List<String> PHD_SCHOOL_LEVELS = List.of(DOCTORATE_DEGREE, DOCTORATE_DEGREE_PRE_BOLOGNA);

    public static boolean isCTSP(final EducationLevelType educationLevelType) {
        return CTSP_SCHOOL_LEVELS.contains(educationLevelType.getCode());
    }

    public static boolean isCET(final EducationLevelType educationLevelType) {
        return CET_SCHOOL_LEVELS.contains(educationLevelType.getCode());
    }

    public static boolean isDegree(final EducationLevelType educationLevelType) {
        return DEGREE_SCHOOL_LEVELS.contains(educationLevelType.getCode());
    }

    public static boolean isMasterDegree(final EducationLevelType educationLevelType) {
        return MASTER_DEGREE_SCHOOL_LEVELS.contains(educationLevelType.getCode());
    }

    public static boolean isPhd(final EducationLevelType educationLevelType) {
        return PHD_SCHOOL_LEVELS.contains(educationLevelType.getCode());
    }
}
