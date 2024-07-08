package com.example.dentalclinicschedulingplatform.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    private String id;
    private String chatId;
    private String senderId;
    private String recipientId;
}
