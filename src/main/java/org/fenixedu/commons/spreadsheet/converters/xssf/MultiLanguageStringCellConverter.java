package org.fenixedu.commons.spreadsheet.converters.xssf;

import java.util.Locale;

import org.fenixedu.commons.spreadsheet.converters.CellConverter;

/**
 * Needed since MLS was deprecated and this converter was removed from fenixedu-commons
 */
public class MultiLanguageStringCellConverter implements CellConverter {

    private Locale locale = null;

    public MultiLanguageStringCellConverter() {
    }

    public MultiLanguageStringCellConverter(final Locale locale) {
        this.locale = locale;
    }

    @Override
    public Object convert(final Object source) {
        throw new UnsupportedOperationException(
                "TODO - MultiLanguageStringCellConverter transform in LocalizedStringCellConverter?!");
    }

}
