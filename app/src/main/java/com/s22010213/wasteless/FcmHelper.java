package com.s22010213.wasteless;


import  com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FcmHelper {
    private static final String SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
//    private static final String CREDENTIALS_FILE_PATH = "F://BSE/project/serviceAccountKey.json";
    private static GoogleCredentials googleCredentials;

    public static String getAccessToken(){
        try {
            String jsonString = String.valueOf(R.string.private_key);

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));

            if (googleCredentials == null){
                googleCredentials = GoogleCredentials.fromStream(stream)
                        .createScoped(Arrays.asList(SCOPE));
            }
            googleCredentials.refresh();;
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
