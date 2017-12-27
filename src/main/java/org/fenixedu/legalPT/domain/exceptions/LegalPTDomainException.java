package org.fenixedu.legalPT.domain.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.legalPT.util.LegalPTUtil;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

public class LegalPTDomainException extends DomainException {

    private static final long serialVersionUID = 1L;

    public LegalPTDomainException(String key, String... args) {
        super(LegalPTUtil.BUNDLE, key, args);
    }

    public LegalPTDomainException(Status status, String key, String... args) {
        super(status, LegalPTUtil.BUNDLE, key, args);
    }

    public LegalPTDomainException(Throwable cause, String key, String... args) {
        super(cause, LegalPTUtil.BUNDLE, key, args);
    }

    public LegalPTDomainException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, LegalPTUtil.BUNDLE, key, args);
    }

    public static void throwWhenDeleteBlocked(Collection<String> blockers) {
        if (!blockers.isEmpty()) {
            throw new LegalPTDomainException("key.return.argument", blockers.stream().collect(Collectors.joining(", ")));
        }
    }

}
