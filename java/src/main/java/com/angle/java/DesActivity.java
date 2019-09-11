package com.angle.java;

import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class DesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_des);

        String content = "Hello Word";
        try {
            //1，获取cipher 对象
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            //2，通过秘钥对生成器KeyPairGenerator 生成公钥和私钥
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            //使用公钥进行加密，私钥进行解密（也可以反过来使用）
            PublicKey publicKey = keyPair.getPublic();
            System.out.println(Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT));
            PrivateKey privateKey = keyPair.getPrivate();
            //3,使用公钥初始化密码器
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //4，执行加密操作
            byte[] result = cipher.doFinal(content.getBytes());
            //使用私钥初始化密码器
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            //执行解密操作
            byte[] deResult = cipher.doFinal(result);
            System.out.println(new String(deResult));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
