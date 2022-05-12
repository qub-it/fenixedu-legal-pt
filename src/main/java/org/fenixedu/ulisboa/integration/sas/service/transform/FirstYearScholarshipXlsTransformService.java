package org.fenixedu.ulisboa.integration.sas.service.transform;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.fenixedu.ulisboa.integration.sas.dto.ScholarshipStudentFirstYearBean;
import org.fenixedu.ulisboa.integration.sas.util.SASDomainException;

public class FirstYearScholarshipXlsTransformService extends AbstractScholarshipXlsTransformService {

    static Integer FIRST_VALUE_ROW = 1;

    @Override
    public boolean checkExcelFormat(final HSSFSheet sheet) throws IOException {
        // contains cells merged?
        // return sheet.getMergedRegion(0) == null;

        final int columnsRead = Integer.valueOf(String.valueOf(sheet.getRow(0).getLastCellNum()));
        final int columnsExpected = ScholarshipStudentFirstYearBean.INGRESSION_REGIME + 1;

        if (columnsRead == columnsExpected) {
            return true;
        }

        throw new SASDomainException("error.fileFormatDoesNotMatchRequest.expected",
                new String[] { String.valueOf(columnsExpected), String.valueOf(columnsRead) });
    }

    @Override
    public void readStudentLines(HSSFSheet sheet) {
        int i = FIRST_VALUE_ROW;
        HSSFRow row;
        while ((row = sheet.getRow(i)) != null && row.getCell(0) != null) {
            ScholarshipStudentFirstYearBean scholarshipStudentBean = new ScholarshipStudentFirstYearBean();
            readSpreadsheetRow(row, scholarshipStudentBean);
            studentLines.add(scholarshipStudentBean);
            i++;
        }
    }

    private void readSpreadsheetRow(final HSSFRow row, ScholarshipStudentFirstYearBean bean) {
        bean.setInstitutionCode(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.INSTITUTION_CODE));
        bean.setInstitutionName(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.INSTITUTION_NAME));
        bean.setCandidacyNumber(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.CANDIDACY_NUMBER));
        try {
            bean.setStudentNumber(
                    Integer.parseInt(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.STUDENT_NUMBER)));
        } catch (NumberFormatException e) {
            bean.setStudentNumber(null);
        }
        bean.setStudentName(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.STUDENT_NAME));
        bean.setDocumentTypeName(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.DOCUMENT_TYPE_NAME));
        bean.setDocumentNumber(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.DOCUMENT_NUMBER));
        bean.setDegreeCode(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.DEGREE_CODE));
        bean.setDegreeName(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.DEGREE_NAME));
        bean.setDegreeTypeName(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.DEGREE_TYPE_NAME));
        bean.setCode(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.CODE));

        bean.setFiscalCode(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.FISCAL_CODE));
        bean.setDocumentBINumber(getValueFromColumnMayBeNull(row, ScholarshipStudentFirstYearBean.DOCUMENT_BI));
    }

    @Override
    public void writeExcelLines(HSSFSheet sheet) throws IOException {
        for (int i = 0; i < studentLines.size(); i++) {
            ScholarshipStudentFirstYearBean scholarshipStudentBean = (ScholarshipStudentFirstYearBean) studentLines.get(i);
            HSSFRow row = sheet.getRow(FIRST_VALUE_ROW + i);
            writeSpreadsheetRow(row, scholarshipStudentBean);
        }
    }

    private void writeSpreadsheetRow(HSSFRow row, ScholarshipStudentFirstYearBean bean) {
        writeCellString(row, ScholarshipStudentFirstYearBean.INSTITUTION_CODE, bean.getInstitutionCode());
        writeCellInteger(row, ScholarshipStudentFirstYearBean.STUDENT_NUMBER, bean.getStudentNumber());
        writeCellString(row, ScholarshipStudentFirstYearBean.DEGREE_CODE, bean.getDegreeCode());
        writeCellString(row, ScholarshipStudentFirstYearBean.REGISTERED, booleanToString(bean.getEnroled()));

        writeCellLocalDate(row, ScholarshipStudentFirstYearBean.REGISTRATION_DATE,
                bean.getEnroled() ? bean.getEnrolmentDate() : null);
        writeCellBigDecimal(row, ScholarshipStudentFirstYearBean.GRATUITY, bean.getEnroled() ? bean.getGratuityAmount() : null);
        writeCellInteger(row, ScholarshipStudentFirstYearBean.NUMBER_OF_MONTHS_EXECUTION_YEAR,
                bean.getEnroled() ? bean.getNumberOfMonthsExecutionYear() : null);
        writeCellString(row, ScholarshipStudentFirstYearBean.FIRST_MONTH_EXECUTION_YEAR,
                bean.getEnroled() ? toMonthString(bean.getFirstMonthExecutionYear()) : null);
        writeCellString(row, ScholarshipStudentFirstYearBean.OWNER_CET,
                bean.getEnroled() ? booleanToString(bean.getCetQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentFirstYearBean.OWNER_CTSP,
                bean.getEnroled() ? booleanToString(bean.getCtspQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentFirstYearBean.OWNER_BACHELOR,
                bean.getEnroled() ? booleanToString(bean.getDegreeQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentFirstYearBean.OWNER_MASTER,
                bean.getEnroled() ? booleanToString(bean.getMasterQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentFirstYearBean.OWNER_PHD,
                bean.getEnroled() ? booleanToString(bean.getPhdQualificationOwner()) : null);
        writeCellString(row, ScholarshipStudentFirstYearBean.OBSERVATIONS,
                bean.getObservations() != null ? bean.getObservations() : null);
        writeCellString(row, ScholarshipStudentFirstYearBean.REGIME, bean.getEnroled() ? bean.getRegime() : null);

        writeCellInteger(row, ScholarshipStudentFirstYearBean.NUMBER_OF_YEARS_DEGREE,
                bean.getEnroled() ? bean.getNumberOfDegreeCurricularYears() : null);
        writeCellInteger(row, ScholarshipStudentFirstYearBean.NUMBER_OF_REGISTRATIONS_SINCE_REGISTRATION_START,
                bean.getEnroled() ? bean.getCycleNumberOfEnrolmentsYears() : null);
        writeCellBigDecimal(row, ScholarshipStudentFirstYearBean.NUMBER_OF_ECTS,
                bean.getEnroled() ? bean.getNumberOfEnrolledECTS() : null);
        writeCellString(row, ScholarshipStudentFirstYearBean.INGRESSION_REGIME, bean.getIngressionRegimeCodeWithDescription());
    }

    public FirstYearScholarshipXlsTransformService(POIFSFileSystem poifsFileSystem) {
        this.studentLines = new ArrayList<>();
        this.poifsFileSystem = poifsFileSystem;
    }
}
