package com.myApp.qrscannergenerator;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String password, String key) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        byte[] keyBytes = key.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "blowfish");
        Cipher cipher = Cipher.getInstance("blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        String encryptedtext = Base64.getEncoder().
                encodeToString(cipher.doFinal(password.getBytes("UTF-8")));
        return encryptedtext;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String encryptedtext, String key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        byte[] keyBytes = key.getBytes();
        SecretKeySpec secKeySpec = new SecretKeySpec(keyBytes, "blowfish");
        byte[] ecryptedtexttobytes = Base64.getDecoder().
                decode(encryptedtext);
        Cipher cipher = Cipher.getInstance("blowfish");
        cipher.init(Cipher.DECRYPT_MODE, secKeySpec);
        byte[] decrypted = cipher.doFinal(ecryptedtexttobytes);
        String decryptedString =
                new String(decrypted, Charset.forName("UTF-8"));
        return decryptedString;

    }

}
