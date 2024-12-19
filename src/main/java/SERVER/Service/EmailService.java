package SERVER.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


public class EmailService {

    private final JavaMailSender mailSender; // Dependency to send emails

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String name, String verificationToken) {

        try {
            String verificationLink = "http://192.168.0.150:8080/Verification?token=" + verificationToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);// Recipient's email
            message.setSubject("David Tenant Account Email Verification"); // Subject line
            message.setText("Hello " + name + ",\n\nPlease verify your email by clicking the link below:\n"
                    + verificationLink
                    + "\n\nIf you did not request this, please ignore this email.\n\nThank you!");

            // Step 3: Send the email
            mailSender.send(message);
            System.out.println("Verification email sent successfully to " + toEmail);
        }catch (Exception e){
            System.out.println("Verification email sent failed to " + toEmail);
            e.printStackTrace();
        }
    }

}
