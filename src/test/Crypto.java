package test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.swing.JOptionPane;

/**
 *
 * @author Achmad Baihaqi
 */
public class Crypto {
    
    private Cipher eCihpher;
    private SecretKey key;
    
    private int KEY_SIZE = 128, T_LEN =128;
    
    private void init(){
        try{
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(KEY_SIZE);
            key = gen.generateKey();
        }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error : " + ex.getMessage());
        }
    }
    
    private String encrypt(String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        byte[] msgBytes = msg.getBytes();
        eCihpher = Cipher.getInstance("AES/GCM/NoPadding");
        eCihpher.init(Cipher.ENCRYPT_MODE, key);
        byte[] eBytes = eCihpher.doFinal(msgBytes);
        return this.encode(eBytes);
    }
    
    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }
    
    private byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }
    
    private String decrypt(String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
        byte[] msgBytes = this.decode(msg);
        Cipher dCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, this.eCihpher.getIV());
        dCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] dBytes = dCipher.doFinal(msgBytes);
        return new String(dBytes);
    }
    
    public static void main(String[] args) throws Exception{
        
        Crypto cr = new Crypto();
        cr.init();
        String kata = "haqi1234";
        String encrypt = cr.encrypt(kata),
               e1 = cr.encrypt(kata),
               decrypt = cr.decrypt(encrypt),
               d1 = cr.decrypt(e1);
        
        System.out.println("Word : " + kata);
        System.out.println("Encrypt : " + encrypt);
        System.out.println("Decrypt : " + decrypt);
        System.out.println("Decrypt : " + d1);
//        
//        Crypto cr = new Crypto();
//        cr.init();
//        
//        String hash1 = cr.encrypt("terserah"),
//               hash2 = cr.encrypt("terserah");
//        
//        String pass1 = cr.decrypt(hash1),
//               pass2 = cr.decrypt(hash2);
//        
//        System.out.println("Encrypted : " + hash1);
//        System.out.println("Decrypted : " + pass1);
//        System.out.println("Decrypted : " + pass2);
    }
    
}