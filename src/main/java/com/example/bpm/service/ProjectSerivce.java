package com.example.bpm.service;

import com.example.bpm.dto.ProjectDto;
import com.example.bpm.dto.ProjectRequestDto;
import com.example.bpm.dto.ProjectRoleDto;
import com.example.bpm.dto.UserDto;
import com.example.bpm.entity.*;
import com.example.bpm.repository.*;
import com.example.bpm.service.dateLogic.DateManager;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.ArrayList;
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

    DateManager dateManager = new DateManager();

    /*Request Table 관련 기능*/

    //프로젝트 초대를 보내는 메서드      리턴 데이터는 send 한 기록을 보여준다
    public void sendInvite(String sendUser, String recvUser, Long projectId) {
        if (sendUser != null && recvUser != null) {
            projectRequestRepository.plusProjectRequest(sendUser, recvUser, projectId);
            log.info("친구 요청 정상 작동 (서비스 작동)");
            return;
//            Optional<ProjectRequestEntity> projectRequestEntity
//                    = projectRequestRepository.findById(sendUser);
//            //만약 정상 친구요청이 되면 그 row을 확인할 수 있게 return 한다
//            return ProjectRequestDto.toProjectRequestDto(projectRequestEntity.get());
//        } else {
//            log.info("Dto NULL 값 (서비스 작동)");
//            return null;
//        }
        }
    }

    //초대를 확인하는 메서드 recvUUID로만 보여짐
    public List<ProjectRequestDto> findAllToRequestProject(String recvUUID) {
        List<ProjectRequestEntity> entityList = projectRequestRepository.selectParticipantsList(recvUUID);
        List<ProjectRequestDto> dtoList = new ArrayList<>();

        for (ProjectRequestEntity projectRequestEntity :
                entityList) {
            dtoList.add(ProjectRequestDto.toProjectRequestDto(projectRequestEntity));
        }
        log.info("recvUUID를 이용한 초대 수신 메서드 정상작동 (서비스 작동)");
        return dtoList;
    }

    //초대 수락하는 메서드(무조건 비권한자로써 수락받는다)
    //서비스의 파라미터로 true false 값을 받아와도 되지만 파라미터가 ㅈㄴ 많으므로 컨트롤러에서 if 문을 거칠 필요가 있음 (코드 개더럽네)
    public int submitInvite(String sendUUID, String recvUUID, Long projectId, boolean input) {
        //수락
        if (input) {
            //ProjectRquest에 있는 데이터 삭제
            projectRequestRepository.deleteByAllId(sendUUID, recvUUID, projectId);
            log.info("수락 요청으로 인한 요청테이블 데이터 삭제 작동 (서비스 작동)");
            //ProejctRole Table에 데이터 추가
            ProjectRoleEntity projectRoleEntity = projectRoleRepository.insertToRoleEntity(projectId, recvUUID, Long.valueOf(0));
            log.info("수락 요청으로 인한 Role Table에 데이터 삽입 (서비스 작동)");
            return 1;
        }
        //거절
        else if (!input) {
            //ProjectRquest에 있는 데이터 삭제
            projectRequestRepository.deleteByAllId(sendUUID, recvUUID, projectId);
            log.info("거절 요청으로 인한 요청테이블 데이터 삭제 작동 (서비스 작동)");
            return 1;
        } else {
            log.info("수락 요청의 변수값이 잘못되었습니다. (서비스 작동)");
            return 0;
        }
    }
    /*Request Table 관련 메서드 끝*/

    /*ProjectRole + Project Table 관련 기능*/

    public List<ProjectDto> findManagerToProjectList(String userId) {
        //List<ProjectRoleEntity> entityList = projectRoleRepository.findByAlltoUserId(userId);
        List<ProjectRoleEntity> entityList = projectRoleRepository.userForRole(userId);
        log.info("uuid를 통한 관리자 권한 프로젝트 조회");
        List<ProjectDto> dtoListToM = new ArrayList<>();
        for (ProjectRoleEntity projectRoleEntity :
                entityList) {
            //1 == 권한자 (프로젝트 생성자)
            if (projectRoleEntity.getRole().getId() == 1) {
                log.info("관리자 권한으로 된 프로젝트가 있음 (서비스 작동), " + projectRoleEntity.getProjectIdInRole().getTitle());
                ProjectDto dto = ProjectDto.toProjectDto(projectRepository.findById(
                        projectRoleEntity.getProjectIdInRole().getProjectId()).orElse(null));
                dtoListToM.add(dto);
            }
        }
        if (dtoListToM.isEmpty()) {
            log.info("관리자 권한으로 된 프로젝트가 없음 (서비스 작동)");
            return null;
        } else {
            return dtoListToM;
        }
    }

    //본인이 참여하고있는 비권한자 프로젝트 리스트를 리턴해주는 메서드 (프로젝트 리스트 페이지에서 필요함)
    public List<ProjectDto> findParticipantsToProjectList(String userId) {
        List<ProjectRoleEntity> entityList = projectRoleRepository.userForRole(userId);
        List<ProjectDto> dtoListToP = new ArrayList<>();
        for (ProjectRoleEntity projectRoleEntity :
                entityList) {
            //2 == 비권한자 (프로젝트 참여자)
            if (projectRoleEntity.getRole().getId() == 0) {
                log.info("비관리자 권한으로 된 프로젝트가 있음 (서비스 작동)");
                ProjectDto dto = ProjectDto.toProjectDto(projectRepository.findById(
                        projectRoleEntity.getProjectIdInRole().getProjectId()).orElse(null));
                dtoListToP.add(dto);
            }
        }
        if (dtoListToP.isEmpty()) {
            log.info("비관리자 권한으로 된 프로젝트가 없음 (서비스 작동)");
            return null;
        } else {
            return dtoListToP;
        }
    }

    //전체 프로젝트 리스트 가져오는 메서드
    public List<ProjectDto> findAllToProjectList(){
        List<ProjectEntity> entityList = projectRepository.findAll();
        List<ProjectDto> dtoList = new ArrayList<>();
        for (ProjectEntity projectEntity : entityList) {
            dtoList.add(ProjectDto.toProjectDto(projectEntity));
        }
        return dtoList;
    }


    //관리자 프로젝트 권한 부여 메서드
    public ProjectRoleDto autorization(ProjectDto projectDto, UserDto userDto) {
        ProjectEntity projectEntity = ProjectEntity.toProjectEntity(projectDto);
        UserEntity userEntity = UserEntity.toUserEntity(userDto);
        RoleEntity roleEntity = roleRepository.findById(Long.valueOf(1)).orElse(null);
        log.info("권한 부여 메서드 실행 중 " + projectEntity.getProjectId() + ", " + userEntity.getUuid() + ", " + roleEntity.getRoleName());
        ProjectRoleEntity projectRoleEntity = ProjectRoleEntity.toProjectRoleEntity(projectEntity, userEntity, roleEntity);
        log.info("projectRoleEntity 생성 완료");
        log.info("프로젝트 생성자 권한 부여" + projectRoleEntity.getProjectIdInRole().getProjectId() + ", " +
                projectRoleEntity.getUuidInRole().getUuid() + ", " + projectRoleEntity.getRole().getRoleName());
        projectRoleRepository.save(projectRoleEntity);
        return ProjectRoleDto.toProjectRoleDto(projectRoleEntity);
    }

    //생성 메서드
    public ProjectDto createProject(String title, String subtitle, String startDay, String endDay) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setTitle(title);
        projectDto.setSubtitle(subtitle);
        projectDto.setStartDay(dateManager.formatter(startDay));
        projectDto.setEndDay(dateManager.formatter(endDay));
        ProjectEntity projectEntity = projectRepository.save(ProjectEntity.toProjectEntity(projectDto));
        log.info("프로젝트 정상 생성 (서비스 작동)");
        return ProjectDto.toProjectDto(projectEntity);
    }

    //프로젝트 접속하는 메서드
    public ProjectDto selectProject(Long projectId) {
        Optional<ProjectEntity> projectEntity = projectRepository.findById(projectId);
        return ProjectDto.toProjectDto(projectEntity.get());
    }


}
