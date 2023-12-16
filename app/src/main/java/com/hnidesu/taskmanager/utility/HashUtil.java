package com.hnidesu.taskmanager.utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.zip.CRC32;

public class HashUtil {
    public static String MD5Digest(String text) throws Exception {
        MessageDigest md5= MessageDigest.getInstance("MD5");
        md5.update(text.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb=new StringBuilder();
        for (byte b:md5.digest()){
            String hex=String.format("%X",b);
            if(hex.length()==1)
                hex="0"+hex;
            sb.append(hex);
        }
        return sb.toString();
    }
    public static long CRC32Digest(CharSequence text) {
        CRC32 crc32=new CRC32();
        crc32.update(text.toString().getBytes(StandardCharsets.UTF_8));
        return crc32.getValue();
    }
}
