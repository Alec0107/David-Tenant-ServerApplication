package SERVER.Handlers;

import SERVER.Models.Account;
import SERVER.Models.EmailVerificationToken;
import SERVER.Repository.DatabaseDAO;
import SERVER.Service.EmailService;
import SERVER.Service.MailServiceInstance;
import SERVER.hashing_token_encrypt.JWTUtil;
import SERVER.hashing_token_encrypt.PasswordUtil;

import java.util.Date;
import java.util.UUID;


public class AuthHandler {

    private final EmailService emailService = new EmailService(MailServiceInstance.getEmailSender());


    public void registerUser(Account userAccount) {

        Account account = userAccount;

        String username = userAccount.getUsername();
        String email = userAccount.getEmail();
        String firstName = userAccount.getFirstName();
        String lastName = userAccount.getLastName();
        String hashedPassword = PasswordUtil.hashPassword(userAccount.getPassword());

        DatabaseDAO databaseDAO = new DatabaseDAO();
        int userId = databaseDAO.insertClient(username, email, hashedPassword, firstName, lastName);
        System.out.println("User ID: " + userId);

        createJWTToken(userId);
        createEmailToken(userId, firstName, email);

    }

    public void createJWTToken(int userId) {
        JWTUtil jwtUtil = new JWTUtil();
        String token = jwtUtil.generateToken(userId);
        System.out.println("Token: " + token);
        DatabaseDAO databaseDAO = new DatabaseDAO();

        Date expirationDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        databaseDAO.insertJWTToken(userId, token, expirationDate);

    }
    public void createEmailToken(int userId, String name,  String email) {
        String verificationToken = UUID.randomUUID().toString();

        DatabaseDAO databaseDAO = new DatabaseDAO();
        Date expirationDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);// 24HRS
        databaseDAO.insertEmailToken(userId, verificationToken, expirationDate);

        emailService.sendVerificationEmail(email, name, verificationToken);
    }

    public boolean verifyEmailToken(String token) {
        DatabaseDAO databaseDAO = new DatabaseDAO();

        // Retrieve the Token Details
        EmailVerificationToken verificationToken = databaseDAO.getEmailVerificationToken(token);

        if(verificationToken == null) {
            System.out.println("Invalid Token");
            return false;
        }

        // Check if the token has expired
        if(verificationToken.getExpiryTime().before(new Date(System.currentTimeMillis()))){
            System.out.println("Token Expired");
            return false;
        }

        // Mark the user as verified
        databaseDAO.markUserAsVerified(verificationToken.getUserId());
        return true;
    }




}

