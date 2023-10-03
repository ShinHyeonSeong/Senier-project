package com.example.bpm.entity.project.data;

import com.example.bpm.dto.project.WorkDto;
import com.example.bpm.entity.project.data.work.WorkCommentEntity;
import com.example.bpm.entity.project.relation.WorkDocumentEntity;
import com.example.bpm.entity.user.relation.UserWorkEntity;
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

    @JoinColumn(name = "head_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private HeadEntity headIdToWork;

    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ProjectEntity projectIdToWork;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workIdToUserWork")
    private List<UserWorkEntity> userWorkEntityList  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workIdToWorkDocument")
    private List<WorkDocumentEntity> workDocumentEntityList  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workIdToComment")
    private List<WorkCommentEntity> workCommentEntityList  = new ArrayList<>();

}
