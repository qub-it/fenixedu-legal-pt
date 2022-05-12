package org.fenixedu.commons.spreadsheet;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.spreadsheet.SheetData.Cell;
import org.fenixedu.commons.spreadsheet.converters.CellConverter;
import org.fenixedu.commons.spreadsheet.converters.LocalizedStringCellConverter;
import org.fenixedu.commons.spreadsheet.converters.excel.BigDecimalCellConverter;
import org.fenixedu.commons.spreadsheet.converters.excel.IntegerCellConverter;
import org.fenixedu.commons.spreadsheet.converters.xssf.DateTimeCellConverter;
import org.fenixedu.commons.spreadsheet.converters.xssf.LocalDateCellConverter;
import org.fenixedu.commons.spreadsheet.converters.xssf.MultiLanguageStringCellConverter;
import org.fenixedu.commons.spreadsheet.converters.xssf.YearMonthDayCellConverter;
import org.fenixedu.commons.spreadsheet.styles.xssf.XCellAlignment;
import org.fenixedu.commons.spreadsheet.styles.xssf.XCellBorder;
import org.fenixedu.commons.spreadsheet.styles.xssf.XCellFillForegroundColor;
import org.fenixedu.commons.spreadsheet.styles.xssf.XCellFillPattern;
import org.fenixedu.commons.spreadsheet.styles.xssf.XCellStyle;
import org.fenixedu.commons.spreadsheet.styles.xssf.XCellVerticalAlignment;
import org.fenixedu.commons.spreadsheet.styles.xssf.XCellWrapText;
import org.fenixedu.commons.spreadsheet.styles.xssf.XComposedCellStyle;
import org.fenixedu.commons.spreadsheet.styles.xssf.XFontColor;
import org.fenixedu.commons.spreadsheet.styles.xssf.XFontHeight;
import org.fenixedu.commons.spreadsheet.styles.xssf.XFontWeight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;

@Deprecated
class DocxBuilder extends AbstractSheetBuilder {
    static Map<Class<?>, CellConverter> BASE_CONVERTERS;

    static {
        // TODO: grow this list to all common basic types.
        BASE_CONVERTERS = new HashMap<Class<?>, CellConverter>();
        BASE_CONVERTERS.put(Integer.class, new IntegerCellConverter());
        BASE_CONVERTERS.put(DateTime.class, new DateTimeCellConverter());
        BASE_CONVERTERS.put(YearMonthDay.class, new YearMonthDayCellConverter());
        BASE_CONVERTERS.put(LocalDate.class, new LocalDateCellConverter());
        BASE_CONVERTERS.put(BigDecimal.class, new BigDecimalCellConverter());
        BASE_CONVERTERS.put(MultiLanguageStringCellConverter.class, new MultiLanguageStringCellConverter());
        BASE_CONVERTERS.put(LocalizedString.class, new LocalizedStringCellConverter());
    }

    private static XCellStyle HEADER_STYLE = new XComposedCellStyle() {
        {
            IndexedColors black = IndexedColors.BLACK;
            IndexedColors gray = IndexedColors.GREY_25_PERCENT;

            merge(new XFontColor(black));
            merge(new XFontWeight(XSSFFont.BOLDWEIGHT_BOLD));
            merge(new XFontHeight((short) 8));
            merge(new XCellAlignment(XSSFCellStyle.ALIGN_CENTER));
            merge(new XCellFillForegroundColor(gray));
            merge(new XCellFillPattern(XSSFCellStyle.SOLID_FOREGROUND));
            merge(new XCellBorder(XSSFCellStyle.BORDER_THIN));
            merge(new XCellVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER));
            merge(new XCellWrapText(true));
        }
    };

    {
        converters.putAll(BASE_CONVERTERS);
    }

    private XCellStyle headerStyle = HEADER_STYLE;

    int usefulAreaStart;

    int usefulAreaEnd;

    protected void setValue(XSSFWorkbook book, XSSFCell cell, Object value, short span) {
        setValue(book, cell, value, span, null);
    }

    private void setValue(XSSFWorkbook book, XSSFCell cell, Object value, short span, XSSFCellStyle style) {
        if (value != null) {
            Object content = convert(value);
            if (content instanceof Boolean) {
                cell.setCellValue((Boolean) content);
            } else if (content instanceof Double) {
                cell.setCellValue((Double) content);
            } else if (content instanceof String) {
                cell.setCellValue((String) content);
            } else if (content instanceof GregorianCalendar) {
                cell.setCellValue((GregorianCalendar) content);
            } else if (content instanceof Date) {
                cell.setCellValue((Date) content);
            } else if (content instanceof RichTextString) {
                cell.setCellValue((RichTextString) content);
            } else if (content instanceof Formula) {
                // cell.setCellFormula(((Formula) content).getFormula(cell,
                // usefulAreaStart, usefulAreaEnd));
            } else {
                cell.setCellValue(content.toString());
            }
        } else {
            cell.setCellValue((String) null);
        }
        if (span > 1) {
            CellRangeAddress region = new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getColumnIndex() + span - 1);
            cell.getSheet().addMergedRegion(region);
        }
        cell.setCellStyle(style);
    }

    public void build(Map<String, SheetData<?>> sheets, final Set<String> sheetNames, OutputStream output) throws IOException {
        try {
            XSSFWorkbook book = new XSSFWorkbook();
            final XSSFCellStyle xssfHeaderStyle = headerStyle.getStyle(book);

            for (final String sheetName : sheetNames) {
                final XSSFSheet sheet = book.createSheet(sheetName);
                int rownum = 0;
                int colnum = 0;

                // qubExtension, fix sheet insertion order
                SheetData<?> data = sheets.get(sheetName);
                if (!data.headers.get(0).isEmpty()) {

                    for (List<Cell> headerRow : data.headers) {
                        colnum = 0;
                        final XSSFRow row = sheet.createRow(rownum++);
                        for (Cell cell : headerRow) {
                            setValue(book, row.createCell(colnum++), cell.value, cell.span, xssfHeaderStyle);
                            colnum = colnum + cell.span - 1;
                        }
                    }
                }
                usefulAreaStart = rownum;
                for (final List<Cell> line : data.matrix) {
                    colnum = 0;
                    final XSSFRow row = sheet.createRow(rownum++);
                    for (Cell cell : line) {
                        setValue(book, row.createCell(colnum++), cell.value, cell.span);
                        colnum = colnum + cell.span - 1;
                    }
                }
                usefulAreaEnd = rownum - 1;
                if (data.hasFooter()) {
                    colnum = 0;
                    final XSSFRow row = sheet.createRow(rownum++);
                    for (Cell cell : data.footer) {
                        setValue(book, row.createCell(colnum++), cell.value, cell.span);
                        colnum = colnum + cell.span - 1;
                    }
                }
            }
            book.write(output);
        } finally {
            output.flush();
            output.close();
        }
    }

}
