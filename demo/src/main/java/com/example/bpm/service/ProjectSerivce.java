package com.example.bpm.service;

import com.example.bpm.dto.project.HeadDto;
import com.example.bpm.dto.project.ProjectDto;
import com.example.bpm.dto.project.WorkDto;
import com.example.bpm.dto.project.request.ProjectRequestDto;
import com.example.bpm.dto.project.relation.ProjectRoleDto;
import com.example.bpm.dto.user.UserDto;
import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.project.request.ProjectRequestEntity;
import com.example.bpm.entity.project.relation.ProjectRoleEntity;
import com.example.bpm.entity.project.data.RoleEntity;
import com.example.bpm.entity.user.UserEntity;
import com.example.bpm.repository.*;
import com.example.bpm.service.Logic.dateLogic.DateManager;
import com.google.cloud.storage.Acl;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@NoArgsConstructor(force = true)
public class ProjectSerivce {
    @Autowired
    final private ProjectRequestRepository projectRequestRepository;
    @Autowired
    final private ProjectRepository projectRepository;
    @Autowired
    final private ProjectRoleRepository projectRoleRepository;
    @Autowired
    final private RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;

    DateManager dateManager = new DateManager();

    //////////////////////////////////////////////////////////////////
    // project invite
    //////////////////////////////////////////////////////////////////

    @Transactional
    public void sendInvite(String sendUUID, String recvUUID, Long projectId) {
        if (sendUUID != null && recvUUID != null) {
            projectRequestRepository.addProjectRequest(sendUUID, recvUUID, projectId);
        }
    }

    @Transactional
    public List<ProjectRequestDto> recvInvite(String recvUUID){
        List<ProjectRequestDto> projectRequestDtoList = new ArrayList<>();
        List<ProjectRequestEntity> projectRequestEntityList = projectRequestRepository.findProjectRequest(recvUUID);

        for (ProjectRequestEntity projectRequestEntity: projectRequestEntityList) {
            ProjectRequestDto projectRequestDto = new ProjectRequestDto();

            projectRequestDto.insertEntity(projectRequestEntity);

            projectRequestDtoList.add(projectRequestDto);
        }

        return projectRequestDtoList;
    }


    @Transactional
    public int decisionInvite(String sendUUID, String recvUUID, Long projectId, boolean decision) {

        if (decision) {
            projectRequestRepository.deleteByAllId(sendUUID, recvUUID, projectId);

            ProjectRoleEntity projectRoleEntity = projectRoleRepository.insertToRoleEntity(projectId, recvUUID, Long.valueOf(0));

            return 1;
        }

        else if (!decision) {
            projectRequestRepository.deleteByAllId(sendUUID, recvUUID, projectId);
            return 1;
        }

        else {
            return 0;
        }
    }

    public List<ProjectRequestDto> findAllProjectRequestRecv(UserDto userDto) {
        List<ProjectRequestDto> projectRequestDtoList = new ArrayList<>();
        List<ProjectRequestEntity> projectRequestEntityList = projectRequestRepository.findProjectRequest(userDto.getUuid());
        for (ProjectRequestEntity projectRequestEntity : projectRequestEntityList) {
            ProjectRequestDto projectRequestDto = new ProjectRequestDto();
            projectRequestDto.insertEntity(projectRequestEntity);
            projectRequestDtoList.add(projectRequestDto);
        }
        return projectRequestDtoList;
    }

    //////////////////////////////////////////////////////////////////
    // project role
    //////////////////////////////////////////////////////////////////

    @Transactional
    public ProjectRoleDto addRoleManager(ProjectDto projectDto, UserDto userDto) {
        ProjectEntity projectEntity = projectDto.toEntity();
        UserEntity userEntity = userDto.toEntity();
        RoleEntity roleEntity = roleRepository.findById(Long.valueOf(1)).orElse(null);

        ProjectRoleDto projectRoleDto = new ProjectRoleDto(projectEntity, userEntity, roleEntity);

        ProjectRoleEntity projectRoleEntity = projectRoleDto.toEntity();

        projectRoleRepository.save(projectRoleEntity);
        return ProjectRoleDto.toProjectRoleDto(projectRoleEntity);
    }

    @Transactional
    public ProjectRoleDto addRoleEntry(ProjectDto projectDto, UserDto userDto) {
        ProjectEntity projectEntity = projectDto.toEntity();

        UserEntity userEntity = userDto.toEntity();
        RoleEntity roleEntity = roleRepository.findById(Long.valueOf(0)).orElse(null);

        ProjectRoleDto projectRoleDto = new ProjectRoleDto(projectEntity, userEntity, roleEntity);

        ProjectRoleEntity projectRoleEntity = projectRoleDto.toEntity();

        projectRoleRepository.save(projectRoleEntity);
        return ProjectRoleDto.toProjectRoleDto(projectRoleEntity);
    }

    @Transactional
    public void deleteRole(ProjectDto projectDto, UserDto userDto) {
        ProjectEntity projectEntity = projectDto.toEntity();
        UserEntity userEntity = userDto.toEntity();

        projectRoleRepository.deleteAllByProjectIdInRole_ProjectIdAndUuidInRole_Uuid(projectEntity.getProjectId(), userEntity.getUuid());
    }

    //////////////////////////////////////////////////////////////////
    // project create
    //////////////////////////////////////////////////////////////////

    public ProjectDto createProject(String title, String subtitle, String startDay, String endDay) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setTitle(title);
        projectDto.setSubtitle(subtitle);
        projectDto.setStartDay(dateManager.formatter(startDay));
        projectDto.setEndDay(dateManager.formatter(endDay));

        ProjectEntity projectEntity = projectRepository.save(projectDto.toEntity());
        projectDto.insertEntity(projectEntity);
        return projectDto;
    }

    public ProjectDto createProject(ProjectDto projectDto) {

        projectRepository.save(projectDto.toEntity());

        return projectDto;
    }

    //////////////////////////////////////////////////////////////////
    // find project
    //////////////////////////////////////////////////////////////////

    public ProjectDto findProject(Long projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId).get();
        ProjectDto projectDto = new ProjectDto();
        projectDto.insertEntity(projectEntity);
        return projectDto;
    }

    public List<ProjectDto> findProjectListRoleManager(String userId) {
        List<ProjectRoleEntity> projectRoleEntityList = projectRoleRepository.findProjectRoleByUser(userId);
        List<ProjectDto> projectDtoList = new ArrayList<>();

        for (ProjectRoleEntity projectRoleEntity : projectRoleEntityList) {

            if (projectRoleEntity.getRole().getId() == 1) {
                ProjectDto projectDto = new ProjectDto();
                projectDto.insertEntity(projectRoleEntity.getProjectIdInRole());
                projectDtoList.add(projectDto);
            }
        }

        if(projectDtoList.isEmpty()){
            return null;
        }

        return projectDtoList;
    }

    public List<ProjectDto> findProjectListRoleEntry(String userId) {
        List<ProjectRoleEntity> projectRoleEntityList = projectRoleRepository.findProjectRoleByUser(userId);
        List<ProjectDto> projectDtoList = new ArrayList<>();

        for (ProjectRoleEntity projectRoleEntity : projectRoleEntityList) {

            if (projectRoleEntity.getRole().getId() == 0) {
                ProjectDto projectDto = new ProjectDto();
                projectDto.insertEntity(projectRoleEntity.getProjectIdInRole());
                projectDtoList.add(projectDto);
            }
        }

        if(projectDtoList.isEmpty()){
            return null;
        }

        return projectDtoList;
    }

    public List<ProjectDto> findProjectListRoleNot(String userId) {
        List<ProjectDto> findAll = findAllProjectList();
        List<ProjectDto> userAll = findProjectList(userId);
        List<ProjectDto> removeList = new ArrayList<>();

        if (userAll == null) {
            return findAll;
        }

        for (ProjectDto findProjectDto : findAll) {
            for (ProjectDto userProjectDto : userAll){
                if (findProjectDto.getProjectId().equals(userProjectDto.getProjectId())){
                    removeList.add(findProjectDto);
                }
            }
        }

        for (ProjectDto projectDto : removeList){
            findAll.remove(projectDto);
        }

        return findAll;
    }

    public List<ProjectDto> findProjectList(String userId){
        List<ProjectRoleEntity> projectRoleEntityList = projectRoleRepository.findProjectRoleByUser(userId);
        List<ProjectDto> projectDtoList = new ArrayList<>();

        for (ProjectRoleEntity projectRoleEntity : projectRoleEntityList) {

            ProjectDto projectDto = new ProjectDto();
            projectDto.insertEntity(projectRoleEntity.getProjectIdInRole());
            projectDtoList.add(projectDto);

        }

        if(projectDtoList.isEmpty()){
            return null;
        }

        return projectDtoList;
    }

    public List<ProjectDto> findAllProjectList(){
        List<ProjectEntity> projectEntityList = projectRepository.findAll();
        List<ProjectDto> projectDtoList = new ArrayList<>();

        for (ProjectEntity projectEntity : projectEntityList) {
            ProjectDto projectDto = new ProjectDto();
            projectDto.insertEntity(projectEntity);
            projectDtoList.add(projectDto);
        }

        if (projectDtoList.isEmpty()) {
            return null;
        }

        return projectDtoList;
    }

    public List<ProjectDto> findProjectByTitleContaining(String title){
        List<ProjectEntity> projectEntityList = projectRepository.findByTitleContaining(title);
        List<ProjectDto> projectDtoList = new ArrayList<>();

        for (ProjectEntity projectEntity : projectEntityList) {
            ProjectDto projectDto = new ProjectDto();
            projectDto.insertEntity(projectEntity);
            projectDtoList.add(projectDto);
        }

        if(projectDtoList.isEmpty()){
            return null;
        }

        return projectDtoList;
    }

}
