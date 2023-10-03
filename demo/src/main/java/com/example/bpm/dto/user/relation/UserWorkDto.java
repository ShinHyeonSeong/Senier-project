package com.example.bpm.dto.user.relation;

import com.example.bpm.entity.user.UserEntity;
import com.example.bpm.entity.user.relation.UserWorkEntity;
import com.example.bpm.entity.project.data.WorkEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWorkDto {
    private WorkEntity workIdToUserWork;

    private UserEntity userIdToUserWork;

    public UserWorkEntity toEntity(){
        UserWorkEntity userWorkEntity = new UserWorkEntity();

        userWorkEntity.setWorkIdToUserWork(workIdToUserWork);
        userWorkEntity.setUserIdToUserWork(userIdToUserWork);

        return userWorkEntity;
    }

    public void insertEntity(UserWorkEntity userWorkEntity){
        this.workIdToUserWork = userWorkEntity.getWorkIdToUserWork();
        this.userIdToUserWork = userWorkEntity.getUserIdToUserWork();
    }
}
