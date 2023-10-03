package com.example.bpm.service;

import com.example.bpm.dto.message.MessageDto;
import com.example.bpm.dto.project.ProjectDto;
import com.example.bpm.dto.user.UserDto;
import com.example.bpm.entity.message.MessageEntity;
import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.user.UserEntity;
import com.example.bpm.repository.MessageRepository;
import com.example.bpm.repository.ProjectRepository;
import com.example.bpm.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@NoArgsConstructor(force = true)
public class MessageService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final MessageRepository messageRepository;

    /* send message */
    public void sendMessage(String title, String content, UserDto sendUser, String recvUUID, ProjectDto projectDto) {
        Date now = new Date();

        UserEntity recvUserEntity = userRepository.findById(recvUUID).get();
        UserEntity sendUserEntity = sendUser.toEntity();
        ProjectEntity projectEntity = projectDto.toEntity();

        MessageDto messageDto = new MessageDto(null, title, content, now, sendUserEntity, recvUserEntity, projectEntity);

        messageRepository.save(messageDto.toEntity());
    }

    /* recv message */
    public List<MessageDto> recvMessage(UserDto recvUser, ProjectDto projectDto) {

        List<MessageEntity> recvMessageList = messageRepository.findAllByProjectIdToMessage_ProjectIdAndUserIdToMessageRecv_Uuid(projectDto.getProjectId() ,recvUser.getUuid());
        List<MessageDto> messageDtoList = new ArrayList<>();

        for (MessageEntity messageEntity : recvMessageList) {

            MessageDto messageDto = new MessageDto();
            messageDto.insertEntity(messageEntity);
            messageDtoList.add(messageDto);

        }

        return messageDtoList;
    }

    /* open message */
    public MessageDto findMessage(Long id) {

        MessageDto messageDto = new MessageDto();
        messageDto.insertEntity(messageRepository.findById(id).get());

        return messageDto;
    }

    public void deleteMessage(Long id){

        MessageEntity messageEntity = messageRepository.findById(id).get();

        messageRepository.delete(messageEntity);
    }
}
