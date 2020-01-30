package csell.com.vn.csell.interfaces;

import java.util.List;

import csell.com.vn.csell.models.Project;

public interface GetProject {
    void onGetProjectDetails(Project project);

    void onDataProjects(List<Project> arrayProjects);

    void onDataProjectsFilter(List<Project> arrayProjects);
}
