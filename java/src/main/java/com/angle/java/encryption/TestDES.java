package com.angle.java.encryption;


import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 参考文章
 * https://blog.csdn.net/axi295309066/article/details/52491077
 * https://blog.csdn.net/u010989191/article/details/51298287
 */
public class TestDES {

    public static void main(String[] args) {
        /*
         * 这里注意一个问题：
         * 1. Input length must be multiple of 8 when decrypting with padded cipher
         * 这个异常是存在相应的转义字符
         * 2. Input length must be multiple of 8 when decrypting with padded cipher
         * 因为
         */
        String content = "DESTest";
        // 密码长度必须是8的倍数
        String password = "12345678";
        System.out.println("密　钥：" + password);
        System.out.println("加密前：" + content);
        byte[] result = encrypt(content, password);
        System.out.println("加密后：" + new String(result));
        String decryResult = decrypt(result, password);
        System.out.println("解密后：" + decryResult);
    }

    /**
     * 加密
     *
     * @param content 待加密内容
     * @param key     加密的密钥
     * @return
     */
    public static byte[] encrypt(String content, String key) {
        try {
            //生成随机数
            SecureRandom random = new SecureRandom();
            //生成一个密钥
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            //创建一个密钥工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //创建相应的算法，初始化对象
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            return cipher.doFinal(content.getBytes());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @param key     解密的密钥
     * @return
     */
    public static String decrypt(byte[] content, String key) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            byte[] result = cipher.doFinal(content);
            return new String(result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
