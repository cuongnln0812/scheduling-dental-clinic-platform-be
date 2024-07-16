package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.ChatMessage;
import com.example.dentalclinicschedulingplatform.service.IChatMessageService;
import com.example.dentalclinicschedulingplatform.service.IChatRoomService;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ChatMessageService implements IChatMessageService {

    private final FirebaseDatabase firebaseDatabase;
    private final IChatRoomService chatRoomService;

    public ChatMessageService(IChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public CompletableFuture<ChatMessage> save(ChatMessage chatMessage) {
        DatabaseReference chatMessagesRef = firebaseDatabase.getReference().child("chatMessages");
        return chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .thenApply(chatId -> {
                    DatabaseReference newMessageRef = chatMessagesRef.push();
                    String messageId = newMessageRef.getKey();
                    chatMessage.setChatId(chatId);
                    chatMessage.setId(messageId);
                    chatMessagesRef.setValueAsync(chatMessage);
                    return chatMessage;
                });
    }

    @Override
    public CompletableFuture<List<ChatMessage>> findChatMessages(String senderId, String recipientId) {
        DatabaseReference chatMessagesRef = firebaseDatabase.getReference().child("chatMessages");
        return chatRoomService.getChatRoomId(senderId, recipientId, false)
                .thenCompose(chatId -> {
                    if (chatId != null) {
                        Query query = chatMessagesRef.orderByChild("chatId").equalTo(chatId);
                        CompletableFuture<List<ChatMessage>> future = new CompletableFuture<>();
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<ChatMessage> messages = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                                    messages.add(chatMessage);
                                }
                                future.complete(messages);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                future.completeExceptionally(databaseError.toException());
                            }
                        });
                        return future;
                    } else {
                        return CompletableFuture.completedFuture(new ArrayList<>());
                    }
                });
    }
}
