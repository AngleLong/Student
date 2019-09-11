package com.angle.java.encryption;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public enum RSA {
    INSTANCE;

    private String PRIVATE_KEY = "/pkcs8_rsa_private_key.pem";

    private String PUBLIC_KEY = "/rsa_public_key.pem";

    /**
     * 随机生成RSA密钥对
     *
     * @param filePath 密钥文件存放路径
     */
    public void genKeyPair(String filePath) throws Exception {
        genKeyPair(filePath, PUBLIC_KEY, PRIVATE_KEY);
    }

    /**
     * 随机生成RSA密钥对
     *
     * @param filePath       密钥文件存放路径
     * @param publicKeyName  RSA公钥存放文件名
     * @param privateKeyName RSA私钥存放路径
     */
    public void genKeyPair(String filePath, String publicKeyName, String privateKeyName) throws Exception {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen;

        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new Exception("随机生成RSA密钥对异常");
            }
        }

        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("随机生成RSA密钥对异常");
        }
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(2048, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        try {
            // 得到公钥字符串
            String publicKeyString = new String(Base64.encode(publicKey.getEncoded(), Base64.DEFAULT));
            // 得到私钥字符串
            String privateKeyString = new String(Base64.encode(privateKey.getEncoded(), Base64.DEFAULT));
            // 将密钥对写入到文件
            FileWriter pubfw = new FileWriter(filePath + publicKeyName);
            FileWriter prifw = new FileWriter(filePath + privateKeyName);
            BufferedWriter pubbw = new BufferedWriter(pubfw);
            BufferedWriter pribw = new BufferedWriter(prifw);
            pubbw.write(publicKeyString);
            pribw.write(privateKeyString);
            pubbw.flush();
            pubbw.close();
            pubfw.close();
            pribw.flush();
            pribw.close();
            prifw.close();
        } catch (IOException e) {
            throw new Exception("随机生成RSA密钥对异常");
        }

    }


    /**
     * 从文件输入流中加载RSA公钥
     *
     * @param path 公钥输入流
     * @return RSA公钥
     */
    public RSAPublicKey loadPublicKeyByFile(String path) throws Exception {
        try {
            return loadPublicKeyByStr(loadKeyByFile(path));
        } catch (IOException e) {
            throw new Exception("从文件中输入流中加载RSA公钥异常");
        } catch (NullPointerException e) {
            throw new Exception("从文件中输入流中加载RSA公钥异常");
        }
    }


    /**
     * 从字符串中加载RSA公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @return RSA公钥
     */
    public RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("从字符串中加载RSA公钥异常");
        } catch (InvalidKeySpecException e) {
            throw new Exception("从字符串中加载RSA公钥异常");
        } catch (NullPointerException e) {
            throw new Exception("从字符串中加载RSA公钥异常");
        }
    }

    /**
     * 从文件输入流中加载RSA私钥
     *
     * @param path 私钥文件名
     * @return RSA私钥
     */
    public RSAPrivateKey loadPrivateKeyByFile(String path) throws Exception {
        try {
            return loadPrivateKeyByStr(loadKeyByFile(path));
        } catch (IOException e) {
            throw new Exception("从文件输入流中加载RSA私钥异常");
        } catch (NullPointerException e) {
            throw new Exception("从文件输入流中加载RSA私钥异常");
        }
    }

    /**
     * 从字符串中加载RSA私钥
     *
     * @param privateKeyStr 私钥数据字符串
     * @return RSA私钥
     */
    public RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decode(privateKeyStr,Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("从字符串中加载RSA私钥异常");
        } catch (InvalidKeySpecException e) {
            throw new Exception("从字符串中加载RSA私钥异常");
        } catch (NullPointerException e) {
            throw new Exception("从字符串中加载RSA私钥异常");
        }
    }

    /**
     * RSA公钥加密
     *
     * @param publicKey     公钥
     * @param plainTextData 明文
     * @return 密文
     */
    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        Cipher cipher;

        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainTextData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("RSA公钥加密异常");
        } catch (NoSuchPaddingException e) {
            throw new Exception("RSA公钥加密异常");
        } catch (InvalidKeyException e) {
            throw new Exception("RSA公钥加密异常");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("RSA公钥加密异常");
        } catch (BadPaddingException e) {
            throw new Exception("RSA公钥加密异常");
        }
    }

    /**
     * RSA私钥加密
     *
     * @param privateKey    私钥
     * @param plainTextData 明文
     * @return 密文
     */
    public byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(plainTextData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("RSA私钥加密异常");
        } catch (NoSuchPaddingException e) {
            throw new Exception("RSA私钥加密异常");
        } catch (InvalidKeyException e) {
            throw new Exception("RSA私钥加密异常");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("RSA私钥加密异常");
        } catch (BadPaddingException e) {
            throw new Exception("RSA私钥加密异常");
        }
    }

    /**
     * RSA私钥解密
     *
     * @param privateKey 私钥
     * @param cipherData 密文
     * @return 明文
     */
    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("RSA私钥解密异常");
        } catch (NoSuchPaddingException e) {
            throw new Exception("RSA私钥解密异常");
        } catch (InvalidKeyException e) {
            throw new Exception("RSA私钥解密异常");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("RSA私钥解密异常");
        } catch (BadPaddingException e) {
            throw new Exception("RSA私钥解密异常");
        }
    }

    /**
     * RSA公钥解密
     *
     * @param publicKey  公钥
     * @param cipherData 密文
     * @return 明文
     */
    public byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("RSA公钥解密异常");
        } catch (NoSuchPaddingException e) {
            throw new Exception("RSA公钥解密异常");
        } catch (InvalidKeyException e) {
            throw new Exception("RSA公钥解密异常");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("RSA公钥解密异常");
        } catch (BadPaddingException e) {
            throw new Exception("RSA公钥解密异常");
        }
    }

    /**
     * 从文件输入流中加载密钥
     *
     * @param path 文件路径
     * @return 文件所存储的KEY
     */
    private String loadKeyByFile(String path) throws IOException, NullPointerException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String readLine;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null) {
            if (readLine.charAt(0) == '-') {
                continue;
            } else {
                sb.append(readLine);
                sb.append('\r');
            }
        }
        br.close();
        return sb.toString();
    }
}
