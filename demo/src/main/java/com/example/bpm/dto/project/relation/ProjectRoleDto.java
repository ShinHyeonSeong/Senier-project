package com.example.bpm.dto.project.relation;

import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.project.relation.ProjectRoleEntity;
import com.example.bpm.entity.project.data.RoleEntity;
import com.example.bpm.entity.user.UserEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRoleDto {
    private ProjectEntity projectIdToRole;
    private UserEntity uuid;
    private RoleEntity role;

    public static ProjectRoleDto toProjectRoleDto(ProjectRoleEntity projectRoleEntity){
        ProjectRoleDto projectRoleDto = new ProjectRoleDto();
        projectRoleDto.setProjectIdToRole(projectRoleEntity.getProjectIdInRole());
        projectRoleDto.setUuid(projectRoleEntity.getUuidInRole());
        projectRoleDto.setRole(projectRoleEntity.getRole());
        return projectRoleDto;
    }

    public ProjectRoleEntity toEntity(){
        ProjectRoleEntity projectRoleEntity = new ProjectRoleEntity();

        projectRoleEntity.setProjectIdInRole(projectIdToRole);
        projectRoleEntity.setUuidInRole(uuid);
        projectRoleEntity.setRole(role);

        return  projectRoleEntity;
    }

    public void insertEntity(ProjectRoleEntity projectRoleEntity){
        this.projectIdToRole = projectRoleEntity.getProjectIdInRole();
        this.uuid = projectRoleEntity.getUuidInRole();
        this.role = projectRoleEntity.getRole();
    }
}
