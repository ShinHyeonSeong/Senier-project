package com.example.bpm.controller;

import com.example.bpm.dto.document.*;
import com.example.bpm.dto.project.HeadDto;
import com.example.bpm.dto.project.ProjectDto;
import com.example.bpm.dto.project.WorkDto;
import com.example.bpm.dto.project.relation.ProjectDocumentListDto;
import com.example.bpm.dto.user.UserDto;
import com.example.bpm.service.DocumentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.example.bpm.service.ProjectDetailSerivce;
import com.example.bpm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class DocumentController {

    // 서비스 AutoWired
    @Autowired
    private DocumentService documentService;
    @Autowired
    private ProjectDetailSerivce projectDetailSerivce;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpSession session;

    // 생각을 해보니 말야 매번 세션 호출하는것보다는 그냥 따로 메서드 만드는게 훨씬 효율이 좋을듯. 편하기도 하고

    public UserDto getSessionUser() {
        UserDto currentUser = (UserDto) session.getAttribute("userInfo");
        return currentUser;
    }

    public ProjectDto getSessionProject() {
        ProjectDto currentProject = (ProjectDto) session.getAttribute("currentProject");
        return currentProject;
    }

    public Long getSessionAuth() {
        Long auth = (Long) session.getAttribute("auth");
        return auth;
    }

    //////////////////////////////////////////////////////////////////
    // 페이지 연결
    //////////////////////////////////////////////////////////////////

    // 문서 리스트 Document List
    /// 문서 리스트 관련 페이지 연결
    @GetMapping("/project/document")
    public String getDocumentList(Model model, HttpSession session){

        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");

        ProjectDto projectDto = (ProjectDto) session.getAttribute("currentProject");

        String userUuid = sessionUser.getUuid();
        Long projectId = projectDto.getProjectId();

        List<DocumentInfoDto> documentInfoDtoList = new ArrayList<>();
        List<DocumentDto> documentDtoList = documentService.findDocumentListByProjectId(projectId);

        for (DocumentDto documentDto: documentDtoList) {
            DocumentInfoDto documentInfoDto = new DocumentInfoDto();
            documentInfoDto.setDocumentDto(documentDto);

            WorkDto workDto = projectDetailSerivce.findWorkByDocument(documentDto);

            documentInfoDto.setWorkDto(workDto);

            HeadDto headDto = new HeadDto();
            headDto.insertEntity(workDto.getHeadIdToWork());

            documentInfoDto.setHeadDto(headDto);

            documentInfoDtoList.add(documentInfoDto);
        }
        List<UserDto> userDtoList = userService.findUserListByProjectId(getSessionProject().getProjectId());
        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("currentProject", getSessionProject());
        model.addAttribute("auth", getSessionAuth());
        model.addAttribute("documentList", documentInfoDtoList);


        return "documentList";
    }

    // 문서 새로 만들기 Document Add [Post]
    /// 새로운 문서를 만드는 작업<input th:hidden="true" th:name="workId" th:value="${workDto.getWorkId()}"/>
    @GetMapping("document/addDocument")
    public String postAddingDocument(@RequestParam("workId")Long workId , HttpSession session){
        log.info("문서 추가" + workId);
        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");

        String userUuid = sessionUser.getUuid();
        String userName = sessionUser.getName();
        String documentId = documentService.createDocument(userUuid, userName);

        documentService.workDocumentAdd(workId, documentId);

        return "redirect:/document/write?id=" + documentId;
    }

    @PostMapping("document/delete")
    public String deleteDocument(@RequestBody String id){

        log.info("문서 삭제 메서드 실행");
        log.info("DocumentId = " + id);
        documentService.deleteDocument(id);
        log.info("문서 삭제 완료");

        return "redirect:"+session.getAttribute("back");
    }

    // 문서 작성 Document write
    /// 문서 작성 페이지 이동
    @GetMapping("document/write")
    public String getDocumentWrite(@RequestParam("id")String id, Model model, HttpSession session, HttpServletRequest request) {

        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");

        String referer = request.getHeader("Referer");

        if (!referer.contains("document/history")) {
            session.setAttribute("back", referer);
        }

        String userUuid = sessionUser.getUuid();

        if(documentService.accreditUserToWork(userUuid, id, getSessionAuth())){
            log.info("해당 유저에게 수정 권한이 없음");
            return "redirect:/document/view?id="+id;
        }

        DocumentDto documentDto = documentService.findDocumentById(id);
        List<BlockDto> blockDtoList = documentService.findBlockListByDocumentId(id);

        model.addAttribute("document", documentDto);
        model.addAttribute("blockList", blockDtoList);
        model.addAttribute("back", session.getAttribute("back"));

        return "documentWrite";
    }

    // 문서 뷰 Document view
    /// 문서 작성 페이지 이동
    @GetMapping("document/view")
    public String getDocumentView(String id, Model model, HttpSession session) {

        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");
        String userUuid = sessionUser.getUuid();

        DocumentDto documentDto = documentService.findDocumentById(id);
        List<BlockDto> blockDtoList = documentService.findBlockListByDocumentId(id);

        model.addAttribute("document", documentDto);
        model.addAttribute("blockList", blockDtoList);

        return "documentDetail";
    }

    @GetMapping("document/work/view")
    public String getWorkDocumentView(long id, Model model, HttpSession session) {

        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");
        String userUuid = sessionUser.getUuid();

        List<DocumentDto>  documentDtoList = documentService.findDocumentByWorkId(id);

        List<DocumentListDto> documentListDtoList = new ArrayList<>();

        for (DocumentDto documentDto : documentDtoList) {
            DocumentListDto documentListDto = new DocumentListDto();
            documentListDto.setDocumentDto(documentDto);
            documentListDto.setBlockDtoList(documentService.findBlockListByDocumentId(documentDto.getDocumentId()));

            documentListDtoList.add(documentListDto);
        }

        model.addAttribute("document", documentListDtoList);

        return "documentView";
    }


    // 로그 페이지
    /// 헤당 문서의 로그 페이지 이동
    // 로그 페이지
    /// 헤당 문서의 로그 페이지 이동
    @GetMapping("document/history")
    public String getDocumentLog(String id, Model model, HttpSession session) {
        List<LogDto> logDtoList = documentService.findLogListByDocumentId(id);
        model.addAttribute("logList", logDtoList);
        model.addAttribute("projectId", id);
        return "documentLog";
    }

    @PostMapping("document/changeLogData")
    public String postDocumentReturnLog(String id, HttpSession session) {

        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");

        String userName = sessionUser.getName();

        String documentId = documentService.changeLogData(id, userName);
        return "redirect:/document/write?id=" + documentId;
    }


}
