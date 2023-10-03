package com.example.bpm.dto;

import com.example.bpm.entity.DetailEntity;
import com.example.bpm.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkRequestDto {

    private Long workId;
    private String title;
    private Date startDay;
    private Date endDay;
    private int completion;
    private UserEntity userIdToWorkRequest;
    private DetailEntity detailIdToWorkRequest;

}
