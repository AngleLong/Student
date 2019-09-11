package com.angle.java.encryption;

import com.angle.java.utils.Base64Utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 演示非对称加密
 * //一次性加密数据的长度不能大于117 字节
 * private static final int ENCRYPT_BLOCK_MAX = 117;
 * //一次性解密的数据长度不能大于128 字节
 * private static final int DECRYPT_BLOCK_MAX = 128;
 * 非对称加密一般不会单独拿来使用，他并不是为了取代对称加密而出现的，非对称加密速度比对称加密慢很多，极端情况下会慢1000 倍，所以一般不会用来加密大量数据，
 * 通常我们经常会将对称加密和非对称加密两种技术联合起来使用，例如用非对称加密来给称加密里的秘钥进行加密（即秘钥交换）。
 * 参考文章：
 * https://blog.csdn.net/mafei852213034/article/details/53319908
 * https://blog.csdn.net/qq_35605213/article/details/80591869
 */
public class TestRSA {
    public static void main(String[] args) {
        String content = "Hello Word";
        try {
            //1，获取cipher 对象
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            //2，通过秘钥对生成器KeyPairGenerator 生成公钥和私钥
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            //使用公钥进行加密，私钥进行解密（也可以反过来使用）
            PublicKey publicKey = keyPair.getPublic();
            System.out.println("公钥Base64===>" + new String(Base64Utils.encode(publicKey.getEncoded())));
            PrivateKey privateKey = keyPair.getPrivate();
            System.out.println("私钥Base64===>" + new String(Base64Utils.encode(privateKey.getEncoded())));
            //3,使用公钥初始化密码器
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //4，执行加密操作
            byte[] result = cipher.doFinal(content.getBytes());
            //使用私钥初始化密码器
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            //执行解密操作
            byte[] deResult = cipher.doFinal(result);
            System.out.println(new String(deResult));

            PublicKey publicKeyNew = loadPublicKeyByStr(new String(Base64Utils.encode(publicKey.getEncoded())));
            System.out.println("原始公钥Base64===>" + new String(Base64Utils.encode(publicKey.getEncoded())));
            System.out.println("新的公钥Base64===>" + new String(Base64Utils.encode(publicKeyNew.getEncoded())));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * String转公钥PublicKey
     * @param key
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64Utils.decode(key.toCharArray());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 从字符串中加载RSA公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @return RSA公钥
     */
    public static PublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64Utils.decode(publicKeyStr.toCharArray());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("从字符串中加载RSA公钥异常");
        } catch (InvalidKeySpecException e) {
            throw new Exception("从字符串中加载RSA公钥异常");
        } catch (NullPointerException e) {
            throw new Exception("从字符串中加载RSA公钥异常");
        }
    }
}
