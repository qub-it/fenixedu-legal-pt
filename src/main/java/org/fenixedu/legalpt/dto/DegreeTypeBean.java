package org.fenixedu.legalpt.dto;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;

import com.google.common.collect.Lists;

public class DegreeTypeBean implements IBean {

    public static final Comparator<DegreeTypeBean> COMPARE_BY_TEXT = new Comparator<DegreeTypeBean>() {

        @Override
        public int compare(final DegreeTypeBean o1, final DegreeTypeBean o2) {
            int c = o1.getText().compareTo(o2.getText());
            return c != 0 ? c : o1.getId().compareTo(o2.getId());
        }
    };

    private String id;
    private String text;
    private List<TupleDataSourceBean> degreesDataSource;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TupleDataSourceBean> getDegreesDataSource() {
        return degreesDataSource;
    }

    public void setDegreesDataSource(Set<Degree> degrees) {
        this.degreesDataSource = degrees.stream().map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText("[" + x.getCode() + "] " + x.getPresentationNameI18N().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public DegreeTypeBean() {
        this.degreesDataSource = Lists.newArrayList();
    }

    public DegreeTypeBean(DegreeType degreeType) {
        setId(degreeType.getExternalId());
        setText(degreeType.getName().getContent());
        setDegreesDataSource(degreeType.getDegreeSet());
    }

}
