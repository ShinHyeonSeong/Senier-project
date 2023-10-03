package com.example.bpm.entity;

import com.example.bpm.dto.DetailDto;
import com.example.bpm.dto.WorkDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "work")
public class WorkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Long workId;

    @Column(name = "title")
    private String title;

    @Column(name = "discription")
    private String discription;

    @Column(name = "start_day")
    private Date startDay;

    @Column(name = "end_day")
    private Date endDay;

    @Column(name = "completion")
    private int completion;

    @JoinColumn(name = "detail_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DetailEntity detailIdToWork;

    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ProjectEntity projectIdToWork;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workIdToUserWork")
    private List<UserWorkEntity> userWorkEntityList  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workIdToWorkDocument")
    private List<WorkDocumentEntity> workDocumentEntityList  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workIdToComment")
    private List<WorkCommentEntity> workCommentEntityList  = new ArrayList<>();

    public static WorkEntity toWorkEntity(WorkDto workDto) {
        WorkEntity workEntity = new WorkEntity();
        workEntity.setWorkId(workDto.getWorkId());
        workEntity.setTitle(workDto.getTitle());
        workEntity.setDiscription(workDto.getDiscription());
        workEntity.setStartDay(workDto.getStartDay());
        workEntity.setEndDay(workDto.getEndDay());
        workEntity.setCompletion(workDto.getCompletion());
        workEntity.setDetailIdToWork(workDto.getDetailIdToWork());
        workEntity.setProjectIdToWork(workDto.getProjectIdToWork());
        return workEntity;
    }

}
