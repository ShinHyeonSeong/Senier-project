package com.example.bpm.service;

import com.example.bpm.dto.*;
import com.example.bpm.entity.*;
import com.example.bpm.repository.*;
import com.example.bpm.service.dateLogic.DateManager;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@NoArgsConstructor(force = true)
/* 해당 클래스는 프로젝트 안에서 편집이 이루어지고 목표 작업 등을 작성하고 document와의 연결을 구현한 클래스이다    */
public class ProjectDetailSerivce {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final ProjectRepository projectRepository;
    @Autowired
    private final HeadRepository headRepository;
    @Autowired
    private final DetailRepository detailRepository;
    @Autowired
    private final WorkRepository workRepository;
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

    Date currentDate = new Date(); // 시작 날짜(현재) 생성
    DateManager dateManager = new DateManager();

    // 마감기한 문자열 Date 타입 반환 메서드. 문자열 형식 상이로 변환 실패시 null

    /* - - - - 생성 메서드 시작 - - - - */

    // head 생성 메서드
    public HeadDto createHead(String title, String startDate, String deadline, String discription, ProjectDto projectDto) {
        if (headRepository.findByTitle(title).isPresent()) {
            log.info("head title 이 이미 존재한다. (서비스)");
            return null;
        } else {
            log.info("createHead service 작동");
            Date startDay = dateManager.formatter(startDate);
            Date endDay = dateManager.formatter(deadline);
            if (endDay == null) return null;

            HeadDto createHeadDto = new HeadDto();
            createHeadDto.setTitle(title);
            createHeadDto.setDiscription(discription);
            createHeadDto.setStartDay(startDay);
            createHeadDto.setEndDay(endDay);
            createHeadDto.setCompletion(0);
            createHeadDto.setProjectIdToHead(projectRepository.findById(projectDto.getProjectId()).orElse(null));
            log.info("HeadDto 생성 완료");
            HeadEntity createHead = headRepository.save(HeadEntity.toHeadEntity(createHeadDto));
            log.info("HeadEntity 저장 및 생성 완료");
            return HeadDto.toHeadDto(createHead);
        }
    }

    // detail 생성 메서드
    public DetailDto createDetail(String title, String startDate, String deadline, String discription,
                                  HeadDto connectedHead, ProjectDto projectDto) {
        if (detailRepository.findByTitle(title).isPresent()) {
            log.info("detail title 이 이미 존재한다. (서비스)");
            return null;
        } else {
            log.info("createDetail service 작동");
            Date startDay = dateManager.formatter(startDate);
            Date endDay = dateManager.formatter(deadline);
            if (endDay == null) return null;

            DetailDto createDetailDto = new DetailDto();
            createDetailDto.setTitle(title);
            createDetailDto.setDiscription(discription);
            createDetailDto.setStartDay(startDay);
            createDetailDto.setEndDay(endDay);
            createDetailDto.setCompletion(0);
            createDetailDto.setHeadIdToDetail(headRepository.findById(connectedHead.getHeadId()).orElse(null));
            createDetailDto.setProjectIdToDetail(projectRepository.findById(projectDto.getProjectId()).orElse(null));
            log.info("DetailDto 생성 완료");
            DetailEntity createHead = detailRepository.save(DetailEntity.toDetailEntity(createDetailDto));
            log.info("DetailEntity 저장 및 생성 완료");
            return DetailDto.toDetailDto(createHead);
        }
    }

    // 내 작업 만들기
    public WorkDto createWork(String title, String discription, String startDate, String deadline,
                              DetailDto connectDetail, ProjectDto projectDto) {
        if (workRepository.findByTitle(title).isPresent()) {
            log.info("work title 이 이미 존재한다. (서비스)");
            return null;
        }
        Date startDay = dateManager.formatter(startDate);
        Date endDay = dateManager.formatter(deadline);

        WorkDto createWorkDto = new WorkDto();
        createWorkDto.setTitle(title);
        createWorkDto.setDiscription(discription);
        createWorkDto.setStartDay(startDay);
        createWorkDto.setEndDay(endDay);
        createWorkDto.setCompletion(0);
        createWorkDto.setDetailIdToWork(detailRepository.findById(connectDetail.getDetailId()).orElse(null));
        createWorkDto.setProjectIdToWork(projectRepository.findById(projectDto.getProjectId()).orElse(null));
        log.info("work 생성 성공 (서비스)");
        WorkEntity createWork = workRepository.save(WorkEntity.toWorkEntity(createWorkDto));
        log.info("workEntity Id = " + createWork.getWorkId().toString());
        return WorkDto.toWorkDto(createWork);
    }

    //유저 작업 테이블 추가 메서드
    public void addUserWork(WorkDto workDto, List<String> chargeUsers) {
        log.info("user_work table insert method");
        WorkEntity workEntity = WorkEntity.toWorkEntity(workDto);
        UserWorkEntity userWorkEntity = new UserWorkEntity();

        for (String uuid : chargeUsers) {
            Optional<UserEntity> userEntity = userRepository.findById(uuid);
            userWorkEntity = UserWorkEntity.toUserWorkEntity(workEntity, userEntity.get());
            userWorkRepository.save(userWorkEntity);
        }
    }

    /* - - - - 생성 메서드 끝 - - - - */

    /* - - - - 선택 메서드 시작 - - - - - */

    public HeadDto selectHead(Long id) {
        Optional<HeadEntity> find = headRepository.findById(id);
        return HeadDto.toHeadDto(find.get());
    }

    public List<HeadDto> selectAllHead(ProjectDto projectDto) {
        List<HeadDto> headDtoList = new ArrayList<>();
        List<HeadEntity> headEntityList = headRepository.findAllByProjectIdToHead_ProjectId(projectDto.getProjectId());
        for (HeadEntity headEntity : headEntityList) {
            headDtoList.add(HeadDto.toHeadDto(headEntity));
        }
        return headDtoList;
    }

    public DetailDto selectDetail(Long id) {
        Optional<DetailEntity> find = detailRepository.findById(id);
        return DetailDto.toDetailDto(find.get());
    }

    public List<DetailDto> selectAllDetailForHead(HeadDto headDto) {
        List<DetailDto> detailDtoList = new ArrayList<>();
        List<DetailEntity> detailEntityList = detailRepository.findAllByHeadIdToDetail_HeadId(headDto.getHeadId());
        for (DetailEntity detailEntity : detailEntityList) {
            detailDtoList.add(DetailDto.toDetailDto(detailEntity));
        }
        return detailDtoList;
    }

    public List<DetailDto> selectAllDetailForProject(ProjectDto projectDto) {
        List<DetailDto> detailDtoList = new ArrayList<>();
        List<DetailEntity> detailEntityList = detailRepository.
                findAllByProjectIdToDetail_ProjectId(projectDto.getProjectId());
        for (DetailEntity detailEntity : detailEntityList) {
            detailDtoList.add(DetailDto.toDetailDto(detailEntity));
        }
        return detailDtoList;
    }

    public WorkDto selectWork(Long id) {
        Optional<WorkEntity> find = workRepository.findById(id);
        return WorkDto.toWorkDto(find.get());
    }

    public List<WorkDto> selectAllWorkForProject(ProjectDto projectDto) {
        List<WorkDto> workDtoList = new ArrayList<>();
        List<WorkEntity> workEntityList = workRepository.findAllByProjectIdToWork_ProjectId(projectDto.getProjectId());
        for (WorkEntity workEntity : workEntityList) {
            workDtoList.add(WorkDto.toWorkDto(workEntity));
        }
        return workDtoList;
    }

    public List<WorkDto> selectAllWorkForDetail(Long id) {
        List<WorkEntity> workEntityList = workRepository.findAllByDetailIdToWork_DetailId(id);
        List<WorkDto> workDtoList = new ArrayList<>();
        for (WorkEntity workEntity : workEntityList) {
            workDtoList.add(WorkDto.toWorkDto(workEntity));
        }
        return workDtoList;
    }

    public List<WorkDto> selectAllWorkForUser(UserDto userDto) {
        List<UserWorkEntity> userWorkList = userWorkRepository.findAllByUserIdToUserWork_Uuid(userDto.getUuid());
        List<WorkDto> workDtoList = new ArrayList<>();
        for (UserWorkEntity userWorkEntity : userWorkList) {
            workDtoList.add(WorkDto.toWorkDto(workRepository.findById(
                    userWorkEntity.getWorkIdToUserWork().getWorkId()).orElse(null)));
        }
        return workDtoList;
    }

    public UserDto selectUserForUserWork(WorkDto workDto) {
        UserWorkEntity userWorkEntity = userWorkRepository.findByWorkIdToUserWork_WorkId(workDto.getWorkId());
        Optional<UserEntity> userEntity = userRepository.findById(userWorkEntity.getUserIdToUserWork().getUuid());
        return UserDto.toUserDto(userEntity.get());
    }

    public List<UserWorkDto> selectAllUserWorkForWork(Long workId) {
        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByWorkIdToUserWork_WorkId(workId);
        List<UserWorkDto> userWorkDtoList = new ArrayList<>();
        for (UserWorkEntity userWorkEntity : userWorkEntityList) {
            userWorkDtoList.add(UserWorkDto.toUserWorkDto(userWorkEntity));
        }
        return userWorkDtoList;
    }

    public UserWorkDto selectUserWorkForWork(WorkDto workDto) {
        UserWorkDto userWorkDto = UserWorkDto.toUserWorkDto(
                userWorkRepository.findByWorkIdToUserWork_WorkId(workDto.getWorkId()));
        return userWorkDto;
    }

    public List<UserWorkDto> selectUserWorkForWorkList(WorkDto workDto) {
        List<UserWorkDto> userWorkDtoList = new ArrayList<>();
        for (UserWorkEntity userWorkEntity : userWorkRepository.findAllByWorkIdToUserWork_WorkId(workDto.getWorkId())){
            userWorkDtoList.add(UserWorkDto.toUserWorkDto(userWorkEntity));
        }
        return userWorkDtoList;
    }

    public Map<WorkDto, List<UserDto>> selectAllUserWorkForWorkList(List<WorkDto> workDtoList) {
        Map<WorkDto, List<UserDto>> userWorkMap = new HashMap<>();
        for (WorkDto workDto : workDtoList) {
            List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByWorkIdToUserWork_WorkId(workDto.getWorkId());
            WorkDto workDtoKey = WorkDto.toWorkDto(userWorkEntityList.get(0).getWorkIdToUserWork());
            List<UserDto> userDtoList = new ArrayList<>();
            for (UserWorkEntity userWorkEntity : userWorkEntityList) {
                userDtoList.add(UserDto.toUserDto(userWorkEntity.getUserIdToUserWork()));
            }
            userWorkMap.put(workDtoKey, userDtoList );
        }
        return userWorkMap;
    }

    public List<WorkDocumentDto> selectAllWorkDocumentForWork(WorkDto workDto) {
        List<WorkDocumentEntity> workDocumentEntityList =
                workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(workDto.getWorkId());
        List<WorkDocumentDto> workDocumentDtoList = new ArrayList<>();
        WorkDocumentDto workDocumentDto = new WorkDocumentDto();
        for (WorkDocumentEntity workDocumentEntity : workDocumentEntityList) {
            workDocumentDtoList.add(workDocumentDto.toWorkDocumentDto(workDocumentEntity));
        }
        return workDocumentDtoList;
    }

    // work list 매개변수를 통해 workDocument 테이블을 경유하여 workId에 대응하는 모든 document를 리스트로 반환
    public List<DocumentDto> selectAllDocumentForWorkList(List<WorkDto> workDtoList) {
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

    public List<WorkCommentDto> selectAllWorkCommentForWork(WorkDto workDto) {
        List<WorkCommentEntity> workCommentEntityList =
                workCommentRepository.findAllByWorkIdToComment_WorkId(workDto.getWorkId());
        List<WorkCommentDto> workCommentDtoList = new ArrayList<>();
        for (WorkCommentEntity workCommentEntity : workCommentEntityList) {
            workCommentDtoList.add(WorkCommentDto.toWorkCommentDto(workCommentEntity));
        }
        return workCommentDtoList;
    }

    public List<LogDto> selectAllLogForDocument(DocumentDto documentDto) {
        List<Log> logEntityList = logRepository.findAllByDocumentId(documentDto.getDocumentId());
        List<LogDto> logDtoList = new ArrayList<>();
        for (Log log : logEntityList) {
            LogDto logDto = new LogDto();
            logDto.insertEntity(log);
            logDtoList.add(logDto);
        }
        return logDtoList;
    }

    public List<BlockDto> selectAllBlockForDocument(DocumentDto documentDto) {
        List<Block> blockEntityList = blockRepository.findAllByDocumentId(documentDto.getDocumentId());
        List<BlockDto> blockDtoList = new ArrayList<>();
        for (Block block : blockEntityList) {
            BlockDto blockDto = new BlockDto();
            blockDto.insertEntity(block);
            blockDtoList.add(blockDto);
        }
        return blockDtoList;
    }

    /* - - - - 선택 메서드 끝 - - - - - */

    /* - - - - 수정 메서드 시작 - - - - - */
    public HeadDto updateSelectHead(Long headId) {
        Optional<HeadEntity> find = headRepository.findById(headId);
        if (find.isEmpty()) {
            log.info("찾은 결과가 없음 (서비스)");
            return null;
        } else {
            log.info(find.get().getTitle() + "의 내용을 찾음 (서비스)");
            return HeadDto.toHeadDto(find.get());
        }
    }

    public HeadDto updateHead(HeadDto headDto) {
        HeadEntity afterEntity = headRepository.save(HeadEntity.toHeadEntity(headDto));
        return HeadDto.toHeadDto(afterEntity);
    }

    public DetailDto updateSelectDetail(Long detailId) {
        Optional<DetailEntity> findDetail = detailRepository.findById(detailId);
        if (findDetail.isPresent()) {
            log.info(findDetail.get().getTitle() + "의 내용을 찾음 (서비스)");
            return DetailDto.toDetailDto(findDetail.get());
        } else {
            log.info("찾은 결과가 없음 (서비스)");
            return null;
        }
    }

    public DetailDto updateDetail(DetailDto detailDto) {
        DetailEntity afterEntity = DetailEntity.toDetailEntity(detailDto);
        detailRepository.save(afterEntity);
        return DetailDto.toDetailDto(afterEntity);
    }

    public WorkDto updateSelectWork(Long workId) {
        Optional<WorkEntity> find = workRepository.findById(workId);
        if (find.isEmpty()) {
            return null;
        } else {
            return WorkDto.toWorkDto(find.get());
        }
    }

    public WorkDto updateWork(WorkDto workDto) {
        WorkEntity afterEntity = WorkEntity.toWorkEntity(workDto);
        workRepository.save(afterEntity);
        return WorkDto.toWorkDto(afterEntity);
    }

    public HeadDto editHead(String title, String startDate, String deadline, String discription, Long headId) {
        Optional<HeadEntity> headEntity = headRepository.findById(headId);
        if (headEntity.isPresent()) {
            log.info("");
            Date startDay = dateManager.formatter(startDate);
            Date endDay = dateManager.formatter(deadline);
            HeadDto headDto = HeadDto.toHeadDto(headEntity.get());
            headDto.setTitle(title);
            headDto.setStartDay(startDay);
            headDto.setEndDay(endDay);
            headDto.setDiscription(discription);
            HeadDto editHeadDto = HeadDto.toHeadDto(headRepository.save(HeadEntity.toHeadEntity(headDto)));
            return editHeadDto;
        } else return null;
    }

    public DetailDto editDetail(String title, String startDate, String deadline, String discription,
                                Long headId, Long detailId) {
        Optional<DetailEntity> detailEntity = detailRepository.findById(detailId);
        if (detailEntity.isPresent()) {
            Date startDay = dateManager.formatter(startDate);
            Date endDay = dateManager.formatter(deadline);
            DetailDto detailDto = DetailDto.toDetailDto(detailEntity.get());
            detailDto.setTitle(title);
            detailDto.setStartDay(startDay);
            detailDto.setEndDay(endDay);
            detailDto.setDiscription(discription);
            detailDto.setHeadIdToDetail(headRepository.findById(headId).orElse(null));
            DetailDto editDetailDto = DetailDto.toDetailDto(detailRepository.save(DetailEntity.toDetailEntity(detailDto)));
            return editDetailDto;
        }
        return null;
    }

    public WorkDto editWork(String title, String startDate, String deadline, String discription,
                            Long workId, Long linkedDetailId) {
        Optional<WorkEntity> workEntity = workRepository.findById(workId);
        Optional<DetailEntity> detailEntity = detailRepository.findById(linkedDetailId);
        if(workEntity.isPresent()) {
            Date startDay = dateManager.formatter(startDate);
            Date endDay = dateManager.formatter(deadline);
            workEntity.get().setTitle(title);
            workEntity.get().setStartDay(startDay);
            workEntity.get().setEndDay(endDay);
            workEntity.get().setDiscription(discription);
            workEntity.get().setDetailIdToWork(detailEntity.get());
            WorkDto workDto = WorkDto.toWorkDto(workRepository.save(workEntity.get()));
            return workDto;
        }
        return null;
    }

    /* - - - - 수정 메서드 끝 - - - - - */

    /* - - - - 삭제 메서드 시작 - - - - - */
    @Transactional
    public void deleteProjectEntity(ProjectDto projectDto) {
        List<HeadDto> headDtoList = selectAllHead(projectDto);
        for (HeadDto headDto : headDtoList) {
            deleteHeadEntity(headDto.getHeadId());
        }
        log.info("Head 삭제");
        deleteMessageForProject(projectDto.getProjectId());
        log.info("Message 삭제");
        deleteProjectRequestForProject(projectDto.getProjectId());
        log.info("ProjectRequest 삭제");
        deleteProjectRoleForProject(projectDto.getProjectId());
        log.info("ProjectRole 삭제");
        deleteProjectRequest(projectDto.getProjectId());
        log.info("ProjectRequest 삭제");
        deleteProject(projectDto.getProjectId());
        log.info("Project 삭제");
    }

    @Transactional
    public void deleteHeadEntity(Long headId) {
        //head
        HeadDto targetHeadDto = HeadDto.toHeadDto(headRepository.findById(headId).orElse(null));
        log.info("head 검색 완료, head : " + targetHeadDto.getTitle());
        //하위 detail list
        List<DetailDto> detailDtoList = selectAllDetailForHead(targetHeadDto);
        log.info("detail 검색 완료");
        //하위 work list
        List<WorkDto> workDtoList = new ArrayList<>();
        for (DetailDto detailDto : detailDtoList) {
            List<WorkDto> searchWorkDtoList = selectAllWorkForDetail(detailDto.getDetailId());
            for (WorkDto workDto : searchWorkDtoList) {
                workDtoList.add(workDto);
            }
        }
        log.info("work 검색 완료");
        //하위 userWork list
        List<UserWorkDto> userWorkDtoList = new ArrayList<>();
        for (WorkDto workDto : workDtoList) {
            userWorkDtoList.addAll(selectUserWorkForWorkList(workDto));
        }
        //하위 workComment list
        List<WorkCommentDto> workCommentDtoList = new ArrayList<>();
        for (WorkDto workDto : workDtoList) {
            List<WorkCommentDto> searchWorkCommentDtoList = selectAllWorkCommentForWork(workDto);
            for (WorkCommentDto workCommentDto : searchWorkCommentDtoList) {
                workCommentDtoList.add(workCommentDto);
            }
        }
        log.info("workComment 검색 완료");
        //하위 workDocument list
        List<WorkDocumentDto> workDocumentDtoList = new ArrayList<>();
        for (WorkDto workDto : workDtoList) {
            List<WorkDocumentDto> searchWorkDocumentDtoList = selectAllWorkDocumentForWork(workDto);
            for (WorkDocumentDto workDocumentDto : searchWorkDocumentDtoList) {
                workDocumentDtoList.add(workDocumentDto);
            }
        }
        log.info("workDocument 검색 완료");
        //하위 document list
        List<DocumentDto> documentList = selectAllDocumentForWorkList(workDtoList);
        //하위 log list
        List<LogDto> logDtoList = new ArrayList<>();
        for (DocumentDto documentDto : documentList) {
            List<LogDto> searchLogDtoList = selectAllLogForDocument(documentDto);
            for (LogDto logDto : searchLogDtoList) {
                logDtoList.add(logDto);
            }
        }
        log.info("document 검색 완료");

        //log, block delete
        for (DocumentDto documentDto : documentList) {
            deleteLogForDocument(documentDto);
            log.info("log 삭제 완료");
            deleteBlockForDocument(documentDto);
            log.info("block 삭제 완료");
        }
        //하위 workDocument 삭제
        deleteWorkDocumentList(workDocumentDtoList);
        log.info("workDocument 삭제 완료");
        //하위 document 삭제
        deleteDocumentList(documentList);
        log.info("document 삭제 완료");
        //하위 userWork 삭제
        deleteUserWorkList(userWorkDtoList);
        log.info("userWork 삭제 완료");
        //하위 work 삭제
        deleteWorkList(workDtoList);
        log.info("work 삭제 완료");
        //하위 detail 삭제
        deleteDetailList(detailDtoList);
        log.info("detail 삭제 완료");
        //head 삭제
        headRepository.deleteById(targetHeadDto.getHeadId());
        log.info("head 삭제 완료");
    }

    @Transactional
    public void deleteDetailEntity(Long detailId) {
        //detail
        DetailDto detailDto = DetailDto.toDetailDto(detailRepository.findById(detailId).orElse(null));
        //workDto 변환
        List<WorkEntity> workEntityList = workRepository.findAllByDetailIdToWork_DetailId(detailId);
        List<WorkDto> workDtoList = new ArrayList<>();
        for (WorkEntity workEntity : workEntityList) {
            workDtoList.add(WorkDto.toWorkDto(workEntity));
        }
        log.info("work 검색 완료");
        //하위 userWork list
        List<UserWorkDto> userWorkDtoList = new ArrayList<>();
        for (WorkDto workDto : workDtoList) {
            userWorkDtoList.addAll(selectUserWorkForWorkList(workDto));
        }
        log.info("userWork 검색 완료");
        //하위 workComment list
        List<WorkCommentDto> workCommentDtoList = new ArrayList<>();
        for (WorkDto workDto : workDtoList) {
            List<WorkCommentDto> searchWorkCommentDtoList = selectAllWorkCommentForWork(workDto);
            for (WorkCommentDto workCommentDto : searchWorkCommentDtoList) {
                workCommentDtoList.add(workCommentDto);
            }
        }
        log.info("workComment 검색 완료");
        //하위 workDocument list
        List<WorkDocumentDto> workDocumentDtoList = new ArrayList<>();
        for (WorkDto workDto : workDtoList) {
            List<WorkDocumentDto> searchWorkDocumentDtoList = selectAllWorkDocumentForWork(workDto);
            for (WorkDocumentDto workDocumentDto : searchWorkDocumentDtoList) {
                workDocumentDtoList.add(workDocumentDto);
            }
        }
        log.info("workDocument 검색 완료");
        //하위 document list
        List<DocumentDto> documentList = selectAllDocumentForWorkList(workDtoList);
        //하위 log list
        List<LogDto> logDtoList = new ArrayList<>();
        for (DocumentDto documentDto : documentList) {
            List<LogDto> searchLogDtoList = selectAllLogForDocument(documentDto);
            for (LogDto logDto : searchLogDtoList) {
                logDtoList.add(logDto);
            }
        }
        log.info("document 검색 완료");

        //log, block delete
        for (DocumentDto documentDto : documentList) {
            deleteLogForDocument(documentDto);
            log.info("log 삭제 완료");
            deleteBlockForDocument(documentDto);
            log.info("block 삭제 완료");
        }
        //하위 workDocument 삭제
        deleteWorkDocumentList(workDocumentDtoList);
        log.info("workDocument 삭제 완료");
        //하위 document 삭제
        deleteDocumentList(documentList);
        log.info("document 삭제 완료");
        //하위 userWork 삭제
        deleteUserWorkList(userWorkDtoList);
        log.info("userWork 삭제 완료");
        //하위 work 삭제
        deleteWorkList(workDtoList);
        log.info("work 삭제 완료");
        //detail 삭제
        deleteDetail(detailDto);
        log.info("detail 삭제 완료");
    }

    @Transactional
    public void deleteWorkEntity(Long workId) {
        // work
        WorkDto workDto = selectWork(workId);

        List<UserWorkDto> userWorkDtoList = selectAllUserWorkForWork(workId);
        List<WorkCommentDto> workCommentDtoList = selectAllWorkCommentForWork(workDto);
        List<WorkDocumentDto> workDocumentDtoList = selectAllWorkDocumentForWork(workDto);
        List<DocumentDto> documentDtoList = new ArrayList<>();
        for (WorkDocumentDto workDocumentDto : workDocumentDtoList) {
            DocumentDto documentDto = new DocumentDto();
            documentDto.insertEntity(workDocumentDto.getDocumentIdToWorkDocument());
            documentDtoList.add(documentDto);
        }

        for (DocumentDto documentDto : documentDtoList) {
            deleteLogForDocument(documentDto);
            log.info("log 삭제 완료");
            deleteBlockForDocument(documentDto);
            log.info("block 삭제 완료");
        }
        deleteWorkDocumentList(workDocumentDtoList);
        log.info("workDocument 삭제 완료");
        deleteDocumentList(documentDtoList);
        log.info("document 삭제 완료");
        deleteAllWorkCommentForWorkId(workId);
        log.info("WorkComment 삭제 완료");
        deleteAllUserWorkForWork(workId);
        log.info("userWork 삭제 완료");
        deleteWork(workId);
        log.info("work 삭제 완료");
    }

    public void deleteProject(Long projectId) {
        projectRepository.deleteByProjectId(projectId);
    }
    public void deleteDetail(DetailDto detailDto) {
        detailRepository.deleteByDetailId(detailDto.getDetailId());
    }
    public void deleteWork(Long workId) {
        workRepository.deleteAllByWorkId(workId);
    }

    public void deleteProjectRequest(Long projectId) { projectRequestRepository.deleteAllByProjectIdToRequest_ProjectId(projectId); }

    public void deleteDetailList(List<DetailDto> detailDtoList) {
        for (DetailDto detailDto : detailDtoList) {
            detailRepository.deleteAllByDetailId(detailDto.getDetailId());
        }
    }

    public void deleteWorkList(List<WorkDto> workDtoList) {
        for (WorkDto workDto : workDtoList) {
            workRepository.deleteAllByWorkId(workDto.getWorkId());
        }
    }

    public void deleteUserWorkList(List<UserWorkDto> userWorkDtoList) {
        for (UserWorkDto userWorkDto : userWorkDtoList) {
            userWorkRepository.deleteAllByWorkIdToUserWork_WorkId(userWorkDto.getWorkIdToUserWork().getWorkId());
        }
    }

    public void deleteAllUserWorkForWork(Long workId) {
        userWorkRepository.deleteAllByWorkIdToUserWork_WorkId(workId);
    }

    public void deleteAllWorkCommentForWorkId(Long workId) {
        workCommentRepository.deleteAllByWorkIdToComment_WorkId(workId);
    }

    public void deleteWorkDocumentList(List<WorkDocumentDto> workDocumentDtoList) {
        for (WorkDocumentDto workDocumentDto : workDocumentDtoList) {
            workDocumentRepository.deleteAllByWorkIdToWorkDocument_WorkId(
                    workDocumentDto.getWorkIdToWorkDocument().getWorkId());
        }
    }

    public void deleteDocumentList(List<DocumentDto> documentDtoList) {
        for (DocumentDto documentDto : documentDtoList) {
            documentRepository.deleteById(documentDto.getDocumentId());
        }
    }

    public void deleteBlockForDocument(DocumentDto documentDto) {
        blockRepository.deleteAllByDocumentId(documentDto.getDocumentId());
    }

    public void deleteLogForDocument(DocumentDto documentDto) {
        logRepository.deleteAllByDocumentId(documentDto.getDocumentId());
    }

    public void deleteMessageForProject(Long projectId) {
        messageRepository.deleteAllByProjectIdToMessage_ProjectId(projectId);
    }

    public void deleteProjectRequestForProject(Long projectId) {
        projectRequestRepository.deleteAllByProjectIdToRequest_ProjectId(projectId);
    }

    public void deleteProjectRoleForProject(Long projectId) {
        projectRoleRepository.deleteAllByProjectIdInRole_ProjectId(projectId);
    }


    /* - - - - 삭제 메서드 끝 - - - - - */

    /* - - - - 상태 변경 메서드 - - - -  */
    // work 상태 변경 메서드
    public WorkDto workCompletionChange(WorkDto workDto) {
        log.info("workCompletionChange 호출");
        WorkDto changeWorkDto;
        if (workDto.getCompletion() == 0) {
            workDto.setCompletion(1);
            changeWorkDto = updateWork(workDto);
            log.info("완료 상태로 변경");
            checkWorkCompletion(workDto);
            return changeWorkDto;
        } else if (workDto.getCompletion() == 1) {
            workDto.setCompletion(0);
            changeWorkDto = updateWork(workDto);
            log.info("미완료 상태로 변경");
            checkWorkCompletion(workDto);
            return changeWorkDto;
        }
        // work 상태를 검사하여 상위 detail 상태 자동 수정
        return null;
    }

    public DetailDto detailCompletionChange(DetailDto detailDto) {
        DetailDto changeDetailDto;
        if (detailDto.getCompletion() == 0) {
            detailDto.setCompletion(1);
            changeDetailDto = updateDetail(detailDto);
            checkDetailCompletion(detailDto);
            return changeDetailDto;
        } else if (detailDto.getCompletion() == 1) {
            detailDto.setCompletion(0);
            changeDetailDto = updateDetail(detailDto);
            checkDetailCompletion(detailDto);
            return changeDetailDto;
        }
        return null;
    }

    public HeadDto headCompletionChange(HeadDto headDto) {
        HeadDto changeHeadDto;
        if (headDto.getCompletion() == 0) {
            headDto.setCompletion(1);
            changeHeadDto = updateHead(headDto);
            return changeHeadDto;
        } else if (headDto.getCompletion() == 1) {
            headDto.setCompletion(0);
            changeHeadDto = updateHead(headDto);
            return changeHeadDto;
        }
        return null;
    }

    // work 모두 완료시, 자동 완료. 미완료시 자동 미완료 처리
    public void checkWorkCompletion(WorkDto workDto) {
        log.info("checkWorkCompletion() 실행");
        Optional<DetailEntity> detailEntity = detailRepository.findById(workDto.getDetailIdToWork().getDetailId());
        log.info("상위 detail 확인 완료. title : " + detailEntity.get().getTitle());
        List<WorkEntity> workEntityList = workRepository.findAllByDetailIdToWork_DetailId(detailEntity.get().getDetailId());
        boolean allComplete = true;
        for (WorkEntity workEntity : workEntityList) {
            if (workEntity.getCompletion() == 0) {
                log.info("미완 상태의 work 발견");
                allComplete = false;
            }
        }

        DetailDto detailDto = DetailDto.toDetailDto(detailEntity.get());

        if (allComplete) {
            log.info("모든 work 완료 확인됨");
            detailDto.setCompletion(1);
            updateDetail(detailDto);
            log.info("상위 detail 완료 상태로 자동 수정");
            checkDetailCompletion(detailDto);
        } else if (!allComplete) {
            log.info("미완 상태의 work 확인됨");
            detailDto.setCompletion(0);
            log.info("상위 detail 미완 상태로 자동 수정");
            updateDetail(detailDto);
            checkDetailCompletion(detailDto);
        }
    }

    public void checkDetailCompletion(DetailDto detailDto) {
        log.info("detail 상태 확인을 위한 checkDetailCompletion() 실행");
        Optional<HeadEntity> headEntity = headRepository.findById(detailDto.getHeadIdToDetail().getHeadId());
        log.info("상위 head 확인 완료. title : " + headEntity.get().getTitle());
        List<DetailEntity> detailEntityList = detailRepository.findAllByHeadIdToDetail_HeadId(headEntity.get().getHeadId());
        boolean allComplete = true;
        for (DetailEntity detailEntity : detailEntityList) {
            if (detailEntity.getCompletion() == 0) {
                log.info("미완 상태의 head 발견");
                allComplete = false;
            }
        }

        HeadDto headDto = HeadDto.toHeadDto(headEntity.get());

        if (allComplete) {
            log.info("모든 detail 완료 확인됨");
            headDto.setCompletion(1);
            updateHead(headDto);
            log.info("상위 head 완료 상태로 자동 수정");
        } else if (!allComplete) {
            log.info("미완 상태의 detail 식별됨");
            headDto.setCompletion(0);
            updateHead(headDto);
            log.info("상위 head 미완 상태로 자동 수정");
        }
    }

    /* - - - - 댓글 기능 - - - - - */

    //댓글 기능 (댓글리스트 불러오기)
    public List<WorkCommentDto> findByComment(Long workId) {
        List<WorkCommentEntity> entityList = workCommentRepository.findAllByWorkIdToComment_WorkId(workId);
        if (entityList.isEmpty()) {
            log.info("해당 문서에 댓글 없음 (서비스)");
            List<WorkCommentDto> commentDtoList = new ArrayList<>();
            return commentDtoList;
        } else {
            List<WorkCommentDto> commentDtoList = new ArrayList<>();
            for (WorkCommentEntity commentEntity : entityList) {
                commentDtoList.add(WorkCommentDto.toWorkCommentDto(commentEntity));
            }
            log.info("댓글 리스트 불러오기 성공(서비스)");
            return commentDtoList;
        }
    }

    //댓글기능 (댓글 추가)
    public List<WorkCommentDto> plusComment(WorkCommentDto workCommentDto, Long workId) {
        if (workCommentDto.equals(null)) {
            log.info("코멘트가 비어있음 (서비스)");
            return null;
        } else {
            workCommentRepository.save(WorkCommentDto.toWorkCommentEntity(workCommentDto));
            return findByComment(workId);
        }
    }

    //update를 위한 Comment Find
    public WorkCommentDto findComment(Long commentId) {
        Optional<WorkCommentEntity> documentCommentEntity = workCommentRepository.findById(commentId);
        return WorkCommentDto.toWorkCommentDto(documentCommentEntity.get());
    }


    //댓글 삭제
    public List<WorkCommentDto> deleteComment(Long commentId, Long workId) {
        Optional<WorkCommentEntity> now = workCommentRepository.findById(commentId);
        WorkCommentDto workCommentDto = WorkCommentDto.toWorkCommentDto(now.get());
        workCommentRepository.deleteById(commentId);
        return findByComment(workId);
    }


    /* - - - - 댓글 기능 끝- - - - - */
}

