package com.kienluu.jobfinderbackend.service.implement;//package com.kienluu.jobfinderbackend.websocket.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.kienluu.jobfinderbackend.model.MailTemplate;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

@Service
public class MailService {
    @Value("${spring.application.name}")
    private String APP_NAME;
    @Value("${google.mail.username}")
    private String FROM;
    @Value("${google.mail.port}")
    private int PORT;
    @Value("${google.tokenPath}")
    private String TOKEN_PATH;

    @Value("${google.credential-path}")
    private String CREDENTIALS_FILE_PATH;

    @Value("${google.hostIP}")
    private String HOST_IP;

    private final String USER_ID = "admin";

    private final String USER = "${userName}";


    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        String CREDENTIAL_PATH = CREDENTIALS_FILE_PATH;

        InputStream in = getClass().getResourceAsStream(CREDENTIAL_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIAL_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKEN_PATH)))
                .setApprovalPrompt("force")
                .setAccessType("offline") //(1) Set the access type to offline so that the token can be refreshed
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(PORT)
                .setHost(HOST_IP)
                .setCallbackPath("/callback")
                .build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(USER_ID);
    }

    private Message createMessage(MailTemplate template, String code) throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(FROM));
        email.addRecipient(MimeMessage.RecipientType.TO,
                new InternetAddress(template.getTo()));
        email.setSubject("Jobfinder");
        //email.setText(template.getBody());
        String emailTemplate = "/templates/verification.html";

        InputStream inputStream = getClass().getResourceAsStream(emailTemplate); //(3)

        if (inputStream == null) {
            throw new FileNotFoundException("File not found: " + emailTemplate);
        }
        String htmlContent = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
        String receiverName = template.getTo().split("@")[0];
        htmlContent = htmlContent.replace(USER, receiverName);


        htmlContent = htmlContent.replace("${useCase}", template.getUseCase());
        htmlContent = htmlContent.replace("${code}",code);


        email.setContent(htmlContent, "text/html; charset=utf-8");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private String createCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public String send(MailTemplate template) throws GeneralSecurityException, IOException, MessagingException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);
//        var refreshToken = credential.getRefreshToken();
//        var accessToken = credential.getAccessToken();
//        log.info(refreshToken);
//        log.info("Access token: "+accessToken);
        String code = createCode();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APP_NAME)
                .build();

        Message message = createMessage(template,code);


        try {
            // Create send message
            service.users().messages().send("me", message).execute(); //(2)
            return code;

        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
        return "";

    }

}
