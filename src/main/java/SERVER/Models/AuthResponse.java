package SERVER.Models;

public class AuthResponse {

    private boolean isExist;
    private String message;
    private int userID;
    private String userName;

    public AuthResponse(boolean isExist, String message, int userID, String userName) {
        this.isExist = isExist;
        this.message = message;
        this.userID = userID;
        this.userName = userName;
    }

    public AuthResponse(boolean isExist, String message) {
        this.isExist = isExist;
        this.message = message;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SignupResponse{" +
                "isExist=" + isExist +
                ", message='" + message + '\'' +
                '}';
    }
}
