package com.example.dentalclinicschedulingplatform.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        String serviceAccountPath = "/path/to/render/secret/firebase-service-account.json";

        FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://spring-dental-clinic-chat-default-rtdb.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }
}
