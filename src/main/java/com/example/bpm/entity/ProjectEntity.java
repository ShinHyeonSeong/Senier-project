package com.example.bpm.entity;

import com.example.bpm.dto.ProjectDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
@Slf4j
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "title")
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "start_day")
    private Date startDay;

    @Column(name = "end_day")
    private Date endDay;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projectIdInRole")
    private List<ProjectRoleEntity> projectRoleEntityList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projectIdToRequest")
    private List<ProjectRequestEntity> projectRequestEntityList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projectIdToHead")
    private List<HeadEntity> HeadEntityList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projectIdToMessage")
    private List<MessageEntity> MessageEntityList = new ArrayList<>();

    public static ProjectEntity toProjectEntity(ProjectDto projectDto) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setProjectId(projectDto.getProjectId());
        projectEntity.setTitle(projectDto.getTitle());
        projectEntity.setSubtitle(projectDto.getSubtitle());
        projectEntity.setStartDay(projectDto.getStartDay());
        projectEntity.setEndDay(projectDto.getEndDay());
        return projectEntity;
    }

}
