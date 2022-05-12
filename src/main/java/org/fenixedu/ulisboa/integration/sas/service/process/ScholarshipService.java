package org.fenixedu.ulisboa.integration.sas.service.process;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.io.domain.GenericFile;
import org.fenixedu.ulisboa.integration.sas.domain.ScholarshipReportRequest;
import org.fenixedu.ulisboa.integration.sas.service.transform.AbstractScholarshipXlsTransformService;
import org.fenixedu.ulisboa.integration.sas.service.transform.ContractualisationScholarshipXlsTransformService;
import org.fenixedu.ulisboa.integration.sas.service.transform.FirstYearScholarshipXlsTransformService;
import org.fenixedu.ulisboa.integration.sas.service.transform.OtherYearScholarshipXlsTransformService;
import org.fenixedu.ulisboa.integration.sas.util.SASDomainException;
import org.joda.time.DateTime;

public class ScholarshipService {

    static public GenericFile processScholarshipFile(final ScholarshipReportRequest request) {

        GenericFile file = request.getParameterFile();

        POIFSFileSystem poifs;
        try {
            poifs = new POIFSFileSystem(file.getStream());

            final AbstractScholarshipXlsTransformService xlsService;
            if (request.getContractualisation()) {
                xlsService = new ContractualisationScholarshipXlsTransformService(poifs);

            } else {
                xlsService = request.getFirstYearOfCycle() ? new FirstYearScholarshipXlsTransformService(
                        poifs) : new OtherYearScholarshipXlsTransformService(poifs);
            }

            xlsService.readExcelFile();

            final AbstractFillScholarshipService service;
            if (request.getContractualisation() || !request.getFirstYearOfCycle()) {
                service = new FillScholarshipServiceOtherYearService();
            } else {
                service = new FillScholarshipFirstYearService();
            }

            service.fillAcademicInfos(xlsService.getStudentLines(), request.getExecutionYear(), request.getFirstYearOfCycle());

            final HSSFWorkbook hssfWorkbook = xlsService.writeExcelFile(poifs);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                hssfWorkbook.write(outputStream);
                final byte[] content = outputStream.toByteArray();
                return request.createResultFile(getFilename(request), content);

            } catch (final Exception e) {
                throw new DomainException("error.ScholarshipService.spreadsheet.generation.failed", e);
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new DomainException("error.ScholarshipService.spreadsheet.generation.failed", e);
                }
            }

        } catch (SASDomainException e1) {
            throw e1;
        } catch (IOException e1) {
            throw new DomainException(e1.getMessage());
        }
    }

    static private String getFilename(final ScholarshipReportRequest request) {
        final Unit institutionUnit = Bennu.getInstance().getInstitutionUnit();
        final String acronym = institutionUnit.getAcronym();

        final String title = acronym + "_Bolsas_";

        final ExecutionYear executionInterval = request.getExecutionYear();
        final String period =
                executionInterval == null ? "" : executionInterval.getQualifiedName().replace("/", "-").replace(" ", "-") + "_";

        return title + period + new DateTime().toString("yyyy-MM-dd_HH-mm-ss") + "_" + request.getParameterFile().getFilename();
    }

}
