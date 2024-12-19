package SERVER.hashing_token_encrypt;

import OLD.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JWTUtil {

    private static final String SECRET = "your-static-secure-key-to-be-stored-safely";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public static String generateToken(int userId){
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + 24 * 60 * 60 * 1000; // 24HRS

        String tokenString = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(expirationTimeMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        return tokenString;
    }


    // To Check and Validate if Token is still valid or expired
    public static boolean isTokenValid(String token, UserDetails userDetails){

        try{
            Claims claims = getClaimsFromToken(token);
            int userId = Integer.parseInt(claims.getSubject());
            System.out.println("Extracted user ID: " + userId);
            Date expirationDate = claims.getExpiration();
            System.out.println("Token expiration date: " + expirationDate);
            System.out.println("Current date: " + new Date());

            if (userId == userDetails.getId() && expirationDate.after(new Date())) {
                return true;
            } else {
                System.out.println("Token is invalid: User ID or expiration mismatch.");
            }

        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static Claims getClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
