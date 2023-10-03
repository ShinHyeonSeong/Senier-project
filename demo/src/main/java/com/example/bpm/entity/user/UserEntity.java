package com.example.bpm.entity.user;

import com.example.bpm.dto.user.UserDto;
import javax.persistence.*;

import com.example.bpm.entity.user.relation.UserWorkEntity;
import com.example.bpm.entity.project.data.work.WorkCommentEntity;
import com.example.bpm.entity.project.request.WorkRequestEntity;
import com.example.bpm.entity.message.MessageEntity;
import com.example.bpm.entity.project.request.ProjectRequestEntity;
import com.example.bpm.entity.project.relation.ProjectRoleEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user_info")
public class UserEntity {
    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sendUUID")
    private List<ProjectRequestEntity> projectRequestEntityList1 = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "recvUUID")
    private List<ProjectRequestEntity> projectRequestEntityList2 = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "uuidInRole")
    private List<ProjectRoleEntity> projectRoleEntityList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userIdToWorkRequest")
    private List<WorkRequestEntity> workRequestEntityList  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userIdToUserWork")
    private List<UserWorkEntity> userWorkEntityList  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userIdToComment")
    private List<WorkCommentEntity> documentCommentEntities  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userIdToMessageSend")
    private List<MessageEntity> MessageEntityList1  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userIdToMessageRecv")
    private List<MessageEntity> MessageEntityList2  = new ArrayList<>();

}
