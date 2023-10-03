package com.example.bpm.dto.user;

import com.example.bpm.entity.project.data.WorkEntity;
import com.example.bpm.entity.user.UserEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String uuid;
    private String email;
    private String password;
    private String name;

    //회원가입을 할 때 필요한 생성자
    public UserDto(String email, String password, String name) {
        this.uuid = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserEntity toEntity(){
        UserEntity userEntity = new UserEntity();

        userEntity.setUuid(uuid);
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        userEntity.setName(name);

        return userEntity;
    }

    public void insertEntity(UserEntity userEntity){
        this.uuid = userEntity.getUuid();
        this.email = userEntity.getEmail();
        this.password = userEntity.getPassword();
        this.name = userEntity.getName();
    }
}
