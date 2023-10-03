package com.example.bpm.entity;

import com.example.bpm.dto.UserDto;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user_info")
@NoArgsConstructor
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


    public static UserEntity toUserEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(String.valueOf(userDto.getUuid()));
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setName(userDto.getName());
        return userEntity;

    }

    //새로운 정보의 DTO를 받아 Entity를 최신화 (update) 시키는 메서드
    public static UserEntity toUpdateuserEntity(String email, String name) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setName(name);
        return userEntity;
    }
}
