/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xueldor.encrypt.controller;

import com.xueldor.encrypt.EncryptJFrame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xuexiangyu
 */
public class CustromEncryt {
    EncryptJFrame mFrame;
    String[] files;
    String srcDirPath;
    String destDirPath;

    public CustromEncryt(EncryptJFrame frame) {
        mFrame = frame;
    }
    public void setFiles(String[] split,String destDir){
        files = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            files[i] = split[i].trim();
        }
        srcDirPath = new File(split[0]).getParent() + File.separator;
        if(!destDir.endsWith(File.separator)){
            destDir += File.separator;
        }
        destDirPath = destDir;
    }
    
    public void setFiles(String[] split){
        files = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            files[i] = split[i].trim();
        }
        srcDirPath = new File(split[0]).getParent() + File.separator;
    }
    
    public void doComplOriginFile() throws IOException{
       String[] split = files;
        for (String string : split) {
            File file = new File(string);
            if(file.isFile()){
                doComplOnOriginFile(string);
            }else if(file.isDirectory()){
                doComplDirOriginFile(string);
            }else{
                System.err.println("unknown file type,not file nor directory");
            }
        }
    }
    
     private void doComplDirOriginFile(String dir) throws IOException{
        File[] split = new File(dir).listFiles();
        for (File file : split) {
            if(file.isFile()){
                doComplOnOriginFile(file.getAbsolutePath());
            }else if(file.isDirectory()){
                doComplDirOriginFile(file.getAbsolutePath());
            }else{
                System.err.println("unknown file type,not file nor directory");
            }
        }
    }
    
    private void doComplOnOriginFile(String path) throws IOException{
        RandomAccessFile raf = null;
        byte buffer[] = new byte[1024*1024*16];
        int len;
        try {
            File pathFile = new File(path);
            raf = new RandomAccessFile(pathFile,"rw");
            while ((len = raf.read(buffer, 0, buffer.length)) != -1) {
                for (int i = 0; i < len; i++) {
                    buffer[i] = (byte)~buffer[i];
                }
                raf.seek(raf.getFilePointer() - len);
                raf.write(buffer, 0, len);
            }
        } finally{
            if(raf != null){
                raf.close();
            }
        }
    }
    
    public void doCompl() throws IOException{
       String[] split = files;
        for (String string : split) {
            File file = new File(string);
            if(file.isFile()){
                doComplEach(string);
            }else if(file.isDirectory()){
                doComplDir(string);
            }else{
                System.err.println("unknown file type,not file nor directory");
            }
        }
    }
    private void doComplDir(String dir) throws IOException{
        File[] split = new File(dir).listFiles();
        for (File file : split) {
            if(file.isFile()){
                doComplEach(file.getAbsolutePath());
            }else if(file.isDirectory()){
                doComplDir(file.getAbsolutePath());
            }else{
                System.err.println("unknown file type,not file nor directory");
            }
        }
    }
    private void doComplEach(String path) throws IOException{
        FileInputStream fis = null;
        FileOutputStream fos = null;
        byte buffer[] = new byte[1024*1024*16];
        int len;
        try {
            File pathFile = new File(path);
            String dstFileName = destDirPath + path.substring(srcDirPath.length());
            File dstFile = new File(dstFileName);
            dstFile.getParentFile().mkdirs();
            fos = new FileOutputStream(dstFile);
            fis = new FileInputStream(pathFile);
            while ((len = fis.read(buffer, 0, buffer.length)) != -1) {
                for (int i = 0; i < len; i++) {
                    buffer[i] = (byte)~buffer[i];
                }
                fos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CustromEncryt.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(fis != null){
                fis.close();
            }
            if(fos != null){
                fos.close();
            }
        }
    }
}
