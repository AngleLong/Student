package com.angle.java.encryption;


import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 参考文章
 * https://blog.csdn.net/axi295309066/article/details/52491077
 * https://blog.csdn.net/u010989191/article/details/51298287
 */
public class TestDES {

    public static void main(String[] args) {
        /*==================DES加密==================*/
        /*
         * 这里注意一个问题：
         * 1. Input length must be multiple of 8 when decrypting with padded cipher
         * 秘钥的长度必须是8的整数倍,这里可以使用随机数进行相应的加密解密
         */
        System.out.println("===============以下是DES加密===============");
        String content = "DESTest";
        // 密码长度必须是8的倍数
        String password = "12345678";
        System.out.println("密　钥：" + password);
        System.out.println("加密前：" + content);
        byte[] result = encryptDES(content, password);
        System.out.println("加密后：" + new String(result));
        String decryResult = decryptDES(result, password);
        System.out.println("解密后：" + decryResult);

        /*==================AES加密==================*/
        System.out.println("===============以下是AES加密===============");
        String contentAes = "AESTest";
        String passwordAes = "12345678";
        System.out.println("密　钥：" + passwordAes);
        System.out.println("加密前：" + contentAes);
        byte[] resultAes = encodeAES(passwordAes, contentAes);
        System.out.println("加密后：" + new String(resultAes));
        byte[] decryResultAes = decryptAes(passwordAes, resultAes);
        System.out.println("解密后：" + new String(decryResultAes));
    }

    /**
     * 加密(DES)
     *
     * @param content 待加密内容
     * @param key     加密的密钥(长度必须是8的整数倍)
     * @return 加密后的字节数组
     */
    public static byte[] encryptDES(String content, String key) {
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
            return cipher.doFinal(content.getBytes("utf-8"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密(DES)
     *
     * @param content 待解密内容
     * @param key     解密的密钥(长度必须是8的整数倍)
     * @return 加密后的字节数组
     */
    public static String decryptDES(byte[] content, String key) {
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

    /**
     * 加密
     * 1.构造密钥生成器
     * 2.根据ecnodeRules规则初始化密钥生成器
     * 3.产生密钥
     * 4.创建和初始化密码器
     * 5.内容加密
     * 6.返回字符串
     *
     * @param key 秘钥
     */
    public static byte[] encodeAES(String key, String content) {
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            keygen.init(128, random);
            //3.产生原始对称密钥
            SecretKey originalKey = keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] raw = originalKey.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey keyAes = new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, keyAes);
            //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byteEncode = content.getBytes("utf-8");
            //9.根据密码器的初始化方式--加密：将数据加密
            return cipher.doFinal(byteEncode);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解密
     * 解密过程：
     * 1.同加密1-4步
     * 2.将加密后的字符串反纺成byte[]数组
     * 3.将加密内容解密
     */
    public static byte[] decryptAes(String key, byte[] content) {
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            keygen.init(128, random);
            //3.产生原始对称密钥
            SecretKey originalKey = keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] raw = originalKey.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey keyAes = new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, keyAes);
            //解密
            return cipher.doFinal(content);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }
}
