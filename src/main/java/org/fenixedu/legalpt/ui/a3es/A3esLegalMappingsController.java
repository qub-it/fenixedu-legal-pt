package org.fenixedu.legalpt.ui.a3es;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.domain.a3es.A3esInstance;
import org.fenixedu.legalpt.domain.a3es.mapping.A3esMappingType;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.fenixedu.legalpt.util.LegalPTUtil;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.ILegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMappingEntry;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.title.manageA3esLegalMapping",
        accessGroup = "#managers")
@RequestMapping(A3esLegalMappingsController.CONTROLLER_URL)
public class A3esLegalMappingsController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/a3es/managelegalmappings";
    public static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    private String jspPage(final String page) {
        return JSP_PATH + page;
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI, method = GET)
    public String search(Model model) {
        model.addAttribute("legalMappings", A3esInstance.getInstance().getLegalMappingsSet());
        List<ILegalMappingType> possibleTypes = A3esInstance.getInstance().getMappingTypes().stream()
                .filter(type -> LegalMapping.find(A3esInstance.getInstance(), type) == null)
                .sorted((t1, t2) -> t1.getName().getContent().compareTo(t2.getName().getContent())).collect(Collectors.toList());
        model.addAttribute("possibleLegalMappingTypes", possibleTypes);
        return jspPage(_SEARCH_URI);
    }

    private static final String SEARCH_VIEW_URI = "/search/view";
    public static final String SEARCH_VIEW_URL = CONTROLLER_URL + SEARCH_VIEW_URI;

    @RequestMapping(value = SEARCH_VIEW_URI + "/{legalMappingId}")
    public String processSearchToViewAction(@PathVariable("legalMappingId") final LegalMapping legalMapping, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(READ_URL + "/" + legalMapping.getExternalId(), model, redirectAttributes);
    }

    private static final String _DELETE_URI = "/delete";
    public static final String DELETE_URL = CONTROLLER_URL + _DELETE_URI;

    @RequestMapping(value = _DELETE_URI + "/{legalMappingId}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("legalMappingId") final LegalMapping legalMapping, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {
            deleteLegalMapping(legalMapping);
            addInfoMessage(LegalPTUtil.bundle("message.LegalMapping.removed.with.success"), model);
            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (DomainException ex) {
            addErrorMessage(LegalPTUtil.bundle(ex.getKey()), model);
        }
        return read(legalMapping, model);
    }

    @Atomic
    public void deleteLegalMapping(LegalMapping legalMapping) {
        for (LegalMappingEntry entry : legalMapping.getLegalMappingEntriesSet()) {
            legalMapping.deleteEntry(entry);
        }
        legalMapping.delete();
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "selectedType", required = true) A3esMappingType selectedType, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            LegalMapping legalMapping = createLegalMapping(selectedType);
            return redirect(READ_URL + "/" + legalMapping.getExternalId(), model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(LegalPTUtil.bundle(de.getKey()), model);
        }
        return search(model);
    }

    @Atomic
    public LegalMapping createLegalMapping(A3esMappingType type) {
        return type.createMapping(A3esInstance.getInstance());
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "/{legalMappingId}", method = RequestMethod.GET)
    public String read(@PathVariable("legalMappingId") final LegalMapping legalMapping, final Model model) {
        model.addAttribute("legalMapping", legalMapping);
        model.addAttribute("legalMappingEntries", legalMapping.getLegalMappingEntriesSet().stream()
                .sorted((e1, e2) -> e1.getMappingKey().compareTo(e2.getMappingKey())).collect(Collectors.toList()));
        List<TupleDataSourceBean> possibleLegalMappingEntryKeys = legalMapping.getPossibleKeys().stream().map(o -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(legalMapping.keyForObject(o));
            tuple.setText(legalMapping.getLocalizedNameEntryKeyI18NForObject(o).getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
        model.addAttribute("possibleLegalMappingEntryKeys", possibleLegalMappingEntryKeys);
        return jspPage(_READ_URI);
    }

    private static final String _CREATE_ENTRY_URI = "/create/entry";
    public static final String CREATE_ENTRY_URL = CONTROLLER_URL + _CREATE_ENTRY_URI;

    @RequestMapping(value = _CREATE_ENTRY_URI + "/{legalMappingId}", method = RequestMethod.POST)
    public String createEntry(@PathVariable("legalMappingId") LegalMapping legalMapping,
            @RequestParam(value = "key", required = true) String key,
            @RequestParam(value = "value", required = true) String value, Model model) {
        try {
            createLegalMappingEntry(legalMapping, key, value);
            addInfoMessage(LegalPTUtil.bundle("message.LegalMappingEntry.created.with.success"), model);
        } catch (DomainException de) {
            addErrorMessage(LegalPTUtil.bundle(de.getKey()), model);
        }
        return read(legalMapping, model);
    }

    @Atomic
    public void createLegalMappingEntry(LegalMapping legalMapping, String key, String value) {
        legalMapping.addEntry(key, value);
    }

    private static final String _DELETE_ENTRY_URI = "/delete/entry";
    public static final String DELETE_ENTRY_URL = CONTROLLER_URL + _DELETE_ENTRY_URI;

    @RequestMapping(value = _DELETE_ENTRY_URI + "/{legalMappingId}/{legalMappingEntryId}", method = RequestMethod.POST)
    public String deleteEntry(@PathVariable("legalMappingId") LegalMapping legalMapping,
            @PathVariable("legalMappingEntryId") LegalMappingEntry legalMappingEntry, Model model) {
        try {
            deleteLegalMappingEntry(legalMapping, legalMappingEntry);
            addInfoMessage(LegalPTUtil.bundle("message.LegalMappingEntry.removed.with.success"), model);
        } catch (DomainException de) {
            addErrorMessage(LegalPTUtil.bundle(de.getKey()), model);
        }
        return read(legalMapping, model);
    }

    @Atomic
    public void deleteLegalMappingEntry(LegalMapping legalMapping, LegalMappingEntry legalMappingEntry) {
        legalMapping.deleteEntry(legalMappingEntry);
    }

}
