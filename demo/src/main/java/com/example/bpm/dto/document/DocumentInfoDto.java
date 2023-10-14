package com.example.bpm.dto.document;

import com.example.bpm.dto.project.HeadDto;
import com.example.bpm.dto.project.WorkDto;
import lombok.Data;

@Data
public class DocumentInfoDto {

    DocumentDto documentDto;

    WorkDto workDto;

    HeadDto headDto;

    Boolean isRole;
}
