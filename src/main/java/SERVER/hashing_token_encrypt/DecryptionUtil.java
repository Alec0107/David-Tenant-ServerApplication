package SERVER.hashing_token_encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DecryptionUtil {


    private static final String SECRET = "nicholasjanna107";

    public static String decrypt(String encryptedText) throws Exception {

        encryptedText = encryptedText.trim().replaceAll("[^A-Za-z0-9+/=]", "");

        // Step 1: Decode the Base64-encoded encrypted text
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);

        if (encryptedBytes.length % 16 != 0) {
            throw new IllegalArgumentException("Invalid encrypted data length");
        }
        // Step 2: Create a SecretKeySpec with the provided key
        SecretKeySpec keySpec = new SecretKeySpec(SECRET.getBytes("UTF-8"), "AES");

        // Step 3: Initialize the Cipher in DECRYPT_MODE
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        // Step 4: Perform decryption
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

}