package org.fenixedu.ulisboa.integration.sas.service.transform;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.fenixedu.ulisboa.integration.sas.dto.ScholarshipStudentOtherYearBean;
import org.fenixedu.ulisboa.integration.sas.util.SASDomainException;

public class ContractualisationScholarshipXlsTransformService extends OtherYearScholarshipXlsTransformService {

    public ContractualisationScholarshipXlsTransformService(POIFSFileSystem poifsFileSystem) {
        super(poifsFileSystem);
    }

    @Override
    public boolean checkExcelFormat(HSSFSheet sheet) throws IOException {
        final int columnsRead = Integer.valueOf(String.valueOf(sheet.getRow(0).getLastCellNum()));
        final int columnsExpected = ScholarshipStudentOtherYearBean.CONTRACTUALISATION_DOCUMENT_BI + 1;

        if (columnsRead == columnsExpected) {
            return true;
        }

        throw new SASDomainException("error.fileFormatDoesNotMatchRequest.expected",
                new String[] { String.valueOf(columnsExpected), String.valueOf(columnsRead) });
    }

    protected void readSpreadsheetRow(HSSFRow row, ScholarshipStudentOtherYearBean bean) {
        bean.setInstitutionCode(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.INSTITUTION_CODE));
        bean.setInstitutionName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.INSTITUTION_NAME));
        bean.setCandidacyNumber(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.CANDIDACY_NUMBER));
        try {
            bean.setStudentNumber(
                    Integer.parseInt(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.STUDENT_NUMBER)));
        } catch (NumberFormatException e) {
            bean.setStudentNumber(null);
        }
        bean.setStudentName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.STUDENT_NAME));
        bean.setDocumentTypeName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DOCUMENT_TYPE_NAME));
        bean.setDocumentNumber(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DOCUMENT_NUMBER));
        bean.setDegreeCode(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DEGREE_CODE));
        bean.setDegreeName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DEGREE_NAME));
        bean.setDegreeTypeName(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.DEGREE_TYPE_NAME));
        bean.setCode(getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.CODE));

        bean.setDocumentBINumber(
                getValueFromColumnMayBeNull(row, ScholarshipStudentOtherYearBean.CONTRACTUALISATION_DOCUMENT_BI));
    }

    protected void writeSpreadsheetRow(HSSFRow row, ScholarshipStudentOtherYearBean bean) {
        writeCellString(row, ScholarshipStudentOtherYearBean.INSTITUTION_CODE, bean.getInstitutionCode());
        writeCellInteger(row, ScholarshipStudentOtherYearBean.STUDENT_NUMBER, bean.getStudentNumber());
        writeCellString(row, ScholarshipStudentOtherYearBean.DEGREE_CODE, bean.getDegreeCode());
        writeCellInteger(row, ScholarshipStudentOtherYearBean.COUNT_NUMBER_OF_DEGREE_CHANGES, bean.getNumberOfDegreeChanges());
        writeCellString(row, ScholarshipStudentOtherYearBean.CURRENT_YEAR_HAS_MADE_DEGREE_CHANGE,
                booleanToString(bean.getHasMadeDegreeChangeOnCurrentYear()));
        writeCellString(row, ScholarshipStudentOtherYearBean.REGISTERED, booleanToString(bean.getEnroled()));
        writeCellLocalDate(row, ScholarshipStudentOtherYearBean.REGISTRATION_DATE,
                bean.getEnroled() ? bean.getEnrolmentDate() : null);

        writeCellString(row, ScholarshipStudentOtherYearBean.CONTRACTUALISATION_REGIME,
                bean.getEnroled() ? bean.getRegime() : null);
        writeCellBigDecimal(row, ScholarshipStudentOtherYearBean.CONTRACTUALISATION_NUMBER_OF_ECTS,
                bean.getEnroled() ? bean.getNumberOfEnrolledECTS() : null);
        writeCellBigDecimal(row, ScholarshipStudentOtherYearBean.CONTRACTUALISATION_GRATUITY,
                bean.getEnroled() ? bean.getGratuityAmount() : null);
        writeCellInteger(row, ScholarshipStudentOtherYearBean.CONTRACTUALISATION_NUMBER_OF_MONTHS_EXECUTION_YEAR,
                bean.getEnroled() ? bean.getNumberOfMonthsExecutionYear() : null);
        writeCellString(row, ScholarshipStudentOtherYearBean.CONTRACTUALISATION_FIRST_MONTH_EXECUTION_YEAR,
                bean.getEnroled() ? toMonthString(bean.getFirstMonthExecutionYear()) : null);

    }

}
