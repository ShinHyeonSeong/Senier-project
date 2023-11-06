package com.example.bpm.dto.project;

import lombok.Data;

@Data
public class HeadInfoDto {
    private HeadDto headDto;

    private int workNum;

    private int completeWorkNum;

    private int progress;
}
