package com.example.bpm.controller;

import com.example.bpm.dto.document.DocumentDto;
import com.example.bpm.dto.message.MessageDto;
import com.example.bpm.dto.project.HeadDto;
import com.example.bpm.dto.project.HeadInfoDto;
import com.example.bpm.dto.project.ProjectDto;
import com.example.bpm.dto.project.WorkDto;
import com.example.bpm.dto.project.relation.WorkCommentDto;
import com.example.bpm.dto.project.relation.WorkDocumentDto;
import com.example.bpm.dto.user.UserDto;
import com.example.bpm.dto.user.relation.UserWorkDto;
import com.example.bpm.service.*;
import com.example.bpm.service.Logic.dateLogic.DateManager;
import com.google.api.client.json.Json;
import com.google.cloud.storage.Acl;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.rmi.server.LogStream.log;

@Controller
@Slf4j
@ToString
@RequiredArgsConstructor
public class ProjectDetailController {
    @Autowired
    private ProjectDetailSerivce projectDetailSerivce;
    @Autowired
    private ProjectSerivce projectSerivce;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private UserService userService;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private ExceptionService exceptionService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private HttpSession session;


    public UserDto getSessionUser() {
        UserDto currentUser = (UserDto) session.getAttribute("userInfo");
        return currentUser;
    }

    public ProjectDto getSessionProject() {
        ProjectDto currentProject = (ProjectDto) session.getAttribute("currentProject");
        return currentProject;
    }

    // 세션 유저 권한 확인
    public Long getSessionAuth() {
        Long auth = (Long) session.getAttribute("auth");
        return auth;
    }

    public void checkAuth() {
        ProjectDto projectDto = getSessionProject();
        UserDto userDto = getSessionUser();
        Long auth = userService.checkRole(projectDto.getProjectId(), userDto.getUuid());
        session.setAttribute("auth", auth);
    }

    //
    // 프로젝트 메인 창 매핑
    @GetMapping("/project/main")
    public String goProjectMain(Model model) {
        ProjectDto sessionProject = getSessionProject();
        UserDto userDto = getSessionUser();
        List<HeadDto> headDtoList = projectDetailSerivce.findHeadListByProject(sessionProject);
        List<WorkDto> userWorkDtoList = projectDetailSerivce.findWorkListByProject(sessionProject);
        model.addAttribute("headDtoList", headDtoList);
        model.addAttribute("userWorkDtoList", userWorkDtoList);
        return "redirect:/project/" + sessionProject.getProjectId();
    }

    //
//    *//* - - - - 목표 관련 메서드- - - -*//*
    // 목표 리스트창 매핑
    @GetMapping("/project/goals")
    public String goals(Model model) {
        UserDto sessionUser = getSessionUser();
        ProjectDto currentProject = getSessionProject();
        Long auth = getSessionAuth();
        List<HeadDto> headDtoList = projectDetailSerivce.findHeadListByProject(currentProject);
        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());

        List<HeadInfoDto> headInfoDtoList = new ArrayList<>();
        HeadInfoDto headInfoDto;
        for (HeadDto headDto : headDtoList) {
            int workNum;
            int completeWorkNum;
            int progress;
            if (projectDetailSerivce.findWorkListByHead(headDto).size() == 0 || projectDetailSerivce.findWorkListByHead(headDto) == null) {
                workNum = 0;
                completeWorkNum = 0;
                progress = 0;
            } else {
                workNum = projectDetailSerivce.findWorkListByHead(headDto).size();
                completeWorkNum = projectDetailSerivce.getCompleteWorkNumByHead(headDto);
                progress = projectDetailSerivce.getHeadProgressPercent(headDto, workNum, completeWorkNum);
                if (workNum == completeWorkNum) {
                    progress = 100;
                }
            }
            headInfoDto = new HeadInfoDto();
            headInfoDto.setHeadDto(headDto);
            headInfoDto.setWorkNum(workNum);
            headInfoDto.setCompleteWorkNum(completeWorkNum);
            headInfoDto.setProgress(progress);
            headInfoDtoList.add(headInfoDto);
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("projectDto", currentProject);
        model.addAttribute("auth", auth);
        model.addAttribute("headDtoList", headDtoList);
        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("headInfoDtoList", headInfoDtoList);
        return "goal";
    }

    @GetMapping("/project/myGoals")
    public String myGoals(Model model) {
        UserDto sessionUser = getSessionUser();
        ProjectDto currentProject = getSessionProject();
        Long auth = getSessionAuth();
        List<UserWorkDto> userWorkDtoList = projectDetailSerivce.findUserWorkListByUser(sessionUser);
        List<HeadDto> headDtoList = new ArrayList<>();
        HeadDto headDto = new HeadDto();

        for (UserWorkDto userWorkDto : userWorkDtoList) {
            headDto.insertEntity(userWorkDto.getWorkIdToUserWork().getHeadIdToWork());
            if (!headDtoList.contains(headDto)) {
                headDtoList.add(headDto);
            }
        }

        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("projectDto", currentProject);
        model.addAttribute("auth", auth);
        model.addAttribute("headDtoList", headDtoList);
        model.addAttribute("joinUsers", userDtoList);
        return "myGoal";
    }

    // 상위 목표 생성 진입
    @GetMapping("/project/head/create")
    public String goHeadDetail(Model model, @RequestParam(value = "message", required = false) String message) {
        if (message != null) {
            model.addAttribute("message", message);
        }
        return "head-create";
    }

    // head 생성 메서드
    @PostMapping("/project/goal/createHead")
    public String createGoal(@RequestParam(value = "title") String title,
                             @RequestParam(value = "startDay") String startDay,
                             @RequestParam(value = "deadline") String deadline,
                             @RequestParam(value = "discription") String discription,
                             Model model) {
        ProjectDto currentProject = getSessionProject();
        ProjectDetailController.log.info("목표 생성 컨트롤러 작동, ");
        String message = exceptionService.headErrorCheck(currentProject, title, startDay, deadline);
        ProjectDetailController.log.info("head 생성 예외 처리 검사");
        if (message != null) {
            ProjectDetailController.log.info("예외 처리 결과 : " + message);
            model.addAttribute("message", message);
            return "head-create";
        }
        HeadDto createHeadDto = projectDetailSerivce.createHead(title, startDay, deadline, discription, currentProject);
        projectDetailSerivce.completionCheckByDate(currentProject);
        return "redirect:/project/goals";
    }

    // head 상세창 이동 메서드
    @RequestMapping("/project/goal/headView/{id}")
    public String goHeadView(@PathVariable("id") Long id, Model model) {
        HeadDto headDto = projectDetailSerivce.findHeadById(id);
        List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByHead(headDto);
        List<UserDto> userDtoList = userService.findUserListByProjectId(getSessionProject().getProjectId());
        List<String> findUser = new ArrayList<>();

        for (WorkDto workDto : workDtoList) {
            List<UserDto> userList1 = projectDetailSerivce.findUserListByWork(workDto);
            for (UserDto userDto : userList1) {
                if (!findUser.contains(userDto.getName())) {
                    findUser.add(userDto.getName());
                }
            }
        }

        int completeWork = 0;
        int progressWork = 0;
        int waitWork = 0;
        int lateWork = 0;
        for (WorkDto workDto : workDtoList) {
            if (workDto.getCompletion() == 0) {
                progressWork++;
            } else if (workDto.getCompletion() == 1) {
                completeWork++;
            } else if (workDto.getCompletion() == 2) {
                waitWork++;
            }
            else if (workDto.getCompletion() == 3) {
                lateWork++;
            }
        }

        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("sessionUser", getSessionUser());
        model.addAttribute("projectDto", getSessionProject());
        model.addAttribute("headDto", headDto);
        model.addAttribute("workDtoList", workDtoList);
        model.addAttribute("progressWork", progressWork);
        model.addAttribute("completeWork", completeWork);
        model.addAttribute("waitWork", waitWork);
        model.addAttribute("lateWork", lateWork);
        model.addAttribute("auth", getSessionAuth());
        model.addAttribute("chargeUser", findUser);
        return "headView";
    }

    // head 수정창 매핑 메서드
    @RequestMapping("/project/goal/head/edit/{id}")
    public String goEditHead(@PathVariable("id") Long headId,
                             @RequestParam(value = "message", required = false) String message,
                             Model model) {
        if (message != null) {
            model.addAttribute("message", message);
        }
        HeadDto headDto = projectDetailSerivce.findHeadById(headId);
        List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByHead(headDto);
        model.addAttribute("headDto", headDto);
        model.addAttribute("workDtoList", workDtoList);
        return "headEdit";
    }

    // work 수정창 매핑 메서드
    @RequestMapping("/project/goal/work/edit/{id}")
    public String goEditWork(@PathVariable("id") Long workId,
                             @RequestParam(value = "message", required = false) String message,
                             Model model) {
        List<HeadDto> headDtoList = projectDetailSerivce.findHeadListByProject(getSessionProject());
        WorkDto workDto = projectDetailSerivce.findWork(workId);
        List<UserDto> userDtoList = userService.findUserListByProjectId(getSessionProject().getProjectId());
        List<UserWorkDto> userWorkDtoList = projectDetailSerivce.findUserWorkListByWorkId(workId);

        if (message != null) {
            model.addAttribute("message", message);
        }
        model.addAttribute("headDtoList", headDtoList);
        model.addAttribute("workDto", workDto);
        model.addAttribute("userDtoList", userDtoList);
        model.addAttribute("userWorkDtoList", userWorkDtoList);
        return "workEdit";
    }

    // head 수정 실행 메서드
    @PostMapping("/project/head/edit")
    public String editHead(@RequestParam(value = "title") String title,
                           @RequestParam(value = "startDay") String startDay,
                           @RequestParam(value = "deadline") String deadline,
                           @RequestParam(value = "discription") String discription,
                           @RequestParam(value = "headId") Long headId,
                           @RequestParam(value = "deleteWorkList", required = false) List<Long> deleteWorkList,
                           RedirectAttributes rttr,
                           Model model) {

        DateManager dateManager = new DateManager();
        ProjectDto currentProject = getSessionProject();
        String message = exceptionService.headEditErrorCheck(currentProject, title, startDay, deadline);
        if (message != null) {
            log.info("예외 처리 결과 : " + message);
            rttr.addFlashAttribute("message", message);
            return "redirect:/project/goal/head/edit/" + headId;
        }
        HeadDto afterHead = projectDetailSerivce.findHeadById(headId);
        afterHead.setTitle(title);
        afterHead.setDiscription(discription);
        afterHead.setStartDay(dateManager.formatter(startDay));
        afterHead.setEndDay(dateManager.formatter(deadline));
        projectDetailSerivce.updateHead(headId, afterHead);

        if (deleteWorkList != null) {
            for (Long workId : deleteWorkList) {
                projectDetailSerivce.deleteWork(projectDetailSerivce.findWork(workId));
            }
        }
        projectDetailSerivce.completionCheckByDate(currentProject);
        return "redirect:/project/goals";
    }

    // work 수정 실행 메서드
    // entity 사용 배제 필요
    @PostMapping("/project/work/edit")
    public String editWork(@RequestParam(value = "title") String title,
                           @RequestParam(value = "startDay") String startDay,
                           @RequestParam(value = "deadline") String endDay,
                           @RequestParam(value = "discription") String discription,
                           @RequestParam(value = "chargeUsers") List<String> chargeUsers,
                           @RequestParam("headId") Long headId,
                           @RequestParam(value = "workId") Long workId,
                           RedirectAttributes rttr) {
        log.info("작업 편집 메서드 실행");
        DateManager dateManager = new DateManager();
        WorkDto workDto = projectDetailSerivce.findWork(workId);
        String message = exceptionService.workEditErrorCheck(startDay, endDay, headId);
        if (message != null) {
            log.info("예외 처리 결과 : " + message);
            rttr.addFlashAttribute("message", message);
            return "redirect:/project/goal/work/edit/" + workId;
        }

        workDto.setTitle(title);
        workDto.setDiscription(discription);
        workDto.setStartDay(dateManager.formatter(startDay));
        workDto.setEndDay(dateManager.formatter(endDay));
        workDto.setHeadIdToWork(projectDetailSerivce.findHeadById(headId).toEntity());

        projectDetailSerivce.updateWork(workId, workDto);
        projectDetailSerivce.deleteUserWork(workDto.toEntity());
        projectDetailSerivce.addUserWork(workDto, chargeUsers);
        projectDetailSerivce.completionCheckByDate(getSessionProject());
        return "redirect:/project/work/detail/" + workId;
    }

    //    *//* - - - - 목표 관련 메서드 끝 - - - -*//*
//
//
//    *//* - - - - 작업 관련 메서드- - - -*//*
    // work 목록 진입 매핑
    @GetMapping("/project/works")
    public String works(Model model) {
        UserDto sessionUser = getSessionUser();
        ProjectDto currentProject = getSessionProject();
        Long auth = getSessionAuth();
        List<HeadDto> headDtoList = projectDetailSerivce.findHeadListByProject(currentProject);
        List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByProject(currentProject);
        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("projectDto", currentProject);
        model.addAttribute("auth", auth);
        model.addAttribute("headDtoList", headDtoList);
        model.addAttribute("workDtoList", workDtoList);
        model.addAttribute("joinUsers", userDtoList);
        return "work";
    }

    @GetMapping("/project/myWorks")
    public String myWorks(Model model) {
        UserDto sessionUser = getSessionUser();
        ProjectDto currentProject = getSessionProject();
        Long auth = getSessionAuth();
        List<UserWorkDto> userWorkDtoList = projectDetailSerivce.findUserWorkListByUser(sessionUser);
        List<UserWorkDto> projectUserWorkDtoList = projectDetailSerivce.findUserWorkListByProject(userWorkDtoList, currentProject);
        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("projectDto", currentProject);
        model.addAttribute("auth", auth);
        model.addAttribute("userWorkDtoList", projectUserWorkDtoList);
        model.addAttribute("joinUsers", userDtoList);
        return "myWork";
    }

    // work 생성창 진입 메서드
    @GetMapping("/project/work/create")
    public String goCreateWork(Model model, @RequestParam(value = "message", required = false) String message) {
        ProjectDto currentProject = getSessionProject();
        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());
        List<HeadDto> headDtoList = projectDetailSerivce.findHeadListByProject(currentProject);
        if (message != null) {
            model.addAttribute("message", message);
        }
        model.addAttribute("userDtoList", userDtoList);
        model.addAttribute("headDtoList", headDtoList);
        return "workCreate";
    }

    // work 생성 메서드
    @PostMapping("/project/work/createWork")
    public String createWork(@RequestParam("title") String title,
                             @RequestParam("discription") String discription,
                             @RequestParam("startDay") String startDay,
                             @RequestParam("deadline") String deadline,
                             @RequestParam("headId") Long detailId,
                             @RequestParam("chargeUsers") List<String> chargeUsers,
                             RedirectAttributes rttr) {
        ProjectDto currentProject = getSessionProject();
        String message = exceptionService.workEditErrorCheck(startDay, deadline, detailId);
        log("메서드 진입성공");
        if (message != null) {
            log("생성 실패 if문 걸림");
            rttr.addFlashAttribute("message", message);
            return "redirect:/project/work/create";

        }
        HeadDto connnectHead = projectDetailSerivce.findHeadById(detailId);
        WorkDto createWorkDto = projectDetailSerivce.createWork(title, discription, startDay, deadline,
                connnectHead, currentProject);
        ProjectDetailController.log.info("작업 생성 메서드 완료, id = " + createWorkDto.getWorkId());
        projectDetailSerivce.addUserWork(createWorkDto, chargeUsers);
        projectDetailSerivce.completionCheckByDate(currentProject);
        return "redirect:/project/works";
    }

    // work 상세창 진입 메서드
    @RequestMapping("/project/work/detail/{id}")
    public String goWorkDetail(@PathVariable("id") Long id, Model model) {
        WorkDto workDto = projectDetailSerivce.findWork(id);
        HeadDto headDto = projectDetailSerivce.findHeadById(workDto.getHeadIdToWork().getHeadId());
        List<UserWorkDto> userWorkDtoList = projectDetailSerivce.findUserWorkListByWorkId(id);
        List<DocumentDto> documentDtoList = documentService.findDocumentByWorkId(id);
        List<WorkCommentDto> commentDtoList = projectDetailSerivce.findWorkCommentListByWork(workDto);
        List<WorkDocumentDto> workDocumentDtoList = projectDetailSerivce.findWorkDocumentListByWork(workDto);
        if (commentDtoList.isEmpty()) {
            int i = 0;
            model.addAttribute("listNum", i);
            model.addAttribute("CommentList", commentDtoList);
        } else {
            int i = commentDtoList.size();
            model.addAttribute("listNum", i);
            model.addAttribute("CommentList", commentDtoList);
        }
        Long auth = getSessionAuth();

        boolean workAdd = documentService.accreditUserToWork(getSessionUser().getUuid(), id, auth);

        List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByHead(headDto);
        int workNum = workDtoList.size();
        int completeWork;
        int percent;
        if (workDtoList == null || workNum == 0) {
            completeWork = 0;
            workNum = 0;
            percent = 0;
        } else {
            completeWork = projectDetailSerivce.getCompleteWorkNumByHead(headDto);
            if (workNum == completeWork) {
                percent = 100;
            } else {
                percent = projectDetailSerivce.getHeadProgressPercent(headDto, workDtoList.size(), completeWork);
            }
        }

        List<UserDto> userDtoList = userService.findUserListByProjectId(getSessionProject().getProjectId());
        model.addAttribute("headDto", headDto);
        model.addAttribute("percent", percent);
        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("sessionUser", getSessionUser());
        model.addAttribute("projectDto", getSessionProject());
        model.addAttribute("auth", auth);
        model.addAttribute("workDto", workDto);
        model.addAttribute("userWorkDtoList", userWorkDtoList);
        model.addAttribute("workDocumentDtoList", workDocumentDtoList);
        model.addAttribute("documentList", documentDtoList);
        model.addAttribute("check", workAdd);
        return "workDetail";
    }

    //    *//* - - - - 작업 관련 메서드 끝 - - - -*//*
//
//    *//* - - - - 삭제 메서드 - - - - *//*
    @GetMapping("project/delete/{id}")
    public String deleteProject(@PathVariable("id")Long projectId) {
        ProjectDto projectDto = projectSerivce.findProject(projectId);
        projectDetailSerivce.deleteProject(projectDto);
        return "redirect:/project/projectManagerList";
    }

    @RequestMapping("/project/goal/head/delete/{id}")
    public String deleteHead(@PathVariable("id") Long headId) {
        projectDetailSerivce.deleteHead(projectDetailSerivce.findHeadById(headId));
        return "redirect:/project/goals";
    }

    @RequestMapping("/project/goal/work/delete/{id}")
    public String deleteWork(@PathVariable("id") Long workId) {
        projectDetailSerivce.deleteWork(projectDetailSerivce.findWork(workId));
        return "redirect:/project/works";
    }


    /* - - - - 댓글 관련 메서드 - - - -*/
    @PostMapping("/workDetail/addComment")
    public String plusComment(@RequestParam("workId") Long workId,
                              @RequestParam("comment") String comment,
                              HttpSession session, Model model, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        /* 댓글을 추가 시키는 메서드 */
        WorkDto workDto = projectDetailSerivce.findWork(workId);
        UserDto nowUser = getSessionUser();
        WorkCommentDto workCommentDto = new WorkCommentDto();
        workCommentDto.setComment(comment);
        workCommentDto.setWorkIdToComment(workDto.toEntity());
        workCommentDto.setUserIdToComment(nowUser.toEntity());
        /* 댓글을 추가 시키는 메서드 끝 */

        /*추가 시킬 댓글 내용과, 현재 documentID 를 같이 넘겨 리턴 값으로 자동 리스트를 뽑아온다*/
        projectDetailSerivce.createWorkComment(workCommentDto);
        List<WorkCommentDto> list = projectDetailSerivce.findWorkCommentListByWork(workDto);
        model.addAttribute("commentList", list);
        return "redirect:" + referer;
    }

    //
//    //댓글 수정을 하기 위한 댓글 데이터를 가져오는 메서드 (프론트에서는 댓글을 수정할 수 있는 화면이 필요하다
//    @GetMapping("/workDetail/commentUpdate")
//    public String updateForm(@RequestParam("commentId") Long commentId, Model model) {
//        WorkCommentDto updateComment = projectDetailSerivce.findWorkComment(commentId);
//        model.addAttribute("updateComment", updateComment);
//
//        return "";
//    }
//
//    @PostMapping("댓글 수정 완료 시")
//    public String updateComment(@RequestParam("workId") Long workId,
//                                @RequestParam("comment") String comment, HttpSession session, Model model) {
//
//        WorkDto workDto = projectDetailSerivce.findWork(workId);
//        UserDto nowUser = getSessionUser();
//        WorkCommentDto workCommentDto = new WorkCommentDto();
//        workCommentDto.setComment(comment);
//        workCommentDto.setWorkIdToComment(workDto.toEntity());
//        workCommentDto.setUserIdToComment(nowUser.toEntity());
//
//        List<WorkCommentDto> list = projectDetailSerivce.plusComment(workCommentDto, workId);
//        model.addAttribute("commentList", list);
//        return "";
//    }
//
    @RequestMapping("/workDetail/commentDelete/{cid}")
    public String deleteComment(@PathVariable("cid") Long commentId, Model model) {
        WorkCommentDto workCommentDto = projectDetailSerivce.findWorkComment(commentId);
        Long workId = workCommentDto.getWorkIdToComment().getWorkId();
        projectDetailSerivce.deleteWorkComment(commentId);
        List<WorkCommentDto> workCommentDtoList = projectDetailSerivce.findWorkCommentListByWork(projectDetailSerivce.findWork(workId));
        model.addAttribute("CommentList", workCommentDtoList);
        return "redirect:/project/work/detail/" + workId;
    }

    //    *//* - - - - 댓글 관련 메서드 끝 - - - -*//*
//
//    *//* 상태 완료 처리 메서드 *//*
    @RequestMapping("/project/head/completion/change")
    public String headCompletionChange(Long id, int state) {
        projectDetailSerivce.updateHeadCompletion(id, state);
        return "redirect:/project/goals";
    }

    @RequestMapping("/project/work/completion/change")
    public String workCompletionChange(Long id, int state, HttpServletRequest request) {
        projectDetailSerivce.updateWorkCompletion(id, state);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    //*  - - - - - Calendar Controller - - - - - *//
    @GetMapping("/project/calender") //기본 페이지 표시
    public String viewCalendar(Model model) {

        ProjectDto currentProject = getSessionProject();
        UserDto sessionUser = getSessionUser();
        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("projectDto", currentProject);
        model.addAttribute("joinUsers", userDtoList);

        return "calendar";
    }

    @RequestMapping(value = "/calendar/event", method = {RequestMethod.GET}) //ajax 데이터 전송 URL
    public @ResponseBody List<Map<String, Object>> getEvent() {

        ProjectDto projectDto = getSessionProject();

        return calendarService.getEventList(projectDto.getProjectId());
    }


    //* - - - - - Message Contorller - - - - - - *//*
    @GetMapping("/project/recvMessageList")
    public String viewRecvMessageList(HttpSession session, Model model) {
        UserDto userDto = getSessionUser();
        ProjectDto projectDto = getSessionProject();
        List<MessageDto> messageDtoList = messageService.recvMessageList(userDto, projectDto);
        UserDto sessionUser = getSessionUser();
        ProjectDto currentProject = getSessionProject();
        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("projectDto", currentProject);
        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("List", messageDtoList);
        return "recvMessageList";
    }

    @GetMapping("/project/sendMessageList")
    public String viewSendMessageList(HttpSession session, Model model) {
        UserDto userDto = getSessionUser();
        ProjectDto projectDto = getSessionProject();
        List<MessageDto> messageDtoList = messageService.sendMessageList(userDto, projectDto);
        List<UserDto> userDtoList = userService.findUserListByProjectId(projectDto.getProjectId());
        model.addAttribute("sessionUser", userDto);
        model.addAttribute("projectDto", projectDto);
        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("List", messageDtoList);

        return "sendMessageList";
    }

    @GetMapping("/project/messageForm")
    public String sendMessageForm(Model model) {
        List<UserDto> userDtos = userService.findUserListByProjectId(getSessionProject().getProjectId());
        model.addAttribute("userList", userDtos);
        UserDto sessionUser = getSessionUser();
        ProjectDto currentProject = getSessionProject();
        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("projectDto", currentProject);
        model.addAttribute("joinUsers", userDtoList);
        return "messageForm";
    }

    @PostMapping("/project/sendMessage")
    public String sendMessage(@RequestParam(value = "title") String title,
                              @RequestParam(value = "content") String content,
                              @RequestParam(value = "recvName") String name,
                              HttpSession session) {
        messageService.sendMessage(title, content, 0L, 1, getSessionUser(), name, getSessionProject());

        return "redirect:/project/sendMessageList";
    }

    @GetMapping("/project/sendRequest/{id}")
    public String sendRequest(@PathVariable("id") Long id,
                              HttpServletRequest request) {
        WorkDto workDto = projectDetailSerivce.findWork(id);
        UserDto projectManager = userService.findManagerUserByProject(getSessionProject().getProjectId());
        log.info("탐색 완료. 유저 : " + projectManager.getName());
        String title = (workDto.getTitle() + " 작업 완료 요청");
        String content = ("완료 요청인 : 담당자 " + getSessionUser().getName());
        messageService.sendMessage(title, content, id, 1, getSessionUser(), projectManager.getUuid(), getSessionProject());

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/project/requestConfirm")
    public String requestConfirm(Long requestId, HttpServletRequest request) {
        if (requestId != 0) {
            WorkDto workDto = projectDetailSerivce.findWork(requestId);
            workDto.setCompletion(1);
            projectDetailSerivce.updateWork(requestId, workDto);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    @GetMapping("/project/sendMessageList/{id}")
    public String selectSendMessage(@PathVariable("id") Long id, Model model) {
        MessageDto messageDto = messageService.findMessage(id);
        model.addAttribute("message", messageDto);
        UserDto sessionUser = getSessionUser();
        ProjectDto currentProject = getSessionProject();
        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("projectDto", currentProject);
        model.addAttribute("joinUsers", userDtoList);
        return "sendMessageDetail";
    }

    @GetMapping("/project/recvMessageList/{id}")
    public String selectRecvMessage(@PathVariable("id") Long id, Model model) {
        MessageDto beforeMessage = messageService.findMessage(id);
        beforeMessage.setState(0);
        MessageDto afterMessage = messageService.updateMessage(id, beforeMessage);
        model.addAttribute("message", afterMessage);
        UserDto sessionUser = getSessionUser();
        ProjectDto currentProject = getSessionProject();
        List<UserDto> userDtoList = userService.findUserListByProjectId(currentProject.getProjectId());
        Long auth = getSessionAuth();
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("projectDto", currentProject);
        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("auth", auth);
        return "recvMessageDetail";
    }

    @GetMapping("/project/message/delete/{id}")
    public String deleteMessage(@PathVariable("id") Long messageId) {
        messageService.deleteMessage(messageId);
        return "redirect:/project/recvMessageList";
    }

    @GetMapping("/project/template")
    public String goProjectTemplate(Model model) {
        UserDto userDto = getSessionUser();
        ProjectDto presentDto = getSessionProject();
        List<UserDto> userDtoList = userService.findUserListByProjectId(presentDto.getProjectId());
        List<HeadDto> headDtoList = projectDetailSerivce.findHeadListByProject(presentDto);
        Long auth = getSessionAuth();

        projectDetailSerivce.completionCheckByDate(presentDto);
        List<DocumentDto> documentDtoList = documentService.findDocumentListByProjectId(presentDto.getProjectId());

        // 완료, 미완 헤드 수
        int progressHead = projectDetailSerivce.countProgressHead(headDtoList);
        int completeHead = headDtoList.size() - progressHead;

        // 완료 진척도
        int percentage = projectDetailSerivce.getProjectProgressPercent(presentDto);

        model.addAttribute("per", percentage);
        model.addAttribute("auth", auth);
        model.addAttribute("projectDto", presentDto);
        model.addAttribute("sessionUser", userDto);
        model.addAttribute("joinUsers", userDtoList);
        model.addAttribute("documentDtoList", documentDtoList);
        model.addAttribute("headDtoList", headDtoList);
        model.addAttribute("progressHead", progressHead);
        model.addAttribute("completeHead", completeHead);

        List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByProject(presentDto);
        int progressWork = projectDetailSerivce.countProgressWork(workDtoList);
        int completeWork = workDtoList.size() - progressWork;

        model.addAttribute("workDtoList", workDtoList);
        model.addAttribute("progressWork", progressWork);
        model.addAttribute("completeWork", completeWork);
        return "documentTemplate";
    }

    @GetMapping("/project/gantt")
    public String ganttChartTest(Model model) {
        ProjectDto projectDto = getSessionProject();
        List<UserDto> userDtoList = userService.findUserListByProjectId(projectDto.getProjectId());
        model.addAttribute("sessionUser", getSessionUser());
        model.addAttribute("projectDto", projectDto);
        model.addAttribute("joinUsers", userDtoList);
        return "gantt";
    }

    @RequestMapping(value = "/gantt/event", method = {RequestMethod.GET})
    public @ResponseBody List<Map<String, Object>> getGanttEvent() {
        List<Map<String, Object>> workList = new ArrayList<>();
        HashMap<String, Object> work;
        List<HeadDto> headDtoList = projectDetailSerivce.findHeadListByProject(getSessionProject());
        for (HeadDto headDto : headDtoList) {
            work = new HashMap<String, Object>();
            work.put("id", headDto.getTitle());
            work.put("name", headDto.getTitle());
            work.put("startDay", headDto.getStartDay());
            work.put("deadline", headDto.getEndDay());
            work.put("connectedObject", null);
            workList.add(work);
        }

        List<WorkDto> workDtoList = projectDetailSerivce.findWorkListByProject(getSessionProject());
        for (WorkDto workDto : workDtoList) {
            work = new HashMap<String, Object>();
            work.put("id", workDto.getTitle());
            work.put("name", workDto.getTitle());
            work.put("startDay", workDto.getStartDay());
            work.put("deadline", workDto.getEndDay());
            work.put("connectedObject", workDto.getHeadIdToWork().getTitle());
            workList.add(work);
        }
        return workList;
    }
}

