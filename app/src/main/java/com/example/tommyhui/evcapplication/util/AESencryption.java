package com.example.tommyhui.evcapplication.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESencryption {

    private SecretKeySpec secretKey ;
    private byte[] key ;

    public AESencryption(String myKey) {
        setKey(myKey);
    }

    public void setKey(String myKey){
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            System.out.println(key.length);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            System.out.println(key.length);
            System.out.println(new String(key,"UTF-8"));
            secretKey = new SecretKeySpec(key, "AES");

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public String encrypt(String strToEncrypt) {
        String result = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            result = Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")), Base64.DEFAULT);
        }
        catch (Exception e){
            System.out.println("Error while encrypting: "+e.toString());
        }
        return result;
    }

    public String decrypt(String strToDecrypt) {
        String result = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            result = new String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: "+e.toString());
        }
        return result;
    }
}
