package com.example.bpm.service;

import com.example.bpm.dto.project.ProjectDto;
import com.example.bpm.dto.user.UserDto;
import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.project.relation.ProjectRoleEntity;
import com.example.bpm.entity.user.UserEntity;
import com.example.bpm.repository.ProjectRepository;
import com.example.bpm.repository.ProjectRoleRepository;
import com.example.bpm.repository.UserRepository;
import com.google.cloud.storage.Acl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    @Autowired
    final private UserRepository userRepository;
    @Autowired
    final private ProjectRoleRepository projectRoleRepository;
    @Autowired
    private ProjectRepository projectRepository;

    /* sign up */
    public UserDto signUp(UserDto userDto) {
        UserEntity userEntity = userDto.toEntity();
        Optional<UserEntity> userResult = userRepository.findByEmail(userDto.getEmail());

        if (userResult.isPresent()) {
            return null;
        }

        else {
            userRepository.save(userEntity);
            userDto.insertEntity(userEntity);
            return userDto;
        }
    }

    /* sign in */
    public UserDto signIn(String email, String password) {
        Optional<UserEntity> findUser = userRepository.findByEmail(email);
        if (findUser.isEmpty()) {
            return null;
        }

        UserEntity loginUser = findUser.get();

        if (loginUser.getEmail().equals(email) && loginUser.getPassword().equals(password)) {

            UserDto userDto = new UserDto();
            userDto.insertEntity(loginUser);

            return userDto;
        }
        else {
            return null;
        }
    }

    //////////////////////////////////////////////////////////////////
    // find user
    //////////////////////////////////////////////////////////////////

    public UserDto findUser(String id) {
        Optional<UserEntity> findId = userRepository.findById(id);

        if (findId.isPresent()) {
            UserEntity userEntity = findId.get();
            UserDto userDto = new UserDto();
            userDto.insertEntity(userEntity);
            return userDto;
        }

        else {
            return null;
        }
    }

    public UserDto findUserByEmail(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        if (userEntity.isEmpty()) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.insertEntity(userEntity.get());

        return userDto;
    }

    public List<UserDto> findUserByEmailContaining(String searchKeyword) {
        List<UserEntity> userEntityList = userRepository.findAllByEmailContaining(searchKeyword);
        List<UserDto> userDtoList = new ArrayList<>();

        for (UserEntity userEntity : userEntityList) {
            UserDto userDto = new UserDto();
            userDto.insertEntity(userEntity);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    public List<UserDto> findUserListByProjectId(Long id) {
        List<ProjectRoleEntity> projectRoleEntityList = projectRoleRepository.findProjectRoleByProject(id);
        List<UserDto> userDtoList = new ArrayList<>();

        for (ProjectRoleEntity projectRoleEntity : projectRoleEntityList) {
            UserEntity userEntity = projectRoleEntity.getUuidInRole();

            UserDto userDto = new UserDto();
            userDto.insertEntity(userEntity);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    public UserDto findManagerUserByProject(Long id) {
        UserDto userDto = new UserDto();
        List<ProjectRoleEntity> projectRoleEntityList = projectRoleRepository.findProjectRoleByProject(id);
        for(ProjectRoleEntity projectRoleEntity : projectRoleEntityList) {
            log.info(projectRoleEntity.getUuidInRole().getName());
            if (projectRoleEntity.getRole().getId() == 1L) {
                userDto.insertEntity(projectRoleEntity.getUuidInRole());
                log.info("확인 완료, 프로젝트 매니저 : " + projectRoleEntity.getUuidInRole().getName());
            }
        }
        return userDto;
    }

    /* check user role */

    public Long checkRole(Long id, String uuid) {

        ProjectRoleEntity projectRoleEntity = projectRoleRepository.findByProjectIdInRole_ProjectIdAndUuidInRole_Uuid(id, uuid);

        if (projectRoleEntity != null) {

            return projectRoleEntity.getRole().getId();

        } else {

            return Long.valueOf(2);
        }
    }

    //////////////////////////////////////////////////////////////////
    // update user
    //////////////////////////////////////////////////////////////////

    @Transactional
    public UserDto updateUser(UserDto userDto, String email, String name) {
        userDto.setEmail(email);
        userDto.setName(name);

        userRepository.save(userDto.toEntity());

        return userDto;
    }

    @Transactional
    public int updatePassword(UserDto userDto, String email, String password, String newPassword, String confirmPassword) {
        UserEntity userEntity = userRepository.findById(userDto.getUuid()).get();

        if (userEntity.getEmail().equals(email)) {

            if (userEntity.getPassword().equals(password)) {

                if (newPassword.equals(confirmPassword)) {

                    userEntity.setPassword(newPassword);
                    userRepository.save(userEntity);

                    return 0;
                }

                else {
                    return 3;
                }
            }

            else {
                return 2;
            }
        }

        else {
            return 1;
        }
    }

    //////////////////////////////////////////////////////////////////
    // delete user
    //////////////////////////////////////////////////////////////////

    // not work
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

}

