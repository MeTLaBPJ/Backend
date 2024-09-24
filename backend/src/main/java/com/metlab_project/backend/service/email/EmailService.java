package com.metlab_project.backend.service.email;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.metlab_project.backend.domain.entity.email.EmailAuth;
import com.metlab_project.backend.repository.email.EmailAuthRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final EmailAuthRepository emailAuthRepository;
    private final JavaMailSender emailSender;
    private final String ePw = createKey();

    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to); // to => 보내는 대상
        message.setSubject("인피 회원가입 이메일 인증"); // 메일 제목

        // 메일 내용
        String msgg = "<div style='margin:100px;'>";
        msgg += "<h1>인천대학교 인피</h1>";
        msgg += "<h1 style='color:blue;'>인증번호</h1> <h1> 안내 메일입니다.</h1>";
        msgg += "<br>";
        msgg += "<p>인피에 오신 것을 환영합니다!</p>";
        msgg += "<br>";
        msgg += "<p>해당 이메일은 회원가입을 위한 인증번호 안내 메일입니다.</p>";
        msgg += "<br>";
        msgg += "<p>하단 인증번호를 '이메일 인증번호' 칸에 입력하여 가입을 완료해주세요.</p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana;'>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>" + ePw + "</strong><div><br/>";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("metlabpj@naver.com", "metlabpj"));

        return message;
    }

    public String createKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            int index = rnd.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97)); // 영어 소문자
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65)); // 영어 대문자
                    break;
                case 2:
                    key.append(rnd.nextInt(10)); // 숫자
                    break;
            }
        }

        return key.toString();
    }

    public String sendSimpleMessage(String to) throws Exception {
        MimeMessage message = createMessage(to);

        try {
            emailSender.send(message); // 메일 발송
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException("메일 전송에 실패했습니다.");
        }

        EmailAuth emailAuth = new EmailAuth(ePw, to);
        emailAuthRepository.save(emailAuth);
        return ePw; // 메일로 보냈던 인증 코드를 서버로 반환
    }
}