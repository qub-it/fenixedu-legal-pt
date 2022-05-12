package org.fenixedu.ulisboa.integration.sas.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

public class SchoolLevelTypeMapping extends SchoolLevelTypeMapping_Base {

    protected SchoolLevelTypeMapping() {
        super();
    }

    public void delete() {
        setDegreeType(null);
        setBennu(null);
        super.deleteDomainObject();
    }

    public static Stream<SchoolLevelTypeMapping> findAll() {
        return Bennu.getInstance().getSchoolLevelTypeMappingSet().stream();
    }

    public static void registerEvents() {

        //The SchoolLevelTypeMapping must be deleted when a degree type is removed
        FenixFramework.getDomainModel().registerDeletionListener(DegreeType.class, new DeletionListener<DegreeType>() {

            @Override
            public void deleting(DegreeType object) {
                Optional.ofNullable(object.getSchoolLevelTypeMapping()).ifPresent(sltm -> sltm.delete());
            }
        });
    }

    public void edit(SchoolLevelType schoolLevelType, DegreeType degreeType) {
        checkPreConditions(this, schoolLevelType, degreeType);
        setSchoolLevel(schoolLevelType);
        setDegreeType(degreeType);
    }

    public static SchoolLevelTypeMapping create(SchoolLevelType schoolLevelType, DegreeType degreeType) {
        checkPreConditions(null, schoolLevelType, degreeType);
        SchoolLevelTypeMapping schoolLevelTypeMapping = new SchoolLevelTypeMapping();
        schoolLevelTypeMapping.setSchoolLevel(schoolLevelType);
        schoolLevelTypeMapping.setDegreeType(degreeType);
        schoolLevelTypeMapping.setBennu(Bennu.getInstance());
        return schoolLevelTypeMapping;
    }

    // Check if the arguments are not null, and if the degree type has no associated schoolLevel.
    // First argument can be null (for constructor case)
    public static void checkPreConditions(SchoolLevelTypeMapping schoolLevelTypeMapping, SchoolLevelType schoolLevelType,
            DegreeType degreeType) {

        //TODO localize messages
        if (degreeType == null) {
            throw new RuntimeException("Degree Type cannot be null");
        }
        if (schoolLevelType == null) {
            throw new RuntimeException("School Level cannot be null");
        }
        SchoolLevelTypeMapping degreeTypeCurrentSchoolLevelTypeMapping = degreeType.getSchoolLevelTypeMapping();
        if (degreeTypeCurrentSchoolLevelTypeMapping != null && degreeTypeCurrentSchoolLevelTypeMapping != schoolLevelTypeMapping) {
            throw new RuntimeException("Degree type already has associated school level");
        }

    }

    //Change visibility of getters
    @Override
    public DegreeType getDegreeType() {
        return super.getDegreeType();
    }

    @Override
    public SchoolLevelType getSchoolLevel() {
        return super.getSchoolLevel();
    }

    private static final List<SchoolLevelType> CTSP_SCHOOL_LEVELS = new ArrayList<>();

    private static final List<SchoolLevelType> CET_SCHOOL_LEVELS = Arrays.asList(SchoolLevelType.TECHNICAL_SPECIALIZATION);

    private static final List<SchoolLevelType> DEGREE_SCHOOL_LEVELS = Arrays.asList(SchoolLevelType.DEGREE,
            SchoolLevelType.DEGREE_PRE_BOLOGNA, SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA);

    private static final List<SchoolLevelType> MASTER_DEGREE_SCHOOL_LEVELS = Arrays.asList(SchoolLevelType.MASTER_DEGREE,
            SchoolLevelType.MASTER_DEGREE_PRE_BOLOGNA, SchoolLevelType.MASTER_DEGREE_INTEGRATED);

    private static final List<SchoolLevelType> PHD_SCHOOL_LEVELS = Arrays.asList(SchoolLevelType.DOCTORATE_DEGREE,
            SchoolLevelType.DOCTORATE_DEGREE_PRE_BOLOGNA);

    public static boolean isCTSP(final SchoolLevelType schoolLevelType) {
        return CTSP_SCHOOL_LEVELS.contains(schoolLevelType);
    }

    public static boolean isCET(final SchoolLevelType schoolLevelType) {
        return CET_SCHOOL_LEVELS.contains(schoolLevelType);
    }

    public static boolean isDegree(final SchoolLevelType schoolLevelType) {
        return DEGREE_SCHOOL_LEVELS.contains(schoolLevelType);
    }

    public static boolean isMasterDegree(final SchoolLevelType schoolLevelType) {
        return MASTER_DEGREE_SCHOOL_LEVELS.contains(schoolLevelType);
    }

    public static boolean isPhd(final SchoolLevelType schoolLevelType) {
        return PHD_SCHOOL_LEVELS.contains(schoolLevelType);
    }
}
