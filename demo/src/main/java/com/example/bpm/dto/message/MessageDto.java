package com.example.bpm.dto.message;

import com.example.bpm.entity.message.MessageEntity;
import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Long requestId;

    private int state;

    private UserEntity userIdToMessageSend;

    private UserEntity userIdToMessageRecv;

    private ProjectEntity projectIdToMessage;

    public MessageEntity toEntity(){
        MessageEntity messageEntity = new MessageEntity();

        messageEntity.setMessageId(messageId);
        messageEntity.setTitle(title);
        messageEntity.setContent(content);
        messageEntity.setDate(date);
        messageEntity.setRequestId(requestId);
        messageEntity.setState(state);
        messageEntity.setUserIdToMessageSend(userIdToMessageSend);
        messageEntity.setUserIdToMessageRecv(userIdToMessageRecv);
        messageEntity.setProjectIdToMessage(projectIdToMessage);

        return  messageEntity;
    }

    public void insertEntity(MessageEntity messageEntity){
        this.messageId = messageEntity.getMessageId();
        this.title = messageEntity.getTitle();
        this.content = messageEntity.getContent();
        this.date = messageEntity.getDate();
        this.requestId = messageEntity.getRequestId();
        this.state = messageEntity.getState();
        this.userIdToMessageSend = messageEntity.getUserIdToMessageSend();
        this.userIdToMessageRecv = messageEntity.getUserIdToMessageRecv();
        this.projectIdToMessage = messageEntity.getProjectIdToMessage();
    }

}
