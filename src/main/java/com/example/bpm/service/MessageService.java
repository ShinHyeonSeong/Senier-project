package com.example.bpm.service;

import com.example.bpm.dto.MessageDto;
import com.example.bpm.dto.ProjectDto;
import com.example.bpm.dto.UserDto;
import com.example.bpm.entity.MessageEntity;
import com.example.bpm.entity.ProjectEntity;
import com.example.bpm.entity.UserEntity;
import com.example.bpm.repository.MessageRepository;
import com.example.bpm.repository.ProjectRepository;
import com.example.bpm.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@NoArgsConstructor(force = true)
public class MessageService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ProjectRepository projectRepository;
    @Autowired
    private final MessageRepository messageRepository;

    //메세지 보내기
    public void sendMessage(String title, String content, UserDto nowUuid, String recvName, ProjectDto nowProject) {
        Date now = new Date();
        UserEntity recvUser = userRepository.findByName(recvName);
        MessageDto messageDto = new MessageDto(title, content, now, UserEntity.toUserEntity(nowUuid), recvUser, ProjectEntity.toProjectEntity(nowProject));

        messageRepository.save(MessageEntity.toMessageEntity(messageDto));
        log.info("보내기 완료 (서비스)" + messageDto.toString());
    }

    //수신함 확인 (파라미터 = 현재 로그인된 user, 현재 접속된 project
    public List<MessageDto> selectAllRecv(UserDto recvUser, ProjectDto projectDto) {
        String recvUserUuid = recvUser.getUuid();
        Long projectId = projectDto.getProjectId();
        List<MessageEntity> recvMessageList = messageRepository.findAllByUserIdToMessageRecv_Uuid(recvUserUuid);
        List<MessageDto> messageDtos = new ArrayList<>();

        for (MessageEntity messageEntity : recvMessageList) {
            MessageDto messageDto = new MessageDto();
            messageDtos.add(messageDto.toMessageDto(messageEntity));
        }
        return messageDtos;
    }

    //발신함 확인
    public List<MessageDto> selectAllSend(UserDto sendUser, ProjectDto projectDto) {
        String sendUserUuid = sendUser.getUuid();
        Long projectId = projectDto.getProjectId();
        List<MessageEntity> recvMessageList = messageRepository.findAllByUserIdToMessageSend_Uuid(sendUserUuid);
        List<MessageDto> messageDtos = new ArrayList<>();

        for (MessageEntity messageEntity : recvMessageList) {
            MessageDto messageDto = new MessageDto();
            messageDtos.add(messageDto.toMessageDto(messageEntity));
        }
        return messageDtos;
    }

    //메세지 하나 확인하기
    public MessageDto selectMessage(Long id) {
        return MessageDto.toMessageDto(messageRepository.findById(id).get());
    }

}
