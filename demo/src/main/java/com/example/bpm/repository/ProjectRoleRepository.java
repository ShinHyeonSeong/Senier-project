package com.example.bpm.repository;

import com.example.bpm.entity.project.relation.ProjectRoleEntity;
import com.example.bpm.entity.project.pk.ProjectRolePKEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ProjectRoleRepository extends JpaRepository<ProjectRoleEntity, ProjectRolePKEntity> {

    @Query(value = "select * from project_role where uuid = :uuid", nativeQuery = true)
    List<ProjectRoleEntity> findProjectRoleByUser(String uuid);

    @Query(value = "select * from project_role where project_id = :id", nativeQuery = true)
    List<ProjectRoleEntity> findProjectRoleByProject(Long id);

    //public List<ProjectRoleEntity> findAllByUuidInRole(String uuid);

    @Query(value = "insert into project_role(project_id, uuid, role)" +
            "values (?,?,?)", nativeQuery = true)
    ProjectRoleEntity insertToRoleEntity(Long projectId, String recvUUID, Long aLong);

    ProjectRoleEntity findByProjectIdInRole_ProjectIdAndUuidInRole_Uuid(Long id, String uuid);

    ProjectRoleEntity findByProjectIdInRole_ProjectIdAndRole(Long id, Long role);

    public List<ProjectRoleEntity> findAllByProjectIdInRole_ProjectId(Long projectId);

    @Transactional
    public void deleteAllByProjectIdInRole_ProjectId(Long projectId);

    @Transactional
    public void deleteAllByProjectIdInRole_ProjectIdAndUuidInRole_Uuid(Long id, String uuid);
}
