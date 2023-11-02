package com.example.bpm.service;

import com.example.bpm.dto.document.BlockDto;
import com.example.bpm.dto.document.DocumentDto;
import com.example.bpm.dto.document.LogDto;
import com.example.bpm.dto.document.json.JsonDocumentDto;
import com.example.bpm.dto.project.WorkDto;
import com.example.bpm.dto.project.relation.ProjectDocumentListDto;
import com.example.bpm.entity.document.BlockEntity;
import com.example.bpm.entity.document.DocumentEntity;
import com.example.bpm.entity.document.LogEntity;
import com.example.bpm.entity.project.data.WorkEntity;
import com.example.bpm.entity.project.relation.WorkDocumentEntity;
import com.example.bpm.entity.user.relation.UserWorkEntity;
import com.example.bpm.repository.*;
import com.example.bpm.service.Logic.dateLogic.DateManager;
import com.example.bpm.service.Logic.logLogic.LogManager;
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

import javax.swing.text.Document;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class DocumentService {

    // filed
    private String bucketName = "bpm-file-storage";

    // autoWired
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

    // class

    private LogManager logManager = new LogManager();

    private DateManager dateManager = new DateManager();

    //////////////////////////////////////////////////////////////////
    // document create
    //////////////////////////////////////////////////////////////////

    public String createDocument(String userUuid, String userName){
        DocumentDto documentDto = new DocumentDto();

        UUID uuid = UUID.randomUUID();

        documentDto.setDocumentId(uuid.toString());
        documentDto.setTitle("제목 없음");
        documentDto.setDateDocument(dateManager.DocumentTime());

        /// 유저 uuid 저장
        documentDto.setUuid(userUuid);

        documentDto.setUserName(userName);
        log.info(documentDto.getDocumentId() + documentDto.getTitle() + documentDto.getUuid() + documentDto.getUserName() + documentDto.getDateDocument());
        DocumentEntity documentEntity = documentDto.toEntity();

        documentRepository.save(documentEntity);

        return documentDto.getDocumentId();
    }

    public void createDocumentByTitle(String userUuid, String userName, List<String> documentTitle, WorkDto workDto){
        for (String title : documentTitle) {
            DocumentDto documentDto = new DocumentDto();

            UUID uuid = UUID.randomUUID();

            documentDto.setDocumentId(uuid.toString());
            if (title == null) {
                documentDto.setTitle("제목 없음");
            } else {
                documentDto.setTitle(title);
            }
            documentDto.setDateDocument(dateManager.DocumentTime());

            documentDto.setUuid(userUuid);

            documentDto.setUserName(userName);
            DocumentEntity documentEntity = documentDto.toEntity();

            DocumentEntity createDocumentEntity = documentRepository.save(documentEntity);

            workDocumentAdd(workDto.getWorkId(), createDocumentEntity.getDocumentId());
        }
    }

    public void workDocumentAdd(Long workId, String documentId){
        WorkDocumentEntity workDocumentEntity = new WorkDocumentEntity();
        workDocumentEntity.setWorkIdToWorkDocument(workRepository.findByWorkId(workId));
        workDocumentEntity.setDocumentIdToWorkDocument(documentRepository.findByDocumentId(documentId));

        workDocumentRepository.save(workDocumentEntity);
    }

    //////////////////////////////////////////////////////////////////
    // document update
    //////////////////////////////////////////////////////////////////

    public void saveDocument(JsonDocumentDto jsonDocumentDto, String userUuid, String userName){

        DocumentEntity documentEntity = jsonDocumentDto.documentEntityOut();
        documentEntity.setDateDocument(dateManager.DocumentTime());

        documentEntity.setUuid(userUuid);

        documentEntity.setUserName(userName);

        documentRepository.save(documentEntity);

        List<BlockEntity> deleteBlockListEntity = blockRepository.findByDocumentId(jsonDocumentDto.getId());
        List<BlockEntity> addBlockListEntity = jsonDocumentDto.blockEntityOut();

        blockChange(deleteBlockListEntity, addBlockListEntity);

        logReturn(documentEntity, addBlockListEntity, userName + "- Save Document");
    }

    public String changeLogData(String id, String userName){
        LogEntity logEntity = findLogById(id);

        String[] logDocument = logEntity.getLog().split("\\]");
        DocumentEntity documentEntity = logManager.deserializeDocument(logDocument[0]);

        String[] logBlock = logDocument[1].split("\\[");
        List<BlockEntity> deleteBlockListEntity = blockRepository.findByDocumentId(logEntity.getDocumentId());
        List<BlockEntity> addBlockListEntity = new ArrayList<>();

        for (String blockLog : logBlock) {
            BlockEntity blockEntity = logManager.deserializeblock(blockLog);
            addBlockListEntity.add(blockEntity);
        }

        documentRepository.save(documentEntity);

        blockChange(deleteBlockListEntity, addBlockListEntity);

        logReturn(documentEntity, addBlockListEntity, userName + "- Chage Log Data<br>" + logEntity.getDateLog());

        return documentEntity.getDocumentId();
    }

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

    //////////////////////////////////////////////////////////////////
    // document delate
    //////////////////////////////////////////////////////////////////

    @Transactional
    public void deleteDocument(String documentId){
        List<BlockEntity> deleteBlockListEntity = blockRepository.findByDocumentId(documentId);
        List<LogEntity> deleteLogListEntity = logRepository.findByDocumentId(documentId);

        workDocumentRepository.deleteAllByDocumentIdToWorkDocument_DocumentId(documentId);

        for (BlockEntity blockEntity : deleteBlockListEntity) {
            blockRepository.delete(blockEntity);
        }

        for (LogEntity logEntity : deleteLogListEntity) {
            logRepository.delete(logEntity);
        }

        documentRepository.deleteById(documentId);
    }

    /* check user auth */

    public boolean accreditUserToWork(String uuid, String DocumentId, Long auth){

        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByUserIdToUserWork_Uuid(uuid);

        WorkDocumentEntity workDocumentEntity = workDocumentRepository.findByDocumentIdToWorkDocument_DocumentId(DocumentId);

        if (auth == 1){
            return false;
        }
        if (auth == 2){
            return true;
        }
        for (UserWorkEntity userWorkEntity: userWorkEntityList) {
            if (userWorkEntity.getWorkIdToUserWork().getWorkId().equals(workDocumentEntity.getWorkIdToWorkDocument().getWorkId()))
                return false;
        }

        return true;

    }

    public boolean accreditUserToWork(String uuid, long workId, Long auth){

        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByUserIdToUserWork_Uuid(uuid);


        if (auth == 1){
            return true;
        }
        if (auth == 2){
            return false;
        }
        for (UserWorkEntity userWorkEntity: userWorkEntityList) {
            if (userWorkEntity.getWorkIdToUserWork().getWorkId().equals(workId))
                return true;
        }

        return false;

    }

    //////////////////////////////////////////////////////////////////
    // call service
    //////////////////////////////////////////////////////////////////

    public void blockChange(List<BlockEntity> deleteBlockListEntity, List<BlockEntity> addBlockListEntity) {
        for (BlockEntity blockEntity : deleteBlockListEntity) {
            blockRepository.delete(blockEntity);
        }

        for (BlockEntity blockEntity : addBlockListEntity) {
            blockRepository.save(blockEntity);
        }
    }

    public void logReturn(DocumentEntity documentEntity, List<BlockEntity> blockEntityList, String logType) {
        String logString = "";

        logString += logManager.changeDocumentToString(documentEntity) + "]";

        for (BlockEntity blockEntity : blockEntityList) {
            logString += logManager.changeBlockToString(blockEntity) + "[";
        }

        LogEntity logEntity = new LogEntity();

        UUID uuid = UUID.randomUUID();

        logEntity.setLogId(uuid.toString());
        logEntity.setDocumentId(documentEntity.getDocumentId());
        logEntity.setLog(logString);
        logEntity.setDateLog(dateManager.logTime());
        logEntity.setLogType(logType);

        logRepository.save(logEntity);
    }

    //////////////////////////////////////////////////////////////////
    // find Document
    //////////////////////////////////////////////////////////////////

    /* Document DocumentDto */

    public List<DocumentDto> findDocumentList() {
        List<DocumentEntity> documentEntityList = documentRepository.findAll();
        List<DocumentDto> documentDtoList = new ArrayList<>();

        for (DocumentEntity documentEntity :
                documentEntityList)
        {
            DocumentDto documentDto = new DocumentDto();
            documentDto.insertEntity(documentEntity);
            documentDtoList.add(documentDto);
        }

        return documentDtoList;
    }

    public List<DocumentDto> findDocumentListByUser(String userUuid){
        List<DocumentEntity> documentEntityList = new ArrayList<>();
        List<DocumentDto> documentDtoList = new ArrayList<>();

        List<UserWorkEntity> userWorkEntityList = userWorkRepository.findAllByUserIdToUserWork_Uuid(userUuid);

        for (UserWorkEntity userWorkEntity : userWorkEntityList) {
            List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(userWorkEntity.getWorkIdToUserWork().getWorkId());
            for (WorkDocumentEntity workDocumentEntity : workDocumentEntityList) {
                documentEntityList.add(documentRepository.findByDocumentId(workDocumentEntity.getDocumentIdToWorkDocument().getDocumentId()));
            }
        }

        for (DocumentEntity documentEntity : documentEntityList)
        {
            DocumentDto documentDto = new DocumentDto();
            documentDto.insertEntity(documentEntity);
            documentDtoList.add(documentDto);
        }

        return documentDtoList;
    }

    public  List<DocumentDto> findDocumentListByUserAndProject(String userUuid, Long id) {
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
            documentDtoList.addAll(findDocumentByWorkId(workEntity.getWorkId()));
        }

        return documentDtoList;
    }

    public DocumentDto findDocumentById(String id) {
        DocumentEntity documentEntity = documentRepository.findByDocumentId(id);
        DocumentDto documentDto = new DocumentDto();
        documentDto.insertEntity(documentEntity);

        return documentDto;
    }

    public List<DocumentDto> findDocumentByWorkId(Long id){
        List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(id);

        List<DocumentEntity> documentEntityList = new ArrayList<>();
        List<DocumentDto> documentDtoList = new ArrayList<>();

        for (WorkDocumentEntity workDocumentEntity: workDocumentEntityList) {
            documentEntityList.add(workDocumentEntity.getDocumentIdToWorkDocument());
        }


        for (DocumentEntity documentEntity : documentEntityList)
        {
            DocumentDto documentDto = new DocumentDto();
            documentDto.insertEntity(documentEntity);
            documentDtoList.add(documentDto);
        }

        return documentDtoList;
    }

    public List<DocumentDto> findDocumentListByProjectId(Long id){
        List<DocumentDto> documentDtoList = new ArrayList<>();
        List<WorkEntity> workEntityList = workRepository.findAllByProjectIdToWork_ProjectId(id);

        for (WorkEntity workEntity: workEntityList) {
            for (DocumentDto documentDto: findDocumentByWorkId(workEntity.getWorkId())) {
                documentDtoList.add(documentDto);
            }
        }

        return documentDtoList;
    }

    /* ProjectDocument ProjectDocumentDto */

    public List<ProjectDocumentListDto> findProjectDocumentListByProjectId(Long id){
        List<ProjectDocumentListDto> projectDocumentList = new ArrayList<>();
        List<WorkEntity> workEntityList = workRepository.findAllByProjectIdToWork_ProjectId(id);

        for (WorkEntity workEntity: workEntityList) {
            ProjectDocumentListDto projectDocumentListDto = new ProjectDocumentListDto();

            projectDocumentListDto.setWorkName(workEntity.getTitle());

            List<DocumentEntity> documentEntityList = new ArrayList<>();
            List<DocumentDto> documentDtoList = new ArrayList<>();
            List<WorkDocumentEntity> workDocumentEntityList = workDocumentRepository.findAllByWorkIdToWorkDocument_WorkId(workEntity.getWorkId());

            for (WorkDocumentEntity workDocumentEntity: workDocumentEntityList) {
                documentEntityList.add(documentRepository.findByDocumentId(workDocumentEntity.getDocumentIdToWorkDocument().getDocumentId()));
            }

            for (DocumentEntity documentEntity : documentEntityList)
            {
                DocumentDto documentDto = new DocumentDto();
                documentDto.insertEntity(documentEntity);
                documentDtoList.add(documentDto);
            }

            projectDocumentListDto.setDocumentDtoList(documentDtoList);

            projectDocumentList.add(projectDocumentListDto);
        }

        return projectDocumentList;
    }

    /* Block BlockDto */

    public List<BlockDto> findBlockListByDocumentId(String id) {
        List<BlockEntity> blockEntityList = blockRepository.findByDocumentId(id);
        List<BlockDto> blockDtoList = new ArrayList<>();

        for (BlockEntity blockEntity :
                blockEntityList) {
            BlockDto blockDto = new BlockDto();
            blockDto.insertEntity(blockEntity);
            blockDtoList.add(blockDto);
        }

        Collections.sort(blockDtoList);

        return blockDtoList;
    }

    /* Log LogDto */

    public List<LogDto> findLogListByDocumentId(String id) {
        List<LogEntity> logEntityList = logRepository.findByDocumentId(id);
        List<LogDto> logDtoList = new ArrayList<>();

        for (LogEntity logEntity : logEntityList) {
            LogDto logDto = new LogDto();
            logDto.insertEntity(logEntity);
            logDtoList.add(logDto);
        }

        Collections.sort(logDtoList, Collections.reverseOrder());

        return logDtoList;
    }

    public LogEntity findLogById(String id) {
        return logRepository.findBylogId(id);
    }

}