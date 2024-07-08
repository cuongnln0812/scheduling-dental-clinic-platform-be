package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.ChatMessage;
import com.example.dentalclinicschedulingplatform.entity.ChatRoom;
import com.example.dentalclinicschedulingplatform.service.IChatService;
import com.google.firebase.database.FirebaseDatabase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class ChatService implements IChatService {

    private final FirebaseDatabase firebaseDatabase;

    public ChatService() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public void addChatMessage(ChatMessage chatMessage) {

    }

    @Override
    public void addChatRoom(ChatRoom chatRoom) {

    }
}
