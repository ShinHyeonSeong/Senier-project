package com.example.bpm.service;

import com.example.bpm.dto.document.DocumentDto;
import com.example.bpm.dto.document.LogDto;
import com.example.bpm.dto.project.HeadDto;
import com.example.bpm.dto.project.ProjectDto;
import com.example.bpm.dto.project.WorkDto;
import com.example.bpm.dto.project.relation.WorkCommentDto;
import com.example.bpm.dto.project.relation.WorkDocumentDto;
import com.example.bpm.dto.user.UserDto;
import com.example.bpm.dto.user.relation.UserWorkDto;
import com.example.bpm.entity.document.BlockEntity;
import com.example.bpm.entity.document.LogEntity;
import com.example.bpm.entity.project.data.HeadEntity;
import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.project.data.work.WorkCommentEntity;
import com.example.bpm.entity.project.data.WorkEntity;
import com.example.bpm.entity.project.relation.WorkDocumentEntity;
import com.example.bpm.entity.user.UserEntity;
import com.example.bpm.entity.user.relation.UserWorkEntity;
import com.example.bpm.repository.*;
import com.example.bpm.service.Logic.dateLogic.DateManager;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
@NoArgsConstructor(force = true)
public class ProjectDetailSerivce {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private HeadRepository headRepository;
    @Autowired
    private WorkRepository workRepository;
    @Autowired
    private UserWorkRepository userWorkRepository;
    @Autowired
    private WorkCommentRepository workCommentRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private WorkDocumentRepository workDocumentRepository;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ProjectRequestRepository projectRequestRepository;
    @Autowired
    private ProjectRoleRepository projectRoleRepository;
    @Autowired
    private ProjectSerivce projectSerivce;

    DateManager dateManager = new DateManager();

    //////////////////////////////////////////////////////////////////
    // create Data
    //////////////////////////////////////////////////////////////////

    // Head create
    public HeadDto createHead(String title, String startDate, String deadline, String discription, ProjectDto projectDto){
        HeadDto headDto = new HeadDto();

        headDto.setTitle(title);
        headDto.setStartDay(dateManager.formatter(startDate));
        headDto.setEndDay(dateManager.formatter(deadline));
        headDto.setDiscription(discription);
        headDto.setCompletion(0);
        headDto.setProjectIdToHead(projectRepository.findById(projectDto.getProjectId()).orElse(null));

        headRepository.save(headDto.toEntity());

        return  headDto;
    }

    public HeadDto createHead(HeadDto headDto){

        headRepository.save(headDto.toEntity());

        return headDto;
    }

    // work create
    public WorkDto createWork(String title, String discription, String startDate, String deadline, HeadDto connectHead, ProjectDto projectDto) {

        WorkDto workDto = new WorkDto();

        workDto.setTitle(title);
        workDto.setDiscription(discription);
        workDto.setStartDay(dateManager.formatter(startDate));
        workDto.setEndDay(dateManager.formatter(deadline));
        workDto.setCompletion(0);
        workDto.setHeadIdToWork(headRepository.findById(connectHead.getHeadId()).orElse(null));
        workDto.setProjectIdToWork(projectRepository.findById(projectDto.getProjectId()).orElse(null));

        workRepository.save(workDto.toEntity());

        return workDto;
    }

    public WorkDto createWork(WorkDto workDto){

        workRepository.save(workDto.toEntity());

        return workDto;
    }

    // work comment create
    public void createWorkComment (WorkCommentDto workCommentDto) {

        workCommentRepository.save(workCommentDto.toEntity());

    }

    // work user add
    public void addUserWork(WorkDto workDto, List<String> chargeUsers) {
        UserWorkEntity userWorkEntity = new UserWorkEntity();

        WorkEntity workEntity = workDto.toEntity();

        for (String uuid : chargeUsers) {
            UserEntity userEntity = userRepository.findById(uuid).get();
            userWorkEntity = new UserWorkDto(workEntity, userEntity).toEntity();
            userWorkRepository.save(userWorkEntity);
        }
    }


    /* - - - - OverLap check - - - - */

    public boolean checkOverlapHead(String title){
        return headRepository.findByTitle(title).isPresent();
    }

    public boolean checkOverlapWork(String title){
        return workRepository.findByTitle(title).isPresent();
    }

    //////////////////////////////////////////////////////////////////
    // find Data
    //////////////////////////////////////////////////////////////////

    /* Head HeadDto */
    public HeadDto findHeadById(Long id) {
        HeadEntity headEntity = headRepository.findById(id).get();
        HeadDto headDto = new HeadDto();
        headDto.insertEntity(headEntity);
        return headDto;
    }

    public List<HeadDto> findHeadListByProject(ProjectDto projectDto) {
        List<HeadDto> headDtoList = new ArrayList<>();
        List<HeadEntity> headEntityList = headRepository.findAllByProjectIdToHead_ProjectId(projectDto.getProjectId());

        for (HeadEntity headEntity : headEntityList) {
            HeadDto headDto = new HeadDto();
            headDto.insertEntity(headEntity);
            headDtoList.add(headDto);
        }
        return headDtoList;
    }


    public int countProgressHead(List<HeadDto> headDtoList) {
        int progressHead = 0;
        for (HeadDto headDto : headDtoList) {
            if (headDto.getCompletion() == 0) {
                progressHead++;
            }
        }
        return progressHead;
    }


    /* Work WorkDto */
    public WorkDto findWork(Long id) {
        WorkEntity workEntity = workRepository.findById(id).get();
        WorkDto workDto = new WorkDto();
        workDto.insertEntity(workEntity);
        return workDto;
    }

    public WorkDto findWorkByDocument(DocumentDto documentDto){
        WorkDocumentEntity workDocumentEntity= workDocumentRepository.findByDocumentIdToWorkDocument_DocumentId(documentDto.getDocumentId());
        WorkEntity workEntity = workDocumentEntity.getWorkIdToWorkDocument();
        WorkDto workDto = new WorkDto();
        workDto.insertEntity(workEntity);

        return workDto;
    }

    public List<WorkDto> findWorkListByProject(ProjectDto projectDto) {
        List<WorkDto> workDtoList = new ArrayList<>();
        List<WorkEntity> workEntityList = workRepository.findAllByProjectIdToWork_ProjectId(projectDto.getProjectId());

        for (WorkEntity workEntity : workEntityList) {
            WorkDto workDto = new WorkDto();
            workDto.insertEntity(workEntity);
            workDtoList.add(workDto);
        }
        return workDtoList;
    }

    public List<WorkDto> findWorkListByHead(HeadDto headDto) {
        List<WorkEntity> workEntityList = workRepository.findAllByHeadIdToWork_HeadId(headDto.getHeadId());
        List<WorkDto> workDtoList = new ArrayList<>();

        for (WorkEntity workEntity : workEntityList) {
            WorkDto workDto = new WorkDto();
            workDto.insertEntity(workEntity);
            workDtoList.add(workDto);
        }
        return workDtoList;
    }

    public List<WorkDto> findWorkListByUser(UserDto userDto) {
        List<UserWorkEntity> userWorkList = userWorkRepository.findAllByUserIdToUserWork_Uuid(userDto.getUuid());
        List<WorkDto> workDtoList = new ArrayList<>();

        for (UserWorkEntity userWorkEntity : userWorkList) {
            WorkDto workDto = new WorkDto();
            workDto.insertEntity(userWorkEntity.getWorkIdToUserWork());
            workDtoList.add(workDto);
        }
        return workDtoList;
    }

    public int countProgressWork(List<WorkDto> workDtoList) {
        int progressWork = 0;
        for (WorkDto workDto : workDtoList) {
            if (workDto.getCompletion() == 0) {
                progressWork++;
            }
        }
        return progressWork;
    }

    /* User UserDto */

    public List<UserDto> findUserListByWork(WorkDto workDto){
        List<UserDto> userDtoList = new ArrayList<>();
        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByWorkIdToUserWork_WorkId(workDto.getWorkId());

        for (UserWorkEntity userWorkEntity: userWorkEntityList) {
            UserDto userDto = new UserDto();
            userDto.insertEntity(userWorkEntity.getUserIdToUserWork());
            userDtoList.add(userDto);
        }

        return userDtoList;
    }

    /* UserWork UserWorkDto */
    public List<UserWorkDto> findUserWorkListByWorkId(Long workId) {
        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByWorkIdToUserWork_WorkId(workId);
        List<UserWorkDto> userWorkDtoList = new ArrayList<>();

        for (UserWorkEntity userWorkEntity : userWorkEntityList) {
            UserWorkDto userWorkDto = new UserWorkDto();
            userWorkDto.insertEntity(userWorkEntity);
            userWorkDtoList.add(userWorkDto);
        }
        return userWorkDtoList;
    }

    public List<UserWorkDto> findUserWorkListByWork(WorkDto workDto) {
        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByWorkIdToUserWork_WorkId(workDto.getWorkId());
        List<UserWorkDto> userWorkDtoList = new ArrayList<>();

        for (UserWorkEntity userWorkEntity : userWorkEntityList){
            UserWorkDto userWorkDto = new UserWorkDto();
            userWorkDto.insertEntity(userWorkEntity);
            userWorkDtoList.add(userWorkDto);
        }
        return userWorkDtoList;
    }

    /* WorkDocument WorkDocumentDto */
    public List<WorkDocumentDto> findWorkDocumentListByWork(WorkDto workDto) {
        List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(workDto.getWorkId());
        List<WorkDocumentDto> workDocumentDtoList = new ArrayList<>();

        for (WorkDocumentEntity workDocumentEntity : workDocumentEntityList) {
            WorkDocumentDto workDocumentDto = new WorkDocumentDto();
            workDocumentDto.insertEntity(workDocumentEntity);
            workDocumentDtoList.add(workDocumentDto);
        }
        return workDocumentDtoList;
    }

    /* Document DocumentDto */
    public List<DocumentDto> findDocumentListByWorkList(List<WorkDto> workDtoList) {
        List<DocumentDto> documentDtoList = new ArrayList<>();
        for (WorkDto workDto : workDtoList) {
            List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(workDto.getWorkId());
            for (WorkDocumentEntity workDocumentEntity : workDocumentEntityList) {
                DocumentDto documentDto = new DocumentDto();
                documentDto.insertEntity(documentRepository.findByDocumentId(workDocumentEntity.getDocumentIdToWorkDocument().getDocumentId()));
                documentDtoList.add(documentDto);
            }
        }
        return documentDtoList;
    }

    public List<DocumentDto> findDocumentListByWork(WorkDto workDto){
        List<DocumentDto> documentDtoList = new ArrayList<>();
        List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(workDto.getWorkId());

        for (WorkDocumentEntity workDocumentEntity: workDocumentEntityList) {
            DocumentDto documentDto = new DocumentDto();
            documentDto.insertEntity(workDocumentEntity.getDocumentIdToWorkDocument());
            documentDtoList.add(documentDto);
        }

        return documentDtoList;
    }

    /* WorkComment WorkCommentDto */

    public List<WorkCommentDto> findWorkCommentListByWork(WorkDto workDto) {
        List<WorkCommentEntity> workCommentEntityList = workCommentRepository.findAllByWorkIdToComment_WorkId(workDto.getWorkId());
        List<WorkCommentDto> workCommentDtoList = new ArrayList<>();

        for (WorkCommentEntity workCommentEntity : workCommentEntityList) {
            WorkCommentDto workCommentDto = new WorkCommentDto();
            workCommentDto.insertEntity(workCommentEntity);
            workCommentDtoList.add(workCommentDto);
        }
        return workCommentDtoList;
    }

    public WorkCommentDto findWorkComment(Long id) {
        WorkCommentEntity documentCommentEntity = workCommentRepository.findById(id).get();
        WorkCommentDto workCommentDto = new WorkCommentDto();
        workCommentDto.insertEntity(documentCommentEntity);

        return workCommentDto;
    }

    /* Log LogDto */

    public List<LogDto> findLogListByDocument(DocumentDto documentDto) {
        List<LogEntity> logEntityEntityList = logRepository.findAllByDocumentId(documentDto.getDocumentId());
        List<LogDto> logDtoList = new ArrayList<>();

        for (LogEntity logEntity : logEntityEntityList) {
            LogDto logDto = new LogDto();
            logDto.insertEntity(logEntity);
            logDtoList.add(logDto);
        }
        return logDtoList;
    }

    //////////////////////////////////////////////////////////////////
    // update Data
    //////////////////////////////////////////////////////////////////

    @Transactional
    public HeadDto updateHead(Long beforeId, HeadDto afterHead) {
        HeadEntity headEntity = headRepository.findById(beforeId).get();

        headEntity.setTitle(afterHead.getTitle());
        headEntity.setDiscription(afterHead.getDiscription());
        headEntity.setStartDay(afterHead.getStartDay());
        headEntity.setEndDay(afterHead.getEndDay());
        headEntity.setCompletion(afterHead.getCompletion());

        headRepository.save(headEntity);

        return afterHead;
    }

    @Transactional
    public WorkDto updateWork(Long beforeId, WorkDto afterWorkDto) {
        WorkEntity workEntity = workRepository.findById(beforeId).get();

        workEntity.setTitle(afterWorkDto.getTitle());
        workEntity.setDiscription(afterWorkDto.getDiscription());
        workEntity.setStartDay(afterWorkDto.getStartDay());
        workEntity.setEndDay(afterWorkDto.getEndDay());
        workEntity.setCompletion(afterWorkDto.getCompletion());

        workRepository.save(workEntity);

        return afterWorkDto;
    }


    //////////////////////////////////////////////////////////////////
    // delete Data
    //////////////////////////////////////////////////////////////////

    /* - - - - Project - - - - */

    @Transactional
    public void deleteProject(ProjectDto projectDto) {
        ProjectEntity projectEntity = projectDto.toEntity();

        List<HeadEntity> headEntityList = headRepository.findAllByProjectIdToHead_ProjectId(projectEntity.getProjectId());

        for (HeadEntity headEntity: headEntityList) {
            deleteHead(headEntity);
        }

        deleteProjectRequest(projectEntity);
        deleteMessage(projectEntity);
        deleteProjectRole(projectEntity);

        projectRepository.deleteById(projectDto.getProjectId());
    }

    /* Low */

    @Transactional
    public void deleteProjectRequest(ProjectEntity projectEntity) {
        projectRequestRepository.deleteAllByProjectIdToRequest_ProjectId(projectEntity.getProjectId());
    }

    @Transactional
    public void deleteMessage(ProjectEntity projectEntity) {
        messageRepository.deleteAllByProjectIdToMessage_ProjectId(projectEntity.getProjectId());
    }

    @Transactional
    public void deleteProjectRole(ProjectEntity projectEntity) {
        projectRoleRepository.deleteAllByProjectIdInRole_ProjectId(projectEntity.getProjectId());
    }

    /* - - - - Head - - - - */

    @Transactional
    public void deleteHead(HeadDto headDto){
        HeadEntity headEntity = headDto.toEntity();

        List<WorkEntity> workEntityList = workRepository.findAllByHeadIdToWork_HeadId(headEntity.getHeadId());

        for (WorkEntity workEntity: workEntityList) {
            deleteWork(workEntity);
        }

        headRepository.deleteById(headEntity.getHeadId());
    }

    @Transactional
    public void deleteHead(HeadEntity headEntity){
        List<WorkEntity> workEntityList = workRepository.findAllByHeadIdToWork_HeadId(headEntity.getHeadId());

        for (WorkEntity workEntity: workEntityList) {
            deleteWork(workEntity);
        }

        headRepository.deleteById(headEntity.getHeadId());
    }

    /* - - - - Work - - - - */

    @Transactional
    public void deleteWork(WorkDto workDto) {
        WorkEntity workEntity = workDto.toEntity();

        deleteUserWork(workEntity);
        deleteWorkDocument(workEntity);
        deleteWorkComment(workEntity);
        deleteDocument(workEntity);

        workRepository.deleteById(workEntity.getWorkId());
    }

    @Transactional
    public void deleteWork(WorkEntity workEntity) {

        deleteUserWork(workEntity);
        deleteWorkDocument(workEntity);
        deleteWorkComment(workEntity);
        deleteDocument(workEntity);

        workRepository.deleteById(workEntity.getWorkId());
    }

    /* Low */

    @Transactional
    public void deleteUserWork(WorkEntity workEntity) {
        userWorkRepository.deleteAllByWorkIdToUserWork_WorkId(workEntity.getWorkId());
    }

    @Transactional
    public void deleteWorkDocument(WorkEntity workEntity) {
        workDocumentRepository.deleteAllByWorkIdToWorkDocument_WorkId(workEntity.getWorkId());
    }

    @Transactional
    public void deleteWorkComment(WorkEntity workEntity) {
        workCommentRepository.deleteAllByWorkIdToComment_WorkId(workEntity.getWorkId());
    }

    @Transactional
    public void deleteWorkComment(Long id) {
        workCommentRepository.deleteById(id);
    }

    @Transactional
    public void deleteDocument(WorkEntity workEntity){
        List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(workEntity.getWorkId());

        for (WorkDocumentEntity workDocumentEntity :  workDocumentEntityList) {
            List<BlockEntity> deleteBlockListEntity = blockRepository.findByDocumentId(workDocumentEntity.getDocumentIdToWorkDocument().getDocumentId());
            List<LogEntity> deleteLogListEntity = logRepository.findByDocumentId(workDocumentEntity.getDocumentIdToWorkDocument().getDocumentId());

            for (BlockEntity blockEntity : deleteBlockListEntity) {
                blockRepository.delete(blockEntity);
            }

            for (LogEntity logEntity : deleteLogListEntity) {
                logRepository.delete(logEntity);
            }

            documentRepository.deleteById(workDocumentEntity.getDocumentIdToWorkDocument().getDocumentId());
        }
    }

    //////////////////////////////////////////////////////////////////
    // check Low completion
    //////////////////////////////////////////////////////////////////
    @Transactional
    public boolean checkCompletionHead(Long headId) {
        HeadEntity headEntity = headRepository.findById(headId).get();
        List<WorkEntity> workEntityList = workRepository.findAllByHeadIdToWork_HeadId(headId);

        for (WorkEntity workEntity: workEntityList) {
            if (workEntity.getCompletion() == 0){
                headEntity.setCompletion(0);
                headRepository.save(headEntity);
                return false;
            }
        }

        headEntity.setCompletion(1);
        headRepository.save(headEntity);
        return true;
    }

}

