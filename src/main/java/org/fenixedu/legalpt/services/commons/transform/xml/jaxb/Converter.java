package org.fenixedu.legalpt.services.commons.transform.xml.jaxb;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

public class Converter {

    private static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static Boolean parseBoolean(final String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        return Boolean.valueOf(value);
    }

    public static String printBoolean(final Boolean value) {
        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }

    public static LocalDate parseLocalDate(final String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        return LOCAL_DATE_FORMAT.parseDateTime(value).toLocalDate();
    }
    
    public static String printLocalDate(final LocalDate date) {
        return date.toString(LOCAL_DATE_FORMAT);
    }
    
    public static Long parseLong(final String value) {
        if(Strings.isNullOrEmpty(value)) {
            return null;
        }
        
        return Long.valueOf(value);
    }
    
    public static String printLong(final Long value) {
        if(value == null) {
            return null;
        }
        
        return value.toString();
    }


    public static String convertSexo(final String value) {
        return value;
    }
    
    public static String printSexo(final String value) {
        return value;
    }

}
