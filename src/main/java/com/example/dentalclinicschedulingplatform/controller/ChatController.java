package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.entity.ChatMessage;
import com.example.dentalclinicschedulingplatform.entity.ChatNotification;
import com.example.dentalclinicschedulingplatform.service.IChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final IChatMessageService chatMessageService;

    @MessageMapping("/chat")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DENTIST')")
    public void processMessage(@Payload ChatMessage chatMessage) throws ExecutionException, InterruptedException {
        ChatMessage savedMsg = chatMessageService.save(chatMessage).get();
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "/queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DENTIST')")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
                                                              @PathVariable String recipientId) throws ExecutionException, InterruptedException {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId).get());
    }
}
