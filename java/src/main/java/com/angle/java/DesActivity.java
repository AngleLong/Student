package com.angle.java;

import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class DesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_des);

        //加密
        byte[] encryption = encryption("helloWord");
        byte[] encodeBase64 = Base64.encode(encryption, Base64.DEFAULT);
        String encrypStr = new String(encodeBase64);
        System.out.println("加密后的数据为===》" + encrypStr);
        byte[] decrypt = decrypt(encrypStr);
        byte[] decodeStr = Base64.decode(decrypt, Base64.DEFAULT);
        System.out.println("解密后的数据为===》" + new String(decodeStr));
    }

    /**
     * 加密
     *
     * @param encryptionStr 加密字符串
     * @return 加密后的字符串
     */
    public static byte[] encryption(String encryptionStr) {
        //1,得到cipher 对象（可翻译为密码器或密码系统）
        try {
            Cipher cipher = Cipher.getInstance("DES");
            //2，创建秘钥
            SecretKey key = KeyGenerator.getInstance("DES").generateKey();
            //3，设置操作模式（加密/解密）
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //4，执行操作
            return cipher.doFinal(encryptionStr.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param decryptStr 待解密的字符串
     * @return 加密后的字符串
     */
    public static byte[] decrypt(String decryptStr) {
        //1,得到cipher 对象（可翻译为密码器或密码系统）
        try {
            Cipher cipher = Cipher.getInstance("DES");
            //2，创建秘钥
            SecretKey key = KeyGenerator.getInstance("DES").generateKey();
            //3，设置操作模式（加密/解密）
            cipher.init(Cipher.DECRYPT_MODE, key);
            //4，执行操作
            return cipher.doFinal(decryptStr.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
