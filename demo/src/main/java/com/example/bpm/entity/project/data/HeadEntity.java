package com.example.bpm.entity.project.data;

import com.example.bpm.dto.project.HeadDto;
import com.example.bpm.entity.project.request.WorkRequestEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "head")
public class HeadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "head_id")
    private Long headId;

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

    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ProjectEntity projectIdToHead;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "headIdToWork")
    private List<WorkEntity> workEntityList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "headIdToWorkRequest")
    private List<WorkRequestEntity> workRequestEntityList = new ArrayList<>();

}
