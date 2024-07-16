package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.ChatMessage;
import com.example.dentalclinicschedulingplatform.entity.ChatRoom;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IChatMessageService {
    CompletableFuture<ChatMessage> save(ChatMessage chatMessage);
    CompletableFuture<List<ChatMessage>> findChatMessages(String senderId, String recipientId);
}
