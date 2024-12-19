package SERVER.Models;

import java.sql.Date;

public class EmailVerificationToken {

    int userId;
    String token;
    Date expiryTime;

    public EmailVerificationToken(int userId, String token, Date expiryTime) {
        this.userId = userId;
        this.token = token;
        this.expiryTime = expiryTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    @Override
    public String toString() {
        return "EmailVerificationToken{" +
                "userId=" + userId +
                ", token='" + token + '\'' +
                ", expiryTime=" + expiryTime +
                '}';
    }
}