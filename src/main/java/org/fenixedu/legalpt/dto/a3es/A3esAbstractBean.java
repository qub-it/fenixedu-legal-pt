package org.fenixedu.legalpt.dto.a3es;

import static org.fenixedu.legalpt.services.a3es.process.A3esExportService.PT;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.util.MultiLanguageString;

import com.google.common.collect.Lists;

@SuppressWarnings({ "serial", "deprecation" })
public abstract class A3esAbstractBean implements Serializable {

    private static AtomicLong beanIdSeed = new AtomicLong(1);

    public static Comparator<A3esAbstractBean> COMPARE_BY_ID = new Comparator<A3esAbstractBean>() {

        @Override
        public int compare(A3esAbstractBean bean1, A3esAbstractBean bean2) {
            return bean1.getId().compareTo(bean2.getId());
        }

    };

    private final Long beanId;
    private List<A3esBeanField> fields = Lists.newArrayList();

    public A3esAbstractBean() {
        beanId = generateId();
    }

    public Long getId() {
        return beanId.longValue();
    }

    public String getFormattedId() {
        return String.format("%08d", beanId);
    }

    private static Long generateId() {
        return beanIdSeed.getAndIncrement();
    }

    public List<A3esBeanField> getFields() {
        return fields;
    }

    public A3esBeanField getFieldUnique(final String id) {
        return getField(id).stream().findFirst().orElse(null);
    }

    public List<A3esBeanField> getField(final String id) {
        return fields.stream().filter(i -> StringUtils.equalsIgnoreCase(i.getId(), id)).collect(Collectors.toList());
    }

    public A3esBeanField addField(final String id, final String fieldName, final Locale locale, final MultiLanguageString source,
            final int limit) {
        final A3esBeanField result = A3esBeanField.create(id, fieldName, locale, source, limit);
        getFields().add(result);
        return result;
    }

    public A3esBeanField addField(final String id, final String fieldName, final MultiLanguageString source, final int limit) {
        return addField(id, fieldName, source == null ? null : source.getContent(PT), limit);
    }

    public A3esBeanField addField(final String id, final String fieldName, final String source, final int limit) {
        final A3esBeanField result = A3esBeanField.create(id, fieldName, source, limit);
        getFields().add(result);
        return result;
    }

}
