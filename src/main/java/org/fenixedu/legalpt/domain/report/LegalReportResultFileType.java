package org.fenixedu.legalpt.domain.report;

import java.util.Locale;

import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.util.LegalPTUtil;

import pt.ist.fenixWebFramework.rendererExtensions.util.IPresentableEnum;

public enum LegalReportResultFileType implements IPresentableEnum {

    XML {

        @Override
        public String getFileExtension() {
            return "xml";
        }

    },
    CSV {

        @Override
        public String getFileExtension() {
            return "csv";
        }

    },
    XLSX {

        @Override
        public String getFileExtension() {
            return "xlsx";
        }

    },
    XLS {

        @Override
        public String getFileExtension() {
            return "xls";
        }

    },
    ERROR {

        @Override
        public String getFileExtension() {
            return "txt";
        }
    },
    ZIP {
        @Override
        public String getFileExtension() {
            return "zip";
        }
        
    };

    protected String qualifiedName() {
        return this.getClass().getSimpleName() + "." + name();
    }

    public LocalizedString getLocalizedNameI18N() {
        return LegalPTUtil.bundleI18N(qualifiedName());
    }

    @Override
    public String getLocalizedName() {
        return getLocalizedNameI18N().getContent(I18N.getLocale());
    }

    public String getLocalizedName(final Locale locale) {
        return getLocalizedNameI18N().getContent(locale);
    }

    public abstract String getFileExtension();

}
