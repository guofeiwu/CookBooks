package com.wgf.cookbooks.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * author WuGuofei on 2017/5/3.
 * e-mail：guofei_wu@163.com
 */

public class Md5 {
    public static String hashMd5(String password){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for(byte b:bytes){
                String s = Integer.toHexString(b & 0xff);
                if(s.length() == 1){
                    stringBuilder.append("0");
                }
                stringBuilder.append(s);
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
