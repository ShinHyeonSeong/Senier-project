package com.example.bpm.dto;

import com.example.bpm.entity.DetailEntity;
import com.example.bpm.entity.ProjectEntity;
import com.example.bpm.entity.WorkEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class WorkDto {
    private Long workId;

    private String title;

    private String discription;

    private Date startDay;

    private Date endDay;

    private int completion;

    private DetailEntity detailIdToWork;

    private ProjectEntity projectIdToWork;

    public static WorkDto toWorkDto(WorkEntity workEntity) {
        WorkDto workDto = new WorkDto();
        workDto.setWorkId(workEntity.getWorkId());
        workDto.setTitle(workEntity.getTitle());
        workDto.setDiscription(workEntity.getDiscription());
        workDto.setStartDay(workEntity.getStartDay());
        workDto.setEndDay(workEntity.getEndDay());
        workDto.setCompletion(workEntity.getCompletion());
        workDto.setDetailIdToWork(workEntity.getDetailIdToWork());
        workDto.setProjectIdToWork(workEntity.getProjectIdToWork());
        return workDto;
    }
}
