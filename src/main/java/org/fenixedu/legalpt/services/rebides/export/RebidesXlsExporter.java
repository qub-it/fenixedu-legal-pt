package org.fenixedu.legalpt.services.rebides.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.legalpt.dto.rebides.CareerActivitiesBean;
import org.fenixedu.legalpt.dto.rebides.IdentificationBean;
import org.fenixedu.legalpt.dto.rebides.QualificationBean;
import org.fenixedu.legalpt.dto.rebides.RebidesBean;
import org.fenixedu.legalpt.dto.rebides.TeacherBean;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFile;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFileType;

public class RebidesXlsExporter {

    private static class QualificationTeacher {
        private TeacherBean teacherBean;
        private QualificationBean qualificationBean;

        public QualificationTeacher(TeacherBean teacherBean, QualificationBean qualificationBean) {
            this.teacherBean = teacherBean;
            this.qualificationBean = qualificationBean;
        }

    }

    public static LegalReportResultFile write(final LegalReportRequest reportRequest, final RebidesBean rebides) {

        ArrayList<QualificationTeacher> qualificationTeacherList = new ArrayList<QualificationTeacher>();
        for (TeacherBean teacherBean : rebides.getTeachers()) {
            for (QualificationBean qualificationBean : teacherBean.getQualifications()) {
                QualificationTeacher qualificationTeacher = new QualificationTeacher(teacherBean, qualificationBean);
                qualificationTeacherList.add(qualificationTeacher);
            }
        }

        final SheetData<TeacherBean> identificationData = new SheetData<TeacherBean>(rebides.getTeachers()) {
            @Override
            protected void makeLine(final TeacherBean teacherBean) {
                IdentificationBean bean = teacherBean.getIdentification();
                addCell("Nome", bean.getName());
                addCell("NumeroID", bean.getDocumentIdNumber());
                addCell("TipoID", bean.getDocumentIdType());
                addCell("OutroTipoID", bean.getOtherIdDocumentType());
                addCell("DataNascimento", bean.getDateOfBirth());
                addCell("Sexo", bean.getGender());
                addCell("PaisDeNacionalidade", bean.getNationalityCountry());
                addCell("OutroPaisDeNacionalidade", bean.getOtherNationalityCountry());
            }
        };

        final SheetData<TeacherBean> careerActivitiesData = new SheetData<TeacherBean>(rebides.getTeachers()) {
            @Override
            protected void makeLine(final TeacherBean teacherBean) {
                CareerActivitiesBean bean = teacherBean.getCareerActivities();
                addCell("Nome", teacherBean.getIdentification().getName());
                addCell("NumeroID", teacherBean.getIdentification().getDocumentIdNumber());
                addCell("Departamento", bean.getDepartment());
                addCell("OutroDepartamento", bean.getOtherDepartment());
                addCell("Categoria", bean.getCategory());
                addCell("Convidado", bean.getGuest());
                addCell("Equiparado", bean.getSchoolStaff());
                addCell("ProvasAptidao", bean.getAptitudeTests());
                addCell("ProvasAgregacao", bean.getAggregationTests());
                addCell("ProvasEspecialista", bean.getExpertTests());
                addCell("RegimePrestacaoServico", bean.getServiceRegime());
                addCell("RegimeParcial", bean.getPartialRegime());
                addCell("RegimeVinculacao", bean.getBindingRegime());
                addCell("Indice", bean.getContractWageLevel());
                addCell("PercentagemInvestigacao", bean.getResearchPercentage());
                addCell("Docencia", bean.getTeaching());
                addCell("HorasLetivas", bean.getTeachingHoursPercentage());
                addCell("UnidadeHL", bean.getTeachingHoursUnit());
                addCell("ActReitor", bean.getRootUnitPresident());
                addCell("ActViceReitor", bean.getRootUnitVicePresident());
                addCell("ActDiretor", bean.getUnitPresident());
                addCell("ActCoordena", bean.getUnitCoordinator());
                addCell("ActConsGeral", bean.getCoordenatorGeneralCouncil());
                addCell("ActConsGest", bean.getManagementCouncilActivities());
                addCell("ActConsCient", bean.getScientificCouncilActivities());
                addCell("ActConsPedag", bean.getPedagogicCouncilActivities());
                addCell("ActFormacao", bean.getCoachingOrResearchProjectActivities());
                addCell("ActOutra", bean.getOtherActivity());
                addCell("ActOutraDescr", bean.getOtherActivityDescription());
            }
        };

        final SheetData<QualificationTeacher> qualificationsData = new SheetData<QualificationTeacher>(qualificationTeacherList) {
            @Override
            protected void makeLine(final QualificationTeacher qualificationTeacher) {

                TeacherBean teacherBean = qualificationTeacher.teacherBean;
                addCell("Nome", teacherBean.getIdentification().getName());
                addCell("NumeroID", teacherBean.getIdentification().getDocumentIdNumber());

                QualificationBean bean = qualificationTeacher.qualificationBean;
                addCell("NivelFormacao", bean.getSchoolLevel());
                addCell("TipoNivelFormacao", bean.getSchoolLevelOrigin());
                addCell("Estabelecimento", bean.getInstituition());
                addCell("OutroEstabelecimento", bean.getOtherInstituition());
                addCell("Curso", bean.getDegree());
                addCell("OutroCurso", bean.getOtherDegree());
                addCell("Especialidade", bean.getExpertiseArea());
                addCell("OutraEspecialidade", bean.getOtherExpertiseArea());
                addCell("AreaCientifica", bean.getScientificArea());

            }
        };

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            final SpreadsheetBuilder spreadsheetBuilder = new SpreadsheetBuilder();

            spreadsheetBuilder.addSheet("Situação Carreira Atividades", careerActivitiesData);
            spreadsheetBuilder.addSheet("Habilitacoes", qualificationsData);
            spreadsheetBuilder.addSheet("Docentes", identificationData);

            spreadsheetBuilder.build(WorkbookExportFormat.EXCEL, outputStream);
            final byte[] content = outputStream.toByteArray();

            return new LegalReportResultFile(reportRequest, LegalReportResultFileType.XLSX, content);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new DomainException("error.XlsxExporter.spreadsheet.generation.failed", e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new DomainException("error.XlsxExporter.spreadsheet.generation.failed", e);
            }
        }

    }

}
