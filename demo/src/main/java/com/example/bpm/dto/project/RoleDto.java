package com.example.bpm.dto.project;

import com.example.bpm.entity.project.data.RoleEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Long id;
    private String roleName;

    public RoleEntity toEntity(){
        RoleEntity roleEntity = new RoleEntity();

        roleEntity.setId(id);
        roleEntity.setRoleName(roleName);

        return roleEntity;
    }

    public void insertEntity(RoleEntity roleEntity){
        this.id = roleEntity.getId();
        this.roleName = roleEntity.getRoleName();
    }
}
