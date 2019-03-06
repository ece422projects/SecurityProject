import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class User {

    private String userName;
    private String password;
    private String encodedKey;

    private SecretKey key;
    private Cipher ecipher;
    private Cipher dcipher;

    public User(String uName, String password) {

        this.userName = uName;
        this.password = password;

       try {
            this.key = KeyGenerator.getInstance("AES").generateKey();
            this.encodedKey = Base64.getEncoder().encodeToString(this.key.getEncoded());

            this.ecipher = Cipher.getInstance("AES");
            this.dcipher = Cipher.getInstance("AES"); 
            this.ecipher.init(Cipher.ENCRYPT_MODE, this.key);
            this.dcipher.init(Cipher.DECRYPT_MODE, this.key);            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User(String uName, String password, String eKey) {

        this.userName = uName;
        this.password = password;

        try {
            this.encodedKey = eKey;
            byte[] decodedKey = Base64.getDecoder().decode(this.encodedKey);
            this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            this.ecipher = Cipher.getInstance("AES");
            this.dcipher = Cipher.getInstance("AES"); 
            this.ecipher.init(Cipher.ENCRYPT_MODE, this.key);
            this.dcipher.init(Cipher.DECRYPT_MODE, this.key);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public SecretKey getSecretKey() {
        return this.key;
    }

    public String getEncryptionKey() {
        return this.encodedKey;
    }

    public String encryptData(String rawData) {

        try {

            byte[] utf8 = rawData.getBytes("UTF-8");
            byte[] enc = this.ecipher.doFinal(utf8);

            return URLEncoder.encode(Base64.getEncoder().encodeToString(enc), "UTF-8");

        } catch (javax.crypto.BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decryptData(String encryptedString) {

        try {

            String s = URLDecoder.decode(encryptedString, "UTF-8");
            byte[] dec = Base64.getDecoder().decode(s);
            byte[] utf8 = this.dcipher.doFinal(dec);

            return new String(utf8, "UTF8");

        } catch (javax.crypto.BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}