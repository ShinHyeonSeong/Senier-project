package com.example.bpm.dto;

import com.example.bpm.entity.ProjectEntity;
import com.example.bpm.repository.ProjectRepository;
import lombok.*;

import java.text.SimpleDateFormat;
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

    public static ProjectDto toProjectDto(ProjectEntity projectEntity) {
        ProjectDto projectDto = new ProjectDto(projectEntity.getProjectId(),
                projectEntity.getTitle(),
                projectEntity.getSubtitle(),
                projectEntity.getStartDay(),
                projectEntity.getEndDay()
        );
        return projectDto;
    }
}
