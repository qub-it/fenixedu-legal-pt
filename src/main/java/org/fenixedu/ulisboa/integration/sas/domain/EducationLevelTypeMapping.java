package org.fenixedu.ulisboa.integration.sas.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.student.personaldata.EducationLevelType;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

public class EducationLevelTypeMapping extends EducationLevelTypeMapping_Base {

    protected static final String TECHNICAL_SPECIALIZATION = "TECHNICAL_SPECIALIZATION";
    protected static final String DEGREE = "DEGREE";
    protected static final String DEGREE_PRE_BOLOGNA = "DEGREE_PRE_BOLOGNA";
    protected static final String BACHELOR_DEGREE_PRE_BOLOGNA = "BACHELOR_DEGREE_PRE_BOLOGNA";
    protected static final String FIRST_CYCLE_INTEGRATED_MASTER_DEGREE = "FIRST_CYCLE_INTEGRATED_MASTER_DEGREE";
    protected static final String MASTER_DEGREE = "MASTER_DEGREE";
    protected static final String MASTER_DEGREE_PRE_BOLOGNA = "MASTER_DEGREE_PRE_BOLOGNA";
    protected static final String MASTER_DEGREE_INTEGRATED = "MASTER_DEGREE_INTEGRATED";
    protected static final String DOCTORATE_DEGREE = "DOCTORATE_DEGREE";
    protected static final String DOCTORATE_DEGREE_PRE_BOLOGNA = "DOCTORATE_DEGREE_PRE_BOLOGNA";

    protected EducationLevelTypeMapping() {
        super();
    }

    public void delete() {
        setDegreeType(null);
        setEducationLevelType(null);
        setBennu(null);
        super.deleteDomainObject();
    }

    public static Stream<EducationLevelTypeMapping> findAll() {
        return Bennu.getInstance().getEducationLevelTypeMappingSet().stream();
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

    public void edit(EducationLevelType educationLevelType, DegreeType degreeType) {
        checkPreConditions(this, educationLevelType, degreeType);
        setEducationLevelType(educationLevelType);
        setDegreeType(degreeType);
    }

    public static EducationLevelTypeMapping create(EducationLevelType educationLevelType, DegreeType degreeType) {
        checkPreConditions(null, educationLevelType, degreeType);
        EducationLevelTypeMapping educationLevelTypeMapping = new EducationLevelTypeMapping();
        educationLevelTypeMapping.setEducationLevelType(educationLevelType);
        educationLevelTypeMapping.setDegreeType(degreeType);
        educationLevelTypeMapping.setBennu(Bennu.getInstance());
        return educationLevelTypeMapping;
    }

    // Check if the arguments are not null, and if the degree type has no associated educationlevel.
    // First argument can be null (for constructor case)
    public static void checkPreConditions(EducationLevelTypeMapping educationLevelTypeMapping, EducationLevelType educationLevelType,
            DegreeType degreeType) {

        //TODO localize messages
        if (degreeType == null) {
            throw new RuntimeException("Degree Type cannot be null");
        }
        if (educationLevelType == null) {
            throw new RuntimeException("Education Level cannot be null");
        }
        EducationLevelTypeMapping degreeTypeCurrentEducationLevelTypeMapping = degreeType.getEducationLevelTypeMapping();
        if (degreeTypeCurrentEducationLevelTypeMapping != null
                && degreeTypeCurrentEducationLevelTypeMapping != educationLevelTypeMapping) {
            throw new RuntimeException("Degree type already has associated education level");
        }

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
