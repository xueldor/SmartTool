/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xueldor.encrypt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 *
 * @author xuexiangyu
 */
public class DigestUtil {
    
    public static final int MD5 =1;
    public static final String MD5_NAME = StandardNames.MessageDigestName.MD5;
    
    public static final int SHA1 =1<<1;
    public static final String SHA1_NAME = StandardNames.MessageDigestName.SHA1;
    
    public static final int SHA256 =1<<2;
    public static final String SHA256_NAME = StandardNames.MessageDigestName.SHA256;
    public static final int CRC32 =1<<3;
        
    
    /**
     * 计算文件的摘要
     *
     * @param fileStr
     *         待计算文件
     * @param digests 需要计算的算法
     * @param prog 进度回调
     *
     * @return 计算出的MD5值
     */
    public static Map<String,String> getFileDigests(String fileStr,int digests,Progress prog) {
        File file = null;
        if(fileStr == null || !(file = new File(fileStr)).isFile()){
            throw new RuntimeException("文件不存在或者不是普通文件");
        }
        long total = file.length();
        long finished  = 0;
        int progVal = 0;
        
        CRC32 crc32 = null;
        
        ArrayList<MessageDigest> digestArr = new ArrayList<>();
        ArrayList<String> digestNameArr = new ArrayList<>();
        FileInputStream in = null;
        byte buffer[] = new byte[1024*16];
        int len;
        try {
            if((digests & MD5) ==MD5){
                digestNameArr.add(MD5_NAME);
            }
            if((digests & SHA1) ==SHA1){
                digestNameArr.add(SHA1_NAME);
            }
            if((digests & SHA256) ==SHA256){
                digestNameArr.add(SHA256_NAME);
            }
            if((digests & CRC32) == CRC32){
                crc32 = new CRC32();
            }
            for (String name : digestNameArr) {
                MessageDigest instance = MessageDigest.getInstance(name);
                digestArr.add(instance);
            }
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                for (MessageDigest digest : digestArr) {
                    digest.update(buffer, 0, len);
                }
                if(crc32 != null){
                    crc32.update(buffer, 0, len);
                }
                //计算进度
                finished +=len;
                if(prog != null){
                    int newP = (int)(finished * 100/total);
                    if(newP != progVal){
                        progVal = newP;
                        prog.onProgress(progVal);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            throw new RuntimeException("IO异常");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不支持该算法");
        }finally{
            if(in != null){
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(DigestUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        Map<String,String> map = new LinkedHashMap<>();
        for (int i = 0; i < digestNameArr.size(); i++) {
            System.err.println(digestNameArr.get(i));
            map.put(digestNameArr.get(i), bytesToHexString(digestArr.get(i).digest()));
        }
        if(crc32 != null){
            String val = Long.toHexString(crc32.getValue());
            for(int i = 0;i < 8 - val.length();i++){
                val = "0" + val;
            }
            map.put("CRC32", val.toUpperCase());
        }
       return map;
    }
    
    
    /**
     * 计算文件的 MD5值
     *
     * @param fileStr
     *         待计算文件
     * @param prog
     *         进度回调
     *
     * @return 计算出的MD5值
     */
    public static String getMd5ByFile(String fileStr,Progress prog) {
        File file = null;
        if(fileStr == null || !(file = new File(fileStr)).isFile()){
            return null;
        }
        long total = file.length();
        long finished  = 0;
        int progVal = 0;
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024*16];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                digest.update(buffer, 0, len);
                finished +=len;
                if(prog != null){
                    int newP = (int)(finished * 100/total);
                    if(newP != progVal){
                        progVal = newP;
                        prog.onProgress(progVal);
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    private static String getDigestName(int label) {
        String name;
        switch(label){
            case MD5:
                name = MD5_NAME;
                break;
            case SHA1:
                name = SHA1_NAME;
                break;
            case SHA256:
                name = SHA256_NAME;
                break;
            default:
                name = "";
                break;
        }
        return name;
    }
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase());
        }
        return stringBuilder.toString();
    }
    public interface Progress{
        void onProgress(int prog);
    }
}
