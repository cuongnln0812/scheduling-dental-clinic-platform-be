package com.example.dentalclinicschedulingplatform.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

//    @PostConstruct
//    public void init() throws IOException {
////        String serviceAccountPath = "/path/to/render/secret/firebase-service-account.json";
////        FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
//        FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setDatabaseUrl("https://spring-dental-clinic-chat-default-rtdb.firebaseio.com")
//                .build();
//
//        FirebaseApp.initializeApp(options);
//    }
    @PostConstruct
    public void init() throws IOException {
        String firebaseConfig = System.getenv("FIREBASE_CONFIG");
        if (firebaseConfig == null) {
            throw new IllegalArgumentException("Environment variable FIREBASE_CONFIG is not set");
        }

        ByteArrayInputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://spring-dental-clinic-chat-default-rtdb.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }
}
