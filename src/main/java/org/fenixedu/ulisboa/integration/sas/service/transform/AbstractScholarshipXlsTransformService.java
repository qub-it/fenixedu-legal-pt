package org.fenixedu.ulisboa.integration.sas.service.transform;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.ulisboa.integration.sas.dto.AbstractScholarshipStudentBean;
import org.joda.time.LocalDate;

import static com.qubit.terra.framework.tools.excel.ExcelUtil.createCellWithValue;

public abstract class AbstractScholarshipXlsTransformService {

    private static final String SHEET_NAME = "Dados Academicos";

    private static final String DATE_FORMAT = "dd-MM-yyyy";

    protected List<AbstractScholarshipStudentBean> studentLines;

    protected POIFSFileSystem poifsFileSystem;

    protected AbstractScholarshipStudentBean scholarshipStudentBean;

    protected HSSFWorkbook wb;

    public void readExcelFile() throws IOException {
        this.wb = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet sheet = wb.getSheet(SHEET_NAME);

        checkExcelFormat(sheet);
        readStudentLines(sheet);
    }

    public abstract boolean checkExcelFormat(HSSFSheet sheet) throws IOException;

    public abstract void readStudentLines(HSSFSheet sheet) throws IOException;

    public HSSFWorkbook writeExcelFile(POIFSFileSystem poifsFileSystem) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook(poifsFileSystem);
        HSSFSheet sheet = wb.getSheet(SHEET_NAME);

        writeExcelLines(sheet);

        return wb;
    }

    public abstract void writeExcelLines(HSSFSheet sheet) throws IOException;

    public List<AbstractScholarshipStudentBean> getStudentLines() {
        return studentLines;
    }

    public void setStudentLines(List<AbstractScholarshipStudentBean> studentLines) {
        this.studentLines = studentLines;
    }

    public POIFSFileSystem getPoifsFileSystem() {
        return poifsFileSystem;
    }

    public void setPoifsFileSystem(POIFSFileSystem poifsFileSystem) {
        this.poifsFileSystem = poifsFileSystem;
    }

    public AbstractScholarshipStudentBean getScholarshipStudentBean() {
        return scholarshipStudentBean;
    }

    public void setScholarshipStudentBean(AbstractScholarshipStudentBean scholarshipStudentBean) {
        this.scholarshipStudentBean = scholarshipStudentBean;
    }

    /** Methods for Excel manipulation **/
    public String getValueFromColumnMayBeNull(HSSFRow row, int i) {
        HSSFCell cell = row.getCell((short) i);
        if (cell == null) {
            return StringUtils.EMPTY;
        }
        final String valueFromColumn = getValueFromColumn(row, i);
        return valueFromColumn == null ? StringUtils.EMPTY : valueFromColumn.trim();
    }

    private String getValueFromColumn(HSSFRow row, int i) {
        // check if it's necessary to evaluate the cell
        if (row.getCell((short) i).getCellType() == CellType.FORMULA) {
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(row.getCell((short) i));
            if (cellValue != null) {
                switch (cellValue.getCellType()) {
                    case BOOLEAN:
                        return String.valueOf(cellValue.getBooleanValue());
                    case NUMERIC:
                        return String.valueOf(cellValue.getNumberValue());
                    default:
                        return cellValue.getStringValue();
                }
            }
        }

        try {
            return Integer.toString(new Double(row.getCell((short) i).getNumericCellValue()).intValue());
        } catch (NumberFormatException e) {
            return row.getCell((short) i).getStringCellValue();
        } catch (IllegalStateException e) {
            return row.getCell((short) i).getStringCellValue();
        }
    }

    public void writeCellBigDecimal(HSSFRow row, int column, BigDecimal value) {
        createNumberCell(row, column, value);
    }

    public void writeCellInteger(HSSFRow row, int column, Integer value) {
        createNumberCell(row, column, value);
    }

    private static void createNumberCell(HSSFRow row, int column, Number value) {
        if (value != null) {
            createCellWithValue(row, column, value);
        } else {
            createCellWithValue(row, column, (Number) null);
        }
    }

    public void writeCellBoolean(HSSFRow row, int column, Boolean value) {
        final String toWrite;
        if (value == null) {
            toWrite = null;
        } else {
            final String key = value.booleanValue() ? "label.yes" : "label.no";

            toWrite = BundleUtil.getString("resources/SasResources", CoreConfiguration.getConfiguration().defaultLocale(), key);
        }

        writeCellString(row, column, toWrite);
    }

    public void writeCellString(HSSFRow row, int column, String value) {
        createCellWithValue(row, column, value);
    }

    public void writeCellLocalDate(HSSFRow row, int column, LocalDate date) {
        writeCellDate(row, column, date != null ? date.toDateMidnight().toDate() : null, DATE_FORMAT);
    }

    private void writeCellDate(HSSFRow row, int column, Date date, String dateFormat) {
        if (date != null) {
            Cell cell = createCellWithValue(row, column, date);
            HSSFWorkbook workbook = row.getSheet().getWorkbook();
            HSSFCreationHelper createHelper = workbook.getCreationHelper();
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(dateFormat));
            cell.setCellStyle(cellStyle);
        } else {
            createCellWithValue(row, column, (Date) null);
        }
    }

    protected String booleanToString(Boolean value) {
        if (value == null) {
            value = false;
        }
        //TODO refactor to use localized labels instead of hardcoded strings
        return value ? "Sim" : "Não";
    }

    protected String toMonthString(int monthOfYear) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, monthOfYear - 1);

        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.forLanguageTag("pt-PT"));
    }
}
