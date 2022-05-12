package org.fenixedu.commons.spreadsheet.converters.xssf;

import org.fenixedu.commons.spreadsheet.converters.CellConverter;
import org.joda.time.LocalDate;

/**
 * Needed since DocxBuilder was removed from fenixedu-commons and our version doesn't deal with XCellDateFormat, for performance
 * purposes
 */
public class LocalDateCellConverter implements CellConverter {

    @Override
    public Object convert(Object source) {
        return (source != null) ? ((LocalDate) source).toDateTimeAtStartOfDay().toString("yyyy-MM-dd") : null;
    }

}
