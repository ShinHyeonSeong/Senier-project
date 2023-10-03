package com.example.bpm.dto;

import com.example.bpm.entity.HeadEntity;
import com.example.bpm.entity.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

//목표 DTO
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HeadDto {

    private Long headId;

    private String title;

    private String discription;

    private Date startDay;

    private Date endDay;

    private int completion;

    private ProjectEntity projectIdToHead;

    public static HeadDto toHeadDto(HeadEntity headEntity) {
        HeadDto headDto = new HeadDto();
        headDto.setHeadId(headEntity.getHeadId());
        headDto.setTitle(headEntity.getTitle());
        headDto.setDiscription(headEntity.getDiscription());
        headDto.setStartDay(headEntity.getStartDay());
        headDto.setEndDay(headEntity.getEndDay());
        headDto.setCompletion(headEntity.getCompletion());
        headDto.setProjectIdToHead(headEntity.getProjectIdToHead());
        return headDto;
    }
}
