package SERVER.Handlers;

import SERVER.Models.UserDetails;
import SERVER.Repository.DatabaseDAO;
import SERVER.hashing_token_encrypt.JWTUtil;
import SERVER.hashing_token_encrypt.PasswordUtil;
import org.springframework.security.access.method.P;

import java.util.Date;
import java.util.Scanner;

public class AuthHandler{

    private static Scanner scanner = new Scanner(System.in);
    private String username;
    private String email;
    private String password;

    public UserDetails test() {

        System.out.println("Enter username: ");
        username = scanner.nextLine();
        System.out.println("Enter email: ");
        email = scanner.nextLine();
        System.out.println("Enter password: ");
        password = scanner.nextLine();

        String hashedPassword = PasswordUtil.hashPassword(password);

        UserDetails userDetails = new UserDetails(username, email, hashedPassword);

        return userDetails;
    }

}

class Test{
    private static Scanner scanner = new Scanner(System.in);
    private static DatabaseDAO databaseDAO = new DatabaseDAO();

    public static void main(String[] args) {
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt(); scanner.nextLine();

        if(choice == 1) {
            register();
        }else{
            login();
        }

    }


    public static void login(){
        System.out.println("Enter token: ");
        String token = scanner.nextLine();
        UserDetails userDetails = databaseDAO.getUserDetails(token.trim());
        System.out.println(userDetails.toString());


        boolean isValid = JWTUtil.isTokenValid(token, userDetails);
        if(isValid){
            System.out.println("Login successful");
        }else {
            System.out.println("Login failed");
        }


    }

    public static void register(){
        AuthHandler authHandler = new AuthHandler();
        databaseDAO = new DatabaseDAO();

        UserDetails userDetails = authHandler.test();

        //System.out.println(userDetails.getUsername());
        //System.out.println(userDetails.getEmail());
        //System.out.println(userDetails.getPassword());

        int userId =  databaseDAO.insertClient(userDetails.getUsername(), userDetails.getEmail(), userDetails.getPassword());

        String token = JWTUtil.generateToken(userId);
        Date expiry = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        databaseDAO.insertToken(userId, token, expiry);
        UserDetails userDetails1 = databaseDAO.getUserDetails(token.trim());


        System.out.println("Validate token? ");
        String choice = scanner.nextLine();

        if("yes".equals(choice)){
            System.out.println("Enter token: ");
            token = scanner.nextLine();


            if(JWTUtil.isTokenValid(token, userDetails1)){
                System.out.println("Token is valid");
            }else {
                System.out.println("Login failed");
            }
        }
    }
}

