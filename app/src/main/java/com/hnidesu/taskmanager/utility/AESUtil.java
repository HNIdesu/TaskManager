package com.hnidesu.taskmanager.utility;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    public static final String KEY ="ANATANONAMAEDESU";
    public static String decrypt(String rawText) throws Exception {
        byte[] buffer= Base64.decode(rawText,Base64.DEFAULT);
        Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key=new SecretKeySpec(KEY.getBytes("utf-8"),"AES");
        cipher.init(Cipher.DECRYPT_MODE,key);
        return new String(cipher.doFinal(buffer),"utf-8");

    }

    public static String encrypt(String rawText) throws Exception {
        Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key=new SecretKeySpec(KEY.getBytes("utf-8"),"AES");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] buffer= cipher.doFinal(rawText.getBytes("utf-8"));
        buffer=Base64.encode(buffer,Base64.DEFAULT);
        return new String(buffer,"utf-8");
    }
}
