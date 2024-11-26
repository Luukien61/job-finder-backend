package com.kienluu.jobfinderbackend.websocket.mapper;

import com.kienluu.jobfinderbackend.websocket.dto.ChatMessageDto;
import com.kienluu.jobfinderbackend.websocket.dto.ConversationDto;
import com.kienluu.jobfinderbackend.websocket.entity.ChatMessage;
import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
import org.mapstruct.*;

@Mapper
public interface MessageMapper {


    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "senderId", target = "sender.id")
    Conversation toEntity(ConversationDto conversationDto);

    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    Conversation toConversation(ConversationDto conversationDto);

    @InheritInverseConfiguration(name = "toEntity")
    ConversationDto toDto(Conversation conversation);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Conversation partialUpdate(ConversationDto conversationDto, @MappingTarget Conversation conversation);

    ChatMessage toEntity(ChatMessageDto chatMessageDto);

    ChatMessageDto toDto(ChatMessage chatMessage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ChatMessage partialUpdate(ChatMessageDto chatMessageDto, @MappingTarget ChatMessage chatMessage);
}
