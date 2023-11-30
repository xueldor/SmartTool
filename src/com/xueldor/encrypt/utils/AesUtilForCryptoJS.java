/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xueldor.encrypt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 主要参考https://blog.csdn.net/koppel/article/details/129735097
 *
 * @author Administrator
 */
public class AesUtilForCryptoJS {

    public static String encrypt(String origin, String secret) {
        try {
            byte[] input = origin.getBytes(StandardCharsets.ISO_8859_1);
            // 以下两个变量不可修改，否则java加密的，js无法解密
            byte[] saltData = "DFބf$t:".getBytes(StandardCharsets.UTF_8);
            byte[] preData = "Salted__".getBytes(StandardCharsets.UTF_8);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[][] keyAndIV = generateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");

            aesCBC.init(Cipher.ENCRYPT_MODE, key, iv);

            byte[] encrypt = aesCBC.doFinal(input);
            byte[] encryptedData = addBytes(saltData, encrypt);
            return Base64.byteArrayToBase64(addBytes(preData, encryptedData));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedText, String secret) {
        // 参考文章：https://www.pianshen.com/question/31931314701/
        try {
            byte[] cipherData = Base64.base64ToByteArray(encryptedText);
            byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[][] keyAndIV = generateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);
            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedData = aesCBC.doFinal(encrypted);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("aesDecrypt error.param=" + encryptedText);
            e.printStackTrace();
            return null;
        }
    }

    private static byte[][] generateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {
        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;
        try {
            md.reset();
            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {
                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0) {
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                }
                md.update(password);
                if (salt != null) {
                    md.update(salt, 0, 8);
                }
                md.digest(generatedData, generatedLength, digestLength);
                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }
                generatedLength += digestLength;

            }

            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0) {
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);
            }
            return result;
        } catch (DigestException e) {
            throw new RuntimeException(e);
        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte) 0);
        }

    }

    private static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    public static byte[] getFileContent(String file) throws Exception {
        File f = new File(file);
        if (!f.exists()) {
            throw new FileNotFoundException(file);
        }

        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
                // System.out.println("reading");
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static int writeContent(String file, String text, boolean override) throws Exception {
        File f = new File(file);
        if (!override && f.exists()) {
            return 1;
        }
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(f);
            fs.write(text.getBytes(StandardCharsets.ISO_8859_1));
            fs.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return 2;
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
