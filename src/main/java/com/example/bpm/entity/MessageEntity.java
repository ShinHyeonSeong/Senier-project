package com.example.bpm.entity;

import com.example.bpm.dto.MessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "send_uuid")
    private UserEntity userIdToMessageSend;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recv_uuid")
    private UserEntity userIdToMessageRecv;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private ProjectEntity projectIdToMessage;

    public static MessageEntity toMessageEntity(MessageDto messageDto) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessageId(messageDto.getMessageId());
        messageEntity.setTitle(messageDto.getTitle());
        messageEntity.setContent(messageDto.getContent());
        messageEntity.setDate(messageDto.getDate());
        messageEntity.setUserIdToMessageSend(messageDto.getUserIdToMessageSend());
        messageEntity.setUserIdToMessageRecv(messageDto.getUserIdToMessageRecv());
        messageEntity.setProjectIdToMessage(messageDto.getProjectIdToMessage());
        return messageEntity;
    }
}
