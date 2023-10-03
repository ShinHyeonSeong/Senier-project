package com.example.bpm.controller;

import com.example.bpm.dto.*;
import com.example.bpm.entity.Document;
import com.example.bpm.entity.UserEntity;
import com.example.bpm.service.DocumentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DocumentController {

    // 서비스 AutoWired
    @Autowired
    private DocumentService documentService;
    @Autowired
    private HttpSession session;

    // 생각을 해보니 말야 매번 세션 호출하는것보다는 그냥 따로 메서드 만드는게 훨씬 효율이 좋을듯. 편하기도 하고

    public UserDto getSessionUser() {
        UserDto currentUser = (UserDto) session.getAttribute("userInfo");
        return currentUser;
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

        List<DocumentDto> documentDtoList = documentService.getDocumentListByUserAndProjectId(userUuid, projectId);

        List<ProjectDocumentListDto> projectDocumentList = documentService.getDocumentListByProjectId(projectId);

        // 유저가 권한을 가지는 문서들
        model.addAttribute("UserDocumentList", documentDtoList);

        // 현재 프로젝트 문서들
        model.addAttribute("projectDocumentList", projectDocumentList);


        return "documentList";
    }

    // 문서 새로 만들기 Document Add [Post]
    /// 새로운 문서를 만드는 작업
    @PostMapping("document/addDocument")
    public String postAddingDocument(long workId , HttpSession session){

        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");

        String userUuid = sessionUser.getUuid();
        String userName = sessionUser.getName();
        String documentId = documentService.documentAdding(userUuid, userName);

        documentService.workDocumentAdd(workId, documentId);

        return "redirect:/document/write?id=" + documentId;
    }

    @PostMapping("document/delete")
    public String deleteDocument(String id){

        documentService.deleteDocument(id);

        return "redirect:"+session.getAttribute("back");
    }

    // 문서 작성 Document write
    /// 문서 작성 페이지 이동
    @GetMapping("document/write")
    public String getDocumentWrite(String id, Model model, HttpSession session, HttpServletRequest request) {

        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");

        String referer = request.getHeader("Referer");

        if (!referer.contains("document/history")) {
            session.setAttribute("back", referer);
        }

        String userUuid = sessionUser.getUuid();

        if(documentService.accreditUserToWork(userUuid, id, getSessionAuth())){
            return "redirect:/document/view?id="+id;
        }

        DocumentDto documentDto = documentService.getDocumentById(id);
        List<BlockDto> blockDtoList = documentService.getBlockListByDocumentId(id);

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

        DocumentDto documentDto = documentService.getDocumentById(id);
        List<BlockDto> blockDtoList = documentService.getBlockListByDocumentId(id);

        model.addAttribute("document", documentDto);
        model.addAttribute("blockList", blockDtoList);

        return "documentDetail";
    }


    // 로그 페이지
    /// 헤당 문서의 로그 페이지 이동
    // 로그 페이지
    /// 헤당 문서의 로그 페이지 이동
    @GetMapping("document/history")
    public String getDocumentLog(String id, Model model, HttpSession session) {
        List<LogDto> logDtoList = documentService.getLogListById(id);
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
