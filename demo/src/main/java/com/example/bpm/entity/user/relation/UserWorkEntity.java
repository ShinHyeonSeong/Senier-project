package com.example.bpm.entity.user.relation;

import com.example.bpm.entity.user.pk.UserWorkPKEntity;
import com.example.bpm.entity.project.data.WorkEntity;
import com.example.bpm.entity.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "user_work")
@IdClass(UserWorkPKEntity.class)
public class UserWorkEntity {
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "work_id")
    private WorkEntity workIdToUserWork;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uuid")
    private UserEntity userIdToUserWork;
}
