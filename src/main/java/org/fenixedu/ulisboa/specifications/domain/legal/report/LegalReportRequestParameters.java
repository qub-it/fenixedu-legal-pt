package org.fenixedu.ulisboa.specifications.domain.legal.report;

import java.lang.reflect.Type;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public abstract class LegalReportRequestParameters {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static <T extends LegalReportRequestParameters> T fromJson(Class<T> type, String json) {
        if (Strings.isNullOrEmpty(json)) {
            return null;
        }

        // deserializers
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {

            private DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);

            @Override
            public LocalDate deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
                return Strings.isNullOrEmpty(arg0.getAsString()) ? null : formatter.parseDateTime(arg0.getAsString())
                        .toLocalDate();
            }
        });
             
        gsonBuilder.registerTypeHierarchyAdapter(DomainObject.class, new JsonDeserializer<DomainObject>() {
            @Override
            public DomainObject deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
                    throws JsonParseException {
                return Strings.isNullOrEmpty(arg0.getAsString()) ? null : FenixFramework.getDomainObject(arg0.getAsString());
            }
        });

        Gson gson = gsonBuilder.create();
        return gson.fromJson(json, type);
    }

    public String toJson() {
        // serializers
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {

            @Override
            public JsonElement serialize(LocalDate arg0, Type arg1, JsonSerializationContext arg2) {
                return arg0 == null ? null : new JsonPrimitive(arg0.toString(DATE_FORMAT));
            }
        });
        
        gsonBuilder.registerTypeHierarchyAdapter(DomainObject.class, new JsonSerializer<DomainObject>() {
            @Override
            public JsonElement serialize(DomainObject arg0, Type arg1, JsonSerializationContext arg2) {
                return arg0 == null ? null : new JsonPrimitive(arg0.getExternalId());
            }
        });

        Gson gson = gsonBuilder.create();
        return gson.toJson(this);
    }
}
