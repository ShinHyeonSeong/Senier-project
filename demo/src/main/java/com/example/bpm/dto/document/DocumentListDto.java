package com.example.bpm.dto.document;

import lombok.Data;

import java.util.List;

@Data
public class DocumentListDto {

    DocumentDto documentDto;

    List<BlockDto> blockDtoList;

}
