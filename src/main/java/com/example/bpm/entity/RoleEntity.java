package com.example.bpm.entity;

import com.example.bpm.dto.RoleDto;
import javax.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "role")
public class RoleEntity {
    @Id
    @Column(name = "role_id")
    private Long Id;

    @Column(name = "role_name")
    private String roleName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    private List<ProjectRoleEntity> projectRoleEntityList = new ArrayList<>();

    public static RoleEntity toRoleEntity(RoleDto roleDto) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(roleDto.getId());
        roleEntity.setRoleName(roleDto.getRoleName());
        return roleEntity;
    }
}