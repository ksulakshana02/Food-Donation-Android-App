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
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"wasteless-c15c5\",\n" +
                    "  \"private_key_id\": \"d69423f351da2443448f23bb972782fce06aea8c\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDTFbIYaRE/2DZF\\nu7NoRg6sWkku6+C3GjrnqDEtu/dD1OrsqyNDygjUADR4mJC7IOEF1dWfwt2D67Bw\\nQwZZ0D7cQ9J/+yNRkKsDz7RRHLEKoXMZQYVWmHDgiq/ml7RyMY0jQGCjEhxUb73x\\nX1iMpZ8GPpntbbtFI1b/CNkH4yi0r8Nkr6TJWytSAODi7ycaZ5QN+GLlJIbGyxPy\\nscr4rNPqwdKcJWhe2sBDGWd0DCwnt/1k1bFAm8gE8WcqkcFOzS0E+FM4Y4iRr1+M\\n1ROtJcbIbCO/Vqo/gPnwM+TVy8PxwKO+PzRAm4yotZck1v/GdZyPFD6sA5gmJf1Y\\nAxZW7S/TAgMBAAECggEAB0rjwmx4KuyqggHoQq0k8Exq5ILur7S+yJEVfd8qvZOw\\nuBN43kHacznnQNat2AvclP8xva7ASMCkRwSccWQ8lQ8yHwzLSBPyQelI8VfHQAxF\\nizT/HneZcrsn35S/KFt7j6EM6vqtsuHCE1bixXKACiRbMgo3+D1ZJz95gV3A4sPI\\nVQpTuCtIj5WbMFnDbZ7MOE1IR0T81LKu41yw0q8h0JMw9DW7AiAvaJZQW8cbnJYG\\nNsO5gCbhdXL3oyIGLkXYUj2shmMpK1mxLAH2RFTzI1yGzcNvuPY2HszfxgdT3T9F\\nej+UJfrzeA7x+7XTmvZbUCNM4HjyrOSN3C6qkNDFQQKBgQD2ouKgtcC70aT5uzaR\\nyIxNPeharHrPHltBs13DohFr6xfGspnb/IkwLwIyR5w013iF6zpUeK0iryPEX5Ld\\nSKFRNMtFsgPHdrfHwXxLKHosEgihf9UQb8Nzf6BdPKKvifOYSySFfXEVnGxyatt3\\nvyshLc1nTJW1ywWJ13vhsrYSkwKBgQDbGUboxYfsXCPitnjbpBrzZYRBODMP8QEy\\nByT46u+L1gSDysoHwVQfK5VNGHKFxf2cDo8tiI2pFekZ5YKdXK7Ca5EWCR0dSluA\\nw0j/lfa4nvJUeun/+l+nAFvpaUjTXYP1vR5LKdsshIWqrFK1CKIYgSYDSXBa3VCl\\nKvQA5aR1wQKBgGyIsYJXeUEtuJPNNu5gep1jKOT+Ee0jRrVa4WA96by77/KYleyg\\n7R4vEaBesvbt+zOzmC+kC0zAFdVM2axXUeYSfYFmHROhwq49Dx1j6p+KMBIh1vks\\nta4V89QT4uOsqW3TY3b7BNClzXNsYrgHgEtRhJp4sUkW1pEaKoHfyw/RAoGABKPo\\naI2mb08UQ3zx2lUDbRw9TNRP4IqOJ+0Wz17Ka//AYLouxTNHANH/e36FDmg/EssM\\nJT67IVWhdjbyKTsJSHzWucy/nRsyDOwV47PdYdOb9kYUJqO62uKbD7p3HGLipJW4\\nyoa8aZj068Ryi/r+094IXBF2G7aMOrNKL6qtbQECgYA+QBveEMjc3SawBSb8UYtz\\nK2sJcmi/4D/sFB7TuzxgYfF/bMAi/g5kwWsyg4e/oJK0L0ilxcSObKnWdM48cvsK\\nL9YbjCJyWP11CtRcO4z1Hkhk8U3SAFsIWW+vh7mrugL6JFjBJUbFHR5ZsVneFVJJ\\n7FpKJzreooCToAPiLKZkPg==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-x5r3a@wasteless-c15c5.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"110649739880871583151\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-x5r3a%40wasteless-c15c5.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

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
