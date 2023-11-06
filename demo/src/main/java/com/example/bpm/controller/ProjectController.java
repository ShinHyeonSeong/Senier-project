package com.example.bpm.controller;

import com.example.bpm.dto.document.DocumentDto;
import com.example.bpm.dto.project.HeadDto;
import com.example.bpm.dto.project.ProjectDto;
import com.example.bpm.dto.project.WorkDto;
import com.example.bpm.dto.project.relation.ProjectRoleDto;
import com.example.bpm.dto.project.request.ProjectRequestDto;
import com.example.bpm.dto.user.UserDto;
import com.example.bpm.repository.UserRepository;
import com.example.bpm.service.*;
//import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@Builder
public class ProjectController {

    @Autowired
    final private ProjectSerivce projectSerivce;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectDetailSerivce projectDetailSerivce;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private MessageService messageService;

    HttpSession session;

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

    public void checkAuth() {
        ProjectDto projectDto = getSessionProject();
        UserDto userDto = getSessionUser();
        log.info("유저 권한 확인중. 유저명 : " + userDto.getName());
        Long auth = userService.checkRole(projectDto.getProjectId(), userDto.getUuid());
        log.info("권한 검색 종료. 접근 권한 : " + auth);
        session.setAttribute("auth", auth);
    }

    // 관리자 권한 프로젝트 리스트 출력
    @GetMapping("/project/projectManagerList")
    public String getProjectList(HttpSession session, Model model) {
        //세션에서 현재 로그인 되어있는 유저의 정보를 가져온다
        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");
        //UUID를 활용하여 권한자 / 비권한자 프로젝트 리스트를 불러온다
        List<ProjectDto> ManagerToProjectList = projectSerivce.findProjectList(sessionUser.getUuid());
        List<ProjectRoleDto> projectRoleDtoList = new ArrayList<>();

        if (!(ManagerToProjectList == null)) {
            for (ProjectDto projectDto : ManagerToProjectList) {
                projectRoleDtoList.add(projectDetailSerivce.findProjectManager(projectDto.getProjectId()));
            }
        }
        model.addAttribute("user", sessionUser);
        model.addAttribute("managerList", ManagerToProjectList);
        model.addAttribute("projectRoleList", projectRoleDtoList);

        List<ProjectRequestDto> requestDtos = projectSerivce.findAllProjectRequestRecv(sessionUser);
        if (requestDtos.isEmpty()) {
            model.addAttribute("request", false);
        } else model.addAttribute("request", true);
        // 초대 여부 검사 후 프론트에서 시각적 알림 효과 부여

//        List<ProjectRoleDto> ParticipantsToProjectList = projectSerivce.findParticipantsToProjectList(sessionUser.getUuid());
//        if (ManagerToProjectList.isEmpty()) {
//            if (ParticipantsToProjectList.isEmpty()) {
//                log.info("참여중인 리스트가 없음");
//            } else {
//                log.info("비권한자 리스트만 있음");
//            }
//        } else {
//            if (ParticipantsToProjectList.isEmpty()) {
//                log.info("권한자 리스트만 있음");
//            } else {
//                log.info("둘다 리스트 있음");
//            }
//        }
//        //null값이던 데이터가 존재하던 어쨋든 리스트 창을 보여줘야한다. (빈 공백이라도)
//        model.addAttribute("ListToM", ManagerToProjectList);
//        model.addAttribute("ListToP", ParticipantsToProjectList);
        return "projectManagerList";
    }

    // 프로젝트 멤버 권한 리스트 출력
    @GetMapping("/project/projectMemberList")
    public String projectMemberList(HttpSession session, Model model) {
        //세션에서 현재 로그인 되어있는 유저의 정보를 가져온다
        UserDto sessionUser = getSessionUser();
        //UUID를 활용하여 권한자 / 비권한자 프로젝트 리스트를 불러온다
        List<ProjectDto> memberToProjectList = projectSerivce.findProjectListRoleEntry(sessionUser.getUuid());
        model.addAttribute("user", sessionUser);
        model.addAttribute("memberList", memberToProjectList);

        List<ProjectRequestDto> requestDtos = projectSerivce.findAllProjectRequestRecv(sessionUser);
        if (requestDtos.isEmpty()) {
            model.addAttribute("request", false);
        } else model.addAttribute("request", true);
        return "projectMemberList";
    }

    // 전체 프로젝트 리스트 출력
    @GetMapping("/project/projectAllList")
    public String projectAllList(Model model) {
        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");

        List<ProjectDto> AllProjectList = projectSerivce.findProjectListRoleNot(sessionUser.getUuid());
        List<ProjectRoleDto> projectRoleDtoList = new ArrayList<>();
        for (ProjectDto projectDto : AllProjectList) {
            projectRoleDtoList.add(projectDetailSerivce.findProjectManager(projectDto.getProjectId()));
        }

        model.addAttribute("user", sessionUser);
        model.addAttribute("projectRoleList", projectRoleDtoList);
        model.addAttribute("projectAllList", AllProjectList);
        List<ProjectRequestDto> requestDtos = projectSerivce.findAllProjectRequestRecv(sessionUser);
        if (requestDtos.isEmpty()) {
            model.addAttribute("request", false);
        } else model.addAttribute("request", true);
        return "projectAllList";
    }


    @GetMapping("/project/lunch")
    public String lunchProject() {
        return "projectLunch";
    }

    @GetMapping("/project/create")
    public String projectCreate(Model model) {
        UserDto nowUser = getSessionUser();
        model.addAttribute("nowUser", nowUser);
        return "projectCreate";
    }

    @PostMapping("/project/createPage")
    public String createProject(@RequestParam(value = "title") String title,
                                @RequestParam(value = "subtitle") String subtitle,
                                @RequestParam(value = "startDay") String startDay,
                                @RequestParam(value = "endDay") String endDay,
                                Model model,
                                HttpSession session) {
        if (title.equals(null) || startDay.equals(null) || endDay.equals(null)) {
            return "projectCreate";
        } else {
            ProjectDto projectDto = projectSerivce.createProject(title, subtitle, startDay, endDay);
            UserDto sessionUser = getSessionUser();
            session.setAttribute("currentProject", projectDto);
            checkAuth();
            Long auth = getSessionAuth();
            projectSerivce.addRoleManager(projectDto, sessionUser);

            model.addAttribute("projectDto", projectDto);
            return "projectExample";
        }
    }

    @PostMapping("/project/example")
    public String newProjectExample(Model model,
                                    HttpSession session,
                                    @RequestParam(value = "headTitle") String headTitle,
                                    @RequestParam(value = "headDiscription") String headDiscription,
                                    @RequestParam(value = "headStartDay") String headStartDay,
                                    @RequestParam(value = "headDeadline") String headDeadline,

                                    @RequestParam(value = "workTitle") String workTitle,
                                    @RequestParam(value = "workDiscription") String workDiscription,
                                    @RequestParam(value = "workStartDay") String workStartDay,
                                    @RequestParam(value = "workDeadline") String workDeadline,

                                    @RequestParam(value = "documentTitle") List<String> documentTitle) {
        ProjectDto sessionProject = getSessionProject();
        HeadDto createHeadDto = projectDetailSerivce.createHead(headTitle, headStartDay, headDeadline, headDiscription, sessionProject);

        WorkDto createWorkDto = projectDetailSerivce.createWork(workTitle, workDiscription, workStartDay, workDeadline,
                createHeadDto, sessionProject);
        List<String> chargeUsers = new ArrayList<>();
        chargeUsers.add(getSessionUser().getUuid());
        projectDetailSerivce.addUserWork(createWorkDto, chargeUsers);

        documentService.createDocumentByTitle(getSessionUser().getUuid(), getSessionUser().getName(), documentTitle, createWorkDto);

        return "redirect:/project/" + sessionProject.getProjectId();
    }


    // 프로젝트 선택 시 그 프로젝트 정보를 가져오며 프로젝트 창으로 넘어가는 메서드
    // 권한 검색 및 부여 후, 권한에 따라 페이지 리턴.
    @RequestMapping("/project/{id}")
    public String selectProject(@PathVariable("id") Long id, HttpSession session, Model model) {
        UserDto userDto = getSessionUser();
        ProjectDto presentDto = projectSerivce.findProject(id);
        List<UserDto> userDtoList = userService.findUserListByProjectId(id);
        List<HeadDto> headDtoList = projectDetailSerivce.findHeadListByProject(presentDto);

        session.removeAttribute("currentProject");
        session.setAttribute("currentProject", presentDto);
        checkAuth();
        Long auth = getSessionAuth();

        projectDetailSerivce.completionCheckByDate(presentDto);
        List<DocumentDto> documentDtoList = documentService.findDocumentListByProjectId(id);

        // 완료, 미완 헤드 수
        int progressHead = projectDetailSerivce.countProgressHead(headDtoList);
        int completeHead = headDtoList.size() - progressHead;

        // 완료 진척도
        int percentage = projectDetailSerivce.getProjectProgressPercent(presentDto);

        // 안읽은 메세지 식별시 return true
        boolean readState = messageService.checkReadState(getSessionProject().getProjectId(), getSessionUser().getUuid());

        model.addAttribute("per", percentage);
        model.addAttribute("auth", auth);
        model.addAttribute("projectDto", presentDto);
        model.addAttribute("sessionUser", userDto);
        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("documentDtoList", documentDtoList);
        model.addAttribute("headDtoList", headDtoList);
        model.addAttribute("progressHead", progressHead);
        model.addAttribute("completeHead", completeHead);
        model.addAttribute("readState", readState);

        if (getSessionAuth() != 2) {
            List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByProject(presentDto);
            int progressWork = projectDetailSerivce.countProgressWork(workDtoList);
            int completeWork = workDtoList.size() - progressWork;

            model.addAttribute("workDtoList", workDtoList);
            model.addAttribute("progressWork", progressWork);
            model.addAttribute("completeWork", completeWork);
            return "projectMain";
        } else if (getSessionAuth() == 2) {
            List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByProject(presentDto);
            int progressWork = projectDetailSerivce.countProgressWork(workDtoList);
            int completeWork = workDtoList.size() - progressWork;

            model.addAttribute("workDtoList", workDtoList);
            model.addAttribute("progressWork", progressWork);
            model.addAttribute("completeWork", completeWork);

            return "onlyReadPage";
        }
        //권한이 없습니다 알람창 띄우기
        return null;
    }

    //전체 프로젝트 리스트에서 프로젝트 선택 시 해당 소개, 목표,
    @RequestMapping("/projectAll/{id}")
    public String selectAllProject(@PathVariable("id") Long id, HttpSession session, Model model) {
        UserDto userDto = getSessionUser();
        ProjectDto presentDto = projectSerivce.findProject(id);
        List<UserDto> userDtoList = userService.findUserListByProjectId(id);
        List<HeadDto> headDtoList = projectDetailSerivce.findHeadListByProject(presentDto);

        session.removeAttribute("currentProject");
        session.setAttribute("currentProject", presentDto);
        checkAuth();
        Long auth = getSessionAuth();

        projectDetailSerivce.completionCheckByDate(presentDto);
        List<DocumentDto> documentDtoList = documentService.findDocumentListByProjectId(id);

        // 완료, 미완 헤드 수
        int progressHead = projectDetailSerivce.countProgressHead(headDtoList);
        int completeHead = headDtoList.size() - progressHead;

        model.addAttribute("auth", auth);
        model.addAttribute("projectDto", presentDto);
        model.addAttribute("sessionUser", userDto);
        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("documentDtoList", documentDtoList);
        model.addAttribute("headDtoList", headDtoList);
        model.addAttribute("progressHead", progressHead);
        model.addAttribute("completeHead", completeHead);

        if (getSessionAuth() != 2) {
            List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByProject(presentDto);
            int progressWork = projectDetailSerivce.countProgressWork(workDtoList);
            int completeWork = workDtoList.size() - progressWork;

            model.addAttribute("workDtoList", workDtoList);
            model.addAttribute("progressWork", progressWork);
            model.addAttribute("completeWork", completeWork);
            return "projectMain";
        } else if (getSessionAuth() == 2) {
            List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByProject(presentDto);
            int progressWork = projectDetailSerivce.countProgressWork(workDtoList);
            int completeWork = workDtoList.size() - progressWork;

            model.addAttribute("workDtoList", workDtoList);
            model.addAttribute("progressWork", progressWork);
            model.addAttribute("completeWork", completeWork);

            return "onlyReadPage";
        }
        return null;
        //권한이 없습니다 알람창 띄우기
    }


    // 프로젝트 초대 확인창
    @GetMapping("/project/inviteList")
    public String inviteList(HttpSession session, Model model) {
        UserDto sessionUser = (UserDto) session.getAttribute("userInfo");
        //UUID를 활용하여 권한자 / 비권한자 프로젝트 리스트를 불러온다
        model.addAttribute("user", sessionUser);

        List<ProjectRequestDto> requestDtos = projectSerivce.findAllProjectRequestRecv(sessionUser);
        if (requestDtos.isEmpty()) {
            model.addAttribute("request", false);
        } else {
            model.addAttribute("request", true);
            model.addAttribute("requestList", requestDtos);
        }
        return "inviteList";
    }

    // 프로젝트 초대 발송 컨트롤러
    @RequestMapping("/project/invite/{id}")
    public String sendInvite(@PathVariable("id") String uuid, HttpSession session, Model model) {
        UserDto sendUser = (UserDto) session.getAttribute("userInfo");
        ProjectDto projectDto = (ProjectDto) session.getAttribute("currentProject");
        projectSerivce.sendInvite(sendUser.getUuid(), uuid, projectDto.getProjectId());
        return "redirect:/user/search";
    }

    // 프로젝트 초대 수락, 거절 컨트롤러
    // @PathVariable 통해 전달하여 url 노출됨. 추후 재고
    @RequestMapping("/requestResponse/{sendUser}/{recvUser}/{project}/{acceptable}")
    public String requestResponse(@PathVariable("sendUser") String sendUuid,
                                  @PathVariable("recvUser") String recvUuid,
                                  @PathVariable("project") Long projectId,
                                  @PathVariable("acceptable") boolean acceptable) {
        log.info("전달 완료, " + sendUuid + recvUuid + projectId + acceptable);
        projectSerivce.decisionInvite(sendUuid, recvUuid, projectId, acceptable);
        return "redirect:/project/inviteList";
    }


    /* - - - - - - onlyReadPage 접근 - - - - - - */

}

