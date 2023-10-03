package com.example.bpm.controller;

import com.example.bpm.dto.ProjectDto;
import com.example.bpm.dto.UserDto;
import com.example.bpm.service.ProjectSerivce;
import com.example.bpm.service.UserService;

import javax.persistence.*;
import javax.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@ToString
@RequiredArgsConstructor
public class UserController {
    @Autowired
    final private UserService userService;
    @Autowired
    final private ProjectSerivce projectSerivce;
    @Autowired
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
        Long auth = userService.checkRole(projectDto.getProjectId(), userDto.getUuid());
        session.setAttribute("auth", auth);
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/user/join")
    public String goSave() {
        return "join";
    }


    @PostMapping("/user/dosave")
    public String save(@RequestParam("email") String email,
                       @RequestParam("password") String password,
                       @RequestParam("username") String name, Model model) {
        UserDto findUser = userService.findByEmail(email);
        if (findUser == null) {
            UserDto NewUser = new UserDto(email, password, name);
            log.info("DTO 정상 값 입력 (컨트롤러)" + "/" + email + "/" + password + "/" + name);
            UserDto result = userService.save(NewUser);
            return "login";
        } else {
            model.addAttribute("message", "이미 있는 이메일 입니다.");
            return "join";
        }
    }



    @GetMapping("/user/login")
    public String login() {
        return "login";
    }


    @PostMapping("/user/dologin")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session, Model model) {
        UserDto loginResult = userService.login(email, password);
        if (loginResult != null) {
            //세션에 로그인한 정보롤 담아줌 -> main 창에 적용되고 main에 이 세션을 이용할 수 있는 thyleaf가 적용되는 것이다.
            session.removeAttribute("userInfo");
            session.setAttribute("userInfo", loginResult);
            //로그인 성공 알림창 만들어줘야함
            log.info("로그인 성공 세션 정상 입력 (컨트롤러 작동)");
            return "redirect:/project/projectManagerList";
        } else {
            model.addAttribute("message", "이메일 혹은 비밀번호가 일치하지 않습니다.");
            log.info("로그인 실패 세션 적용 실패 (컨트롤러 작동)");
            return "login";
        }
    }

    @GetMapping("/user/logout")
    public String logout(HttpSession session) {
        //세션으로 로그아웃 처리
        session.invalidate();
        log.info("로그아웃 성공 세션 정상 작동(컨트롤러)");
        return "redirect:/index";
    }


    //프로필로 가는 메서드 세션값을 활용해서 user의 정보를 찾아낸다
    @GetMapping("/user/account")
    public String goAccount(HttpSession session, Model model) {
        UserDto sessionUser = getSessionUser();
        UserDto result = userService.findByUser(sessionUser.getUuid());
        model.addAttribute("user", result);
        return "account";
//        if (result != null) {
//            model.addAttribute("user", sessionUser);
//            log.info("회원정보 찾기 성공 (컨트롤러 작동) detail 페이지로 이동");
//            return "user/detail";
//        } else {
//            log.info("서비스에서 유저를 찾지 못함 (컨트롤러 작동)");
//            return null;
//        }
    }

    @GetMapping("/user/accountUpdate")
    public String goAccountChange(HttpSession session, Model model) {
        UserDto sessionUser = getSessionUser();
        UserDto result = userService.findByUser(sessionUser.getUuid());
        model.addAttribute("user", result);
        return "accountUpdate";
    }

    //프로필에서 정보 변경 시 유저의 정보를 찾아오는 메서드

    //회원 정보 변경 시 메서드
    @PostMapping("/user/update")
    public String update(@RequestParam("email") String email,
                         @RequestParam("userName") String name, HttpSession session) {
        UserDto sessionUser = getSessionUser();
        log.info("변경 전 정보 " + sessionUser.getEmail() + sessionUser.getName());
        UserDto updateDto = userService.update(sessionUser, email, name);
        log.info("변경 후 정보 " + updateDto.getEmail() + updateDto.getName());
        if (updateDto != null) {
            log.info("정상 업데이트 되었습니다 (컨트롤러 작동)");
//            session.removeAttribute("userInfo");
//            session.setAttribute("userInfo", updateDto);
            return "redirect:/user/login";
        } else {
            log.info("업데이트 불가 (컨트롤러 작동)");
            return "redirect:/user/accountUpdate";
        }
    }

    @GetMapping("/user/passwordChange")
    public String goPasswordChange(Model model) {
        UserDto sessionUser = getSessionUser();
        UserDto result = userService.findByUser(sessionUser.getUuid());
        model.addAttribute("user", result);
        return "passwordChange";

    }

    // 비밀번호 변경 메서드
    @PostMapping("/passwordUpdate")
    public String passwordChange(@RequestParam("email") String email,
                                 @RequestParam("password") String password,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session) {
        UserDto sessionUser = getSessionUser();
        log.info("컨트롤러 호출 완료");
        log.info(sessionUser.getPassword(), password, email, newPassword, confirmPassword);
        int result = userService.changePassword(sessionUser, email, password, newPassword, confirmPassword);
        if (result == 0) {
            session.removeAttribute("userInfo");
            return "login";
        } else return "login";
    }

    //회원 탈퇴 메서드
    @GetMapping("/user/delete")
    public String deleteById(HttpSession session) {
        UserDto userDto = getSessionUser();
        userService.deleteById(userDto.getUuid());
        session.invalidate();
        log.info("탈퇴되었습니다 (컨트롤러 작동)");
        return "redirect:/index";
    }

    @GetMapping("/user/search")
    public String searchMember() {
        return "searchMember";
    }

    @PostMapping("/user/returnSearch")
    public String search(@RequestParam("searchKeyword") String searchKeyword, Model model) {
        log.info("검색 키워드 : " + searchKeyword);
        List<UserDto> dtoList = userService.searchUser(searchKeyword);

        if (dtoList.isEmpty()) {
            log.info("검색 결과 없음");
            return "redirect:/user/search";
        }
        model.addAttribute("searchUsers", dtoList);
        return "searchMember";
    }
}
