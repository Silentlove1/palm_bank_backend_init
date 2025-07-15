package com.life.bank.palm.service.user.utils;

import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AESEncryptUtil {

    private static final String AES_SALT = "kBi1MfC82EPLRYqg";
    private static final String AES_PKCS5P = "AES/ECB/PKCS5Padding";

    public static String encrypt(String str) {
        return encrypt(str, AES_SALT);
    }

    public static String decrypt(String str) {
        return decrypt(str, AES_SALT);
    }

    public static String encrypt(String str, String key) {
        if (StringUtils.isEmpty(key)) {
            throw new RuntimeException("key不能为空");
        }
        try {
            if (str == null) {
                return null;
            }
            byte[] raw = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(AES_PKCS5P);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            return new BASE64Encoder().encode(encrypted);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String decrypt(String str, String key) {
        if (StringUtils.isEmpty(key)) {
            throw new RuntimeException("key不能为空");
        }
        try {
            if (str == null) {
                return null;
            }
            byte[] raw = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(AES_PKCS5P);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted = new BASE64Decoder().decodeBuffer(str);
            byte[] original = cipher.doFinal(encrypted);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }
}