package org.fenixedu.legalpt.domain.a3es;

import java.util.stream.Stream;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.legalpt.services.a3es.process.A3esExportService;
import org.fenixedu.legalpt.services.a3es.process.A3esExportServiceForEvaluationOfActiveProgram;
import org.fenixedu.legalpt.services.a3es.process.A3esExportServiceForRenewalOfUnalignedProgram;
import org.fenixedu.legalpt.util.LegalPTUtil;

import com.google.common.collect.Lists;

public enum A3esProcessType {

    EVALUATION_OF_NEW_PROGRAM("PAPNCE") {

        @Override
        public A3esExportService getExportService() {
            return null;
        }
    },

    EVALUATION_OF_ACTIVE_PROGRAM("ACEF") {

        @Override
        public A3esExportService getExportService() {
            return new A3esExportServiceForEvaluationOfActiveProgram();
        }
    },

    RENEWAL_OF_UNALIGNED_PROGRAM("PERA") {

        @Override
        public A3esExportService getExportService() {
            return new A3esExportServiceForRenewalOfUnalignedProgram();
        }
    }

    ;

    private String code;

    private A3esProcessType(final String code) {
        this.code = code;
    }

    public LocalizedString getLocalizedName() {
        return LegalPTUtil.bundleI18N(A3esProcessType.class.getName() + "." + name());
    }

    public String getCode() {
        return code;
    }

    abstract public A3esExportService getExportService();

    static public Stream<A3esProcessType> getSupportedTypes() {
        return Lists.newArrayList(A3esProcessType.values()).stream().filter(x -> x.getExportService() != null);
    }

}
