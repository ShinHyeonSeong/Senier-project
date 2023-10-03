package com.example.bpm.entity.project.data;

import com.example.bpm.dto.project.RoleDto;
import javax.persistence.*;

import com.example.bpm.entity.project.relation.ProjectRoleEntity;
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

}