package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.ChatRoom;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IChatRoomService {
    CompletableFuture<String> getChatRoomId(
            String senderId,
            String recipientId,
            boolean createNewRoomIfNotExists
    );
}
