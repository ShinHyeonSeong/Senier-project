package com.example.bpm.entity.message;

import com.example.bpm.dto.message.MessageDto;
import com.example.bpm.entity.user.UserEntity;
import com.example.bpm.entity.project.data.ProjectEntity;
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

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "state")
    private int state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "send_uuid")
    private UserEntity userIdToMessageSend;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recv_uuid")
    private UserEntity userIdToMessageRecv;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private ProjectEntity projectIdToMessage;

}
