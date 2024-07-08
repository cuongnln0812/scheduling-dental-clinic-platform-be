package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.ChatMessage;
import com.example.dentalclinicschedulingplatform.entity.ChatRoom;

public interface IChatService {
    void addChatMessage(ChatMessage chatMessage);
    void addChatRoom(ChatRoom chatRoom );
}
