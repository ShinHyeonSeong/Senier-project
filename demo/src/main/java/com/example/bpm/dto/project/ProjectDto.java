package com.example.bpm.dto.project;

import com.example.bpm.entity.project.data.ProjectEntity;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long projectId;
    private String title;
    private String subtitle;
    private Date startDay;
    private Date endDay;

    public ProjectEntity toEntity(){
        ProjectEntity projectEntity = new ProjectEntity();

        projectEntity.setProjectId(projectId);
        projectEntity.setTitle(title);
        projectEntity.setSubtitle(subtitle);
        projectEntity.setStartDay(startDay);
        projectEntity.setEndDay(endDay);

        return projectEntity;
    }

    public void insertEntity(ProjectEntity projectEntity){
        this.projectId = projectEntity.getProjectId();
        this.title = projectEntity.getTitle();
        this.subtitle = projectEntity.getSubtitle();
        this.startDay = projectEntity.getStartDay();
        this.endDay = projectEntity.getEndDay();
    }
}
