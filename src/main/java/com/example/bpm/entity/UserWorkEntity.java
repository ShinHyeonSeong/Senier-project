package com.example.bpm.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

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

    public static UserWorkEntity toUserWorkEntity(WorkEntity workEntity, UserEntity userEntity) {
        UserWorkEntity userWorkEntity = new UserWorkEntity();
        userWorkEntity.setWorkIdToUserWork(workEntity);
        userWorkEntity.setUserIdToUserWork(userEntity);
        return userWorkEntity;
    }
}
