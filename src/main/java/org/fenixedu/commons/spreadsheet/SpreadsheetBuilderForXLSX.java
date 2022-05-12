package org.fenixedu.commons.spreadsheet;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fenixedu.commons.spreadsheet.converters.CellConverter;

@Deprecated
public class SpreadsheetBuilderForXLSX extends SpreadsheetBuilder {

    private Set<String> sheetNames = new LinkedHashSet<>();

    private Map<String, SheetData<?>> getSheets() {
        return (Map<String, SheetData<?>>) getField("sheets");
    }

    private Map<Class<?>, CellConverter> getConverters() {
        return (Map<Class<?>, CellConverter>) getField("converters");
    }

    private Object getField(final String name) {
        Object result = null;

        try {
            final Field field = getClass().getSuperclass().getDeclaredField(name);
            field.setAccessible(true);
            result = field.get(this);
        } catch (final Throwable t) {
        }

        return result;
    }

    @Override
    public SpreadsheetBuilder addSheet(final String name, final SheetData<?> sheet) {
        final SpreadsheetBuilder result = super.addSheet(name, sheet);

        // qubExtension, fix sheet insertion order
        if (getSheets().containsKey(name)) {
            sheetNames.add(name);
        }

        return result;
    }

    public void build(OutputStream output) throws IOException {

        DocxBuilder builder = new DocxBuilder();
        for (Entry<Class<?>, CellConverter> entry : getConverters().entrySet()) {
            builder.addConverter(entry.getKey(), entry.getValue());
        }
        builder.build(getSheets(), sheetNames, output);
    }

}
