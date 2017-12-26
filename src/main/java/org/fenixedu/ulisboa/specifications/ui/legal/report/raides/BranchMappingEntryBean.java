package org.fenixedu.ulisboa.specifications.ui.legal.report.raides;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;

import com.google.common.collect.Lists;

public class BranchMappingEntryBean implements IBean {

    private CourseGroup branchKey;
    private String value;

    private List<TupleDataSourceBean> branchKeysDataSource = Lists.newArrayList();

    public BranchMappingEntryBean(final DegreeCurricularPlan dcp) {
        branchKeysDataSource.addAll(dcp.getAllCoursesGroups().stream()
                .map(c -> new TupleDataSourceBean(c.getExternalId(), c.getOneFullName())).collect(Collectors.toSet()));
        
        branchKeysDataSource.sort(TupleDataSourceBean.COMPARE_BY_TEXT);
    }

    
    /*******************
     * GETTERS & SETTERS
     * *****************
     */
    
    public CourseGroup getBranchKey() {
        return branchKey;
    }
    
    public void setBranchKey(CourseGroup branchKey) {
        this.branchKey = branchKey;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
}
