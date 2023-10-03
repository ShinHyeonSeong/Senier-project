package com.example.bpm.service;

import com.example.bpm.dto.*;
import com.example.bpm.entity.*;
import com.example.bpm.repository.*;
import com.example.bpm.service.dateLogic.DateManager;
import com.example.bpm.service.logLogic.LogManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class DocumentService {

    // 필드
    private String bucketName = "bpm-file-storage";

    // 레파지토리 AutoWired

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserWorkRepository userWorkRepository;

    @Autowired
    private WorkDocumentRepository workDocumentRepository;

    @Autowired
    private WorkRepository workRepository;

    // 기타 비지니스 클래스

    private LogManager logManager = new LogManager();

    private DateManager dateManager = new DateManager();

    //////////////////////////////////////////////////////////////////
    // 서비스 로직
    //////////////////////////////////////////////////////////////////

    // 새로운 문서 만들기
    /// 해당 함수를 호출하면 새로운 문서를 만들고 해당 문서의 id 를 반환함
    public String documentAdding(String userUuid, String userName){
        DocumentDto documentDto = new DocumentDto();

        UUID uuid = UUID.randomUUID();

        documentDto.setDocumentId(uuid.toString());
        documentDto.setTitle("제목 없음");
        documentDto.setDateDocument(dateManager.DocumentTime());

        /// 유저 uuid 저장
        documentDto.setUuid(userUuid);

        documentDto.setUserName(userName);

        documentRepository.save(documentDto.toEntity());

        return documentDto.getDocumentId();
    }

    // 문서 제거
    public void deleteDocument(String documentId){
        List<Block> deleteBlockList = blockRepository.findByDocumentId(documentId);
        List<Log> deleteLogList = logRepository.findByDocumentId(documentId);

        workDocumentRepository.deleteAllByDocumentIdToWorkDocument_DocumentId(documentId);

        for (Block block : deleteBlockList) {
            blockRepository.delete(block);
        }

        for (Log log : deleteLogList) {
            logRepository.delete(log);
        }

        documentRepository.deleteById(documentId);
    }

    // 문서 저장
    public void saveDocument(JsonDocumentDto jsonDocumentDto, String userUuid, String userName){

        Document document = jsonDocumentDto.documentEntityOut();
        document.setDateDocument(dateManager.DocumentTime());

        document.setUuid(userUuid);

        document.setUserName(userName);

        documentRepository.save(document);

        List<Block> deleteBlockList = blockRepository.findByDocumentId(jsonDocumentDto.getId());
        List<Block> addBlockList = jsonDocumentDto.blockEntityOut();

        blockChange(deleteBlockList, addBlockList);

        logReturn(document, addBlockList, userName + "- Save Document");
    }

    // 로그 데이터로 현재 데이터 교체
    public String changeLogData(String id, String userName){
        Log log = getLogById(id);

        String[] logDocument = log.getLog().split("\\]");
        Document document = logManager.deserializeDocument(logDocument[0]);

        String[] logBlock = logDocument[1].split("\\[");
        List<Block> deleteBlockList = blockRepository.findByDocumentId(log.getDocumentId());
        List<Block> addBlockList = new ArrayList<>();

        for (String blockLog : logBlock) {
            Block block = logManager.deserializeblock(blockLog);
            addBlockList.add(block);
        }

        documentRepository.save(document);

        blockChange(deleteBlockList, addBlockList);

        logReturn(document, addBlockList, userName + "- Chage Log Data<br>" + log.getDateLog());

        return document.getDocumentId();
    }

    // 파일 저장
    public String saveFile(MultipartFile file) throws IOException{

        String uuid = UUID.randomUUID().toString();
        String ext = file.getContentType();

        InputStream keyFile = ResourceUtils.getURL("classpath:" + "oceanic-will-385316-249d7b5e0f68.json").openStream();

        Storage storage = StorageOptions.newBuilder().setProjectId("oceanic-will-385316")
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build().getService();

        Blob blobInfo = storage.create(
                BlobInfo.newBuilder(bucketName, uuid)
                        .setContentType(ext)
                        .build(),
                file.getInputStream()
        );

        return "https://storage.cloud.google.com/bpm-file-storage/"+uuid;
    }


    //work_document 연결
    public void workDocumentAdd(Long workId, String documentId){
        WorkDocumentEntity workDocumentEntity = new WorkDocumentEntity();
        workDocumentEntity.setWorkIdToWorkDocument(workRepository.findByWorkId(workId));
        workDocumentEntity.setDocumentIdToWorkDocument(documentRepository.findByDocumentId(documentId));

        workDocumentRepository.save(workDocumentEntity);
    }

    public boolean accreditUserToWork(String uuid, String DocumentId, Long auth){

        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByUserIdToUserWork_Uuid(uuid);

        WorkDocumentEntity workDocumentEntity = workDocumentRepository.findByDocumentIdToWorkDocument_DocumentId(DocumentId);

        if (auth == 0){
            return false;
        }
        for (UserWorkEntity userWorkEntity: userWorkEntityList) {
            if (userWorkEntity.getWorkIdToUserWork().getWorkId().equals(workDocumentEntity.getWorkIdToWorkDocument().getWorkId()))
                return false;
        }

        return true;

    }

    //////////////////////////////////////////////////////////////////
    // 내부 함수
    //////////////////////////////////////////////////////////////////

    public void blockChange(List<Block> deleteBlockList, List<Block> addBlockList) {
        for (Block block : deleteBlockList) {
            blockRepository.delete(block);
        }

        for (Block block : addBlockList) {
            blockRepository.save(block);
        }
    }

    public void logReturn(Document document, List<Block> blockList, String logType) {
        String logString = "";

        logString += logManager.changeDocumentToString(document) + "]";

        for (Block block: blockList) {
            logString += logManager.changeBlockToString(block) + "[";
        }

        Log log = new Log();

        UUID uuid = UUID.randomUUID();

        log.setLogId(uuid.toString());
        log.setDocumentId(document.getDocumentId());
        log.setLog(logString);
        log.setDateLog(dateManager.logTime());
        log.setLogType(logType);

        logRepository.save(log);
    }

    //////////////////////////////////////////////////////////////////
    // 데이터 받아오기
    //////////////////////////////////////////////////////////////////

    /* Document DocumentDto */

    // 문서 전체 받아오기
    public List<DocumentDto> getDocumentList() {
        List<Document> documentList = documentRepository.findAll();
        List<DocumentDto> documentDtoList = new ArrayList<>();

        for (Document document:
                documentList)
        {
            DocumentDto documentDto = new DocumentDto();
            documentDto.insertEntity(document);
            documentDtoList.add(documentDto);
        }

        return documentDtoList;
    }

    // 유저 기준으로 문서 리스트 받아오기
    public List<DocumentDto> getDocumentListByUser(String userUuid){
        List<Document> documentList = new ArrayList<>();
        List<DocumentDto> documentDtoList = new ArrayList<>();

        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByUserIdToUserWork_Uuid(userUuid);

        for (UserWorkEntity userWorkEntity : userWorkEntityList) {
            List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(userWorkEntity.getWorkIdToUserWork().getWorkId());
            for (WorkDocumentEntity workDocumentEntity : workDocumentEntityList) {
                documentList.add(documentRepository.findByDocumentId(workDocumentEntity.getDocumentIdToWorkDocument().getDocumentId()));
            }
        }

        for (Document document : documentList)
        {
            DocumentDto documentDto = new DocumentDto();
            documentDto.insertEntity(document);
            documentDtoList.add(documentDto);
        }

        return documentDtoList;
    }

    // 유저 및 프로젝트 아이디 기준으로 문서 리스트 받아오기
    public  List<DocumentDto> getDocumentListByUserAndProjectId(String userUuid, Long id) {
        List<DocumentDto> documentDtoList = new ArrayList<>();

        List<WorkEntity> workEntityList = workRepository.findAllByProjectIdToWork_ProjectId(id);
        List<WorkEntity> workUserEntityList = new ArrayList<>();

        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByUserIdToUserWork_Uuid(userUuid);

        for (WorkEntity workEntity : workEntityList) {
            for (UserWorkEntity userWorkEntity : userWorkEntityList) {
                if (workEntity.getWorkId() == userWorkEntity.getWorkIdToUserWork().getWorkId()){
                    workUserEntityList.add(workEntity);
                    break;
                }
            }
        }

        for (WorkEntity workEntity: workUserEntityList) {
            documentDtoList.addAll(getDocumentByWorkId(workEntity.getWorkId()));
        }

        return documentDtoList;
    }

    // 문서 아이디 기준으로 문서 받아오기
    public DocumentDto getDocumentById(String id) {
        Document document = documentRepository.findByDocumentId(id);
        DocumentDto documentDto = new DocumentDto();
        documentDto.insertEntity(document);

        return documentDto;
    }

    // work 기준으로 문서 받아오기
    public List<DocumentDto> getDocumentByWorkId(Long id){
        List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(id);

        List<Document> documentList = new ArrayList<>();
        List<DocumentDto> documentDtoList = new ArrayList<>();

        for (WorkDocumentEntity workDocumentEntity: workDocumentEntityList) {
            documentList.add(workDocumentEntity.getDocumentIdToWorkDocument());
        }


        for (Document document : documentList)
        {
            DocumentDto documentDto = new DocumentDto();
            documentDto.insertEntity(document);
            documentDtoList.add(documentDto);
        }

        return documentDtoList;
    }

    //프로젝트 전체 문서 받아오기
    public List<ProjectDocumentListDto> getDocumentListByProjectId(Long id){
        List<ProjectDocumentListDto> projectDocumentList = new ArrayList<>();
        List<WorkEntity> workEntityList = workRepository.findAllByProjectIdToWork_ProjectId(id);

        for (WorkEntity workEntity: workEntityList) {
            ProjectDocumentListDto projectDocumentListDto = new ProjectDocumentListDto();

            projectDocumentListDto.setWorkName(workEntity.getTitle());

            List<Document> documentList = new ArrayList<>();
            List<DocumentDto> documentDtoList = new ArrayList<>();
            List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(workEntity.getWorkId());

            for (WorkDocumentEntity workDocumentEntity: workDocumentEntityList) {
                documentList.add(documentRepository.findByDocumentId(workDocumentEntity.getDocumentIdToWorkDocument().getDocumentId()));
            }

            for (Document document : documentList)
            {
                DocumentDto documentDto = new DocumentDto();
                documentDto.insertEntity(document);
                documentDtoList.add(documentDto);
            }

            projectDocumentListDto.setDocumentDtoList(documentDtoList);

            projectDocumentList.add(projectDocumentListDto);
        }

        return projectDocumentList;
    }

    /* Block BlockDto */

    // 문서 아이디 기준으로 블럭 리스트 받아오기
    public List<BlockDto> getBlockListByDocumentId(String id) {
        List<Block> blockList = blockRepository.findByDocumentId(id);
        List<BlockDto> blockDtoList = new ArrayList<>();

        for (Block block:
                blockList) {
            BlockDto blockDto = new BlockDto();
            blockDto.insertEntity(block);
            blockDtoList.add(blockDto);
        }

        Collections.sort(blockDtoList);

        return blockDtoList;
    }

    /* Log LogDto */

    // 문서 아이디 기준으로 문서 리스트 불러오기
    public List<LogDto> getLogListById(String id) {
        List<Log> logList = logRepository.findByDocumentId(id);
        List<LogDto> logDtoList = new ArrayList<>();

        for (Log log : logList) {
            LogDto logDto = new LogDto();
            logDto.insertEntity(log);
            logDtoList.add(logDto);
        }

        Collections.sort(logDtoList, Collections.reverseOrder());

        return logDtoList;
    }

    // 로그 아이디로 로그 불러오기 [서비스 내부에서 사용]
    public Log getLogById(String id) {
        return logRepository.findBylogId(id);
    }

}