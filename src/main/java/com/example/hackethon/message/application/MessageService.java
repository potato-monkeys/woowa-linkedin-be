package com.example.hackethon.message.application;

import com.example.hackethon.message.domain.Message;
import com.example.hackethon.message.domain.MessageRepository;
import com.example.hackethon.message.domain.MessageRoom;
import com.example.hackethon.message.domain.MessageRoomRepository;
import com.example.hackethon.message.dto.MessageResponse;
import com.example.hackethon.message.dto.MessageRoomResponse;
import com.example.hackethon.relation.domain.RelationAction;
import com.example.hackethon.relation.domain.RelationshipEdge;
import com.example.hackethon.relation.domain.RelationshipEdgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final RelationshipEdgeRepository edgeRepository;

    @Transactional
    public void sendMessage(Long currentUserId, Long targetUserId, String content) {
        MessageRoom room = roomRepository.findByUsers(currentUserId, targetUserId)
                .orElseGet(() -> roomRepository.save(new MessageRoom(currentUserId, targetUserId)));
        
        Message message = new Message(room.getId(), currentUserId, content);
        messageRepository.save(message);
        
        room.updateLastMessage(content);

        // Increase relationship edge weight when sending a message
        RelationshipEdge edge = edgeRepository.findEdge(Math.min(currentUserId, targetUserId), Math.max(currentUserId, targetUserId))
                .orElseGet(() -> edgeRepository.save(new RelationshipEdge(currentUserId, targetUserId)));
        
        edge.addWeight(RelationAction.MESSAGE.getWeight());
    }

    @Transactional(readOnly = true)
    public List<MessageRoomResponse> getRooms(Long currentUserId) {
        return roomRepository.findRoomsByUserId(currentUserId).stream().map(room -> {
            Long partnerId = room.getUser1Id().equals(currentUserId) ? room.getUser2Id() : room.getUser1Id();
            long unreadCount = messageRepository.countUnreadMessages(room.getId(), currentUserId);
            return new MessageRoomResponse(room.getId(), partnerId, room.getLastMessageContent(), unreadCount, room.getUpdatedAt());
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<MessageResponse> getMessages(Long currentUserId, Long targetUserId) {
        return roomRepository.findByUsers(currentUserId, targetUserId)
                .map(room -> {
                    messageRepository.markMessagesAsRead(room.getId(), currentUserId);
                    return messageRepository.findByRoomIdOrderByCreatedAtAsc(room.getId()).stream()
                            .map(m -> new MessageResponse(m.getId(), m.getSenderId(), m.getContent(), m.isRead(), m.getCreatedAt()))
                            .collect(Collectors.toList());
                })
                .orElse(List.of());
    }
}
