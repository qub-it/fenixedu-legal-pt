package org.fenixedu.commons.spreadsheet.styles.xssf;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class XCellStyle {
    public XSSFCellStyle getStyle(XSSFWorkbook book) {
        XSSFCellStyle style = book.createCellStyle();
        appendToStyle(book, style, null);
        return style;
    }

    protected abstract void appendToStyle(XSSFWorkbook book, XSSFCellStyle style, XSSFFont font);
}
