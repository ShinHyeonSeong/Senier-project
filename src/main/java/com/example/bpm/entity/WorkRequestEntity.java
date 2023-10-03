package com.example.bpm.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "work_requst")
public class WorkRequestEntity {

    @Id
    @Column(name = "work_id")
    private Long workId;
    @Column(name = "title")
    private String title;
    @Column(name = "start_day")
    private Date startDay;
    @Column(name = "end_day")
    private Date endDay;
    @Column(name = "completion")
    private int completion;

    @JoinColumn(name = "requst_user")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity userIdToWorkRequest;

    @JoinColumn(name = "detail_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DetailEntity detailIdToWorkRequest;

}
