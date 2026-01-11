package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.model.MailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ThirdPartyMailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from-email}")
    private String fromEmail;

    @Value("${mail.from-name:JobFinder}")
    private String fromName;

    private final String USER = "${userName}";

    /**
     * Tạo mã xác thực 6 số ngẫu nhiên
     */
    private String createCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Đọc và xử lý HTML template
     */
    private String processHtmlTemplate(MailTemplate template, String code) throws IOException {
        String emailTemplate = "/templates/verification.html";

        InputStream inputStream = getClass().getResourceAsStream(emailTemplate);

        if (inputStream == null) {
            throw new FileNotFoundException("File not found: " + emailTemplate);
        }

        String htmlContent = StreamUtils.copyToString(inputStream, Charset.defaultCharset());

        // Thay thế các placeholder
        String receiverName = template.getTo().split("@")[0];
        htmlContent = htmlContent.replace(USER, receiverName);
        htmlContent = htmlContent.replace("${useCase}", template.getUseCase());
        htmlContent = htmlContent.replace("${code}", code);

        return htmlContent;
    }

    /**
     * Gửi email với template
     */
    public String send(MailTemplate template) throws IOException,  MessagingException {
        // Tạo mã xác thực
        String code = createCode();

        // Xử lý HTML template
        String htmlContent = processHtmlTemplate(template, code);

        // Tạo MimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set thông tin email
        helper.setFrom(fromEmail, fromName);
        helper.setTo(template.getTo());
        helper.setSubject("JobFinder - " + template.getUseCase());
        helper.setText(htmlContent, true); // true = HTML

        // Gửi email
        try {
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + template.getTo());
            return code;
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Gửi email đơn giản (không dùng template)
     */
    public void sendSimpleEmail(String toEmail, String subject, String htmlBody) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, fromName);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        try {
            mailSender.send(message);
            System.out.println("Simple email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send simple email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Gửi email với CC và BCC
     */
    public void sendEmailWithCopy(String toEmail, String subject, String htmlBody,
                                  String[] cc, String[] bcc) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, fromName);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        if (cc != null && cc.length > 0) {
            helper.setCc(cc);
        }

        if (bcc != null && bcc.length > 0) {
            helper.setBcc(bcc);
        }

        mailSender.send(message);
    }
}
