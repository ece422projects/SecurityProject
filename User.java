import javax.crypto.spec.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.lang.Exception;
import java.net.URLDecoder;

import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.net.URLEncoder;
import java.net.URLDecoder;

import java.util.Base64;

public class User {

    private String userName;
    private String groupName;
 
    private SecretKey key;
    private String encodedKey;

    Cipher ecipher;
    Cipher dcipher;

    public User(String uName) {
        this.userName = uName;
        this.groupName = null;

       try {
            this.key = KeyGenerator.getInstance("AES").generateKey();
            this.encodedKey = Base64.getEncoder().encodeToString(this.key.getEncoded());

            this.ecipher = Cipher.getInstance("AES");
            this.dcipher = Cipher.getInstance("AES"); 
            this.ecipher.init(Cipher.ENCRYPT_MODE, key);
            this.dcipher.init(Cipher.DECRYPT_MODE, key);            
            
        }
        catch (Exception e) {
        }
    }

    public User(String uName, String groupName, String eKey) {
        this.userName = uName;
        this.groupName = groupName;

        try {
            this.encodedKey = eKey;
            byte[] decodedKey = Base64.getDecoder().decode(this.encodedKey);
            this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            this.ecipher = Cipher.getInstance("AES");
            this.dcipher = Cipher.getInstance("AES"); 
            this.ecipher.init(Cipher.ENCRYPT_MODE, key);
            this.dcipher.init(Cipher.DECRYPT_MODE, key); 

        } catch (Exception e) {

        }
    }

    public String getUserName() {
        return this.userName;
    }

    public void setGroupName(String gName) {
        this.groupName = gName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String encryptData(String rawData) {

        try {

            // byte[] utf8 = rawData.getBytes("UTF8");
            byte[] utf8 = rawData.getBytes("UTF-8");
            byte[] enc = this.ecipher.doFinal(utf8);

            // return Base64.getEncoder().encodeToString(enc);
            return URLEncoder.encode(Base64.getEncoder().encodeToString(enc), "UTF-8");

        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public String decryptData(String encryptedString) {

        try {

            String s = URLDecoder.decode(encryptedString, "UTF-8");
            byte[] dec = Base64.getDecoder().decode(s);
            byte[] utf8 = this.dcipher.doFinal(dec);

            return new String(utf8, "UTF8");
            // return new String(utf8, "ISO-8859-1");

        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}