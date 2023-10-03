package com.example.bpm.entity.project.relation;

import javax.persistence.*;

import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.project.pk.ProjectRolePKEntity;
import com.example.bpm.entity.project.data.RoleEntity;
import com.example.bpm.entity.user.UserEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Slf4j
@Table(name = "project_role")
@IdClass(ProjectRolePKEntity.class)
public class ProjectRoleEntity implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private ProjectEntity projectIdInRole;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uuid", referencedColumnName = "uuid")
    private UserEntity uuidInRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role")
    private RoleEntity role;

}
