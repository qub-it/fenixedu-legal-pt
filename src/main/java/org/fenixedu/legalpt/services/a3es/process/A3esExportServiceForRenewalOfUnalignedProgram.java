package org.fenixedu.legalpt.services.a3es.process;

public class A3esExportServiceForRenewalOfUnalignedProgram extends A3esExportService {

    @Override
    protected String getFormName() {
        return "Apresentação do pedido";
    }

    @Override
    protected String getCoursesFolderIndex() {
        return "9.4.";
    }

    @Override
    protected String getCoursesFolderName() {
        return getCoursesFolderIndex() + " Fichas de Unidade Curricular";
    }

    @Override
    protected String getTeachersFolderSectionName() {
        return "Secção 2";
    }

    @Override
    protected String getTeachersFolderIndex() {
        return "3.2.";
    }

    @Override
    protected String getTeachersFolderName() {
        return getTeachersFolderIndex() + " Fichas curriculares dos docentes do ciclo de estudos";
    }

    @Override
    protected String getTeacherId() {
        return "cvFolderId";
    }

}
