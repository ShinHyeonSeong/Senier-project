package com.example.bpm.dto;

import com.example.bpm.entity.MessageEntity;
import com.example.bpm.entity.ProjectEntity;
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
public class MessageDto {
    private Long messageId;

    private String title;

    private String content;

    private Date date;

    private UserEntity userIdToMessageSend;

    private UserEntity userIdToMessageRecv;

    private ProjectEntity projectIdToMessage;

    public MessageDto(String title, String content, Date date, UserEntity userIdToMessageSend, UserEntity userIdToMessageRecv, ProjectEntity projectIdToMessage) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.userIdToMessageSend = userIdToMessageSend;
        this.userIdToMessageRecv = userIdToMessageRecv;
        this.projectIdToMessage = projectIdToMessage;
    }

    public static MessageDto toMessageDto(MessageEntity messageEntity) {
        MessageDto messageDto = new MessageDto();
        messageDto.setMessageId(messageEntity.getMessageId());
        messageDto.setTitle(messageEntity.getTitle());
        messageDto.setContent(messageEntity.getContent());
        messageDto.setDate(messageEntity.getDate());
        messageDto.setUserIdToMessageSend(messageEntity.getUserIdToMessageSend());
        messageDto.setUserIdToMessageRecv(messageEntity.getUserIdToMessageRecv());
        messageDto.setProjectIdToMessage(messageEntity.getProjectIdToMessage());
        return messageDto;
    }


}
