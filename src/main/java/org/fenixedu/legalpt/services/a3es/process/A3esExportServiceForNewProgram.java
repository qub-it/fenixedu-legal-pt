package org.fenixedu.legalpt.services.a3es.process;

public class A3esExportServiceForNewProgram extends A3esExportService {

    @Override
    protected String getFormName() {
        return "Apresentação do pedido - Novo ciclo de estudos";
    }

    @Override
    protected String getCoursesFolderIndex() {
        return "4.4.";
    }

    @Override
    protected String getCoursesFolderName() {
        return getCoursesFolderIndex() + " Unidades Curriculares";
    }

    @Override
    protected String getTeachersFolderSectionName() {
        return "Secção 2";
    }

    @Override
    protected String getTeachersFolderIndex() {
        return "5.2.";
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
