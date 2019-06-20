/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xueldor.encrypt.controller;

import com.xueldor.encrypt.RenameJFrame.CellObj;
import com.xueldor.encrypt.utils.Base64;
import com.xueldor.encrypt.utils.HexUtil;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author xuexiangyu
 */
public class CustomRename {
    public Map<File,File> doBatchPreview(boolean recursion,boolean useBase64,boolean forward,ArrayList<CellObj> list,ArrayList<CellObj> outList,StringBuilder logSB){
        Map<File,File> renameMap = new LinkedHashMap<>();
        ArrayList<CellObj> previewList = new ArrayList<>();
        for (CellObj cellObj : list) {
            File f = cellObj.getFile();
            String name = cellObj.getName();
            String transferName = null;
            try{
                transferName = transferName(useBase64, forward, name);
                CellObj newCell = new CellObj(transferName, f);
                outList.add(newCell);
                
            }catch(Exception e){
                CellObj newCell = new CellObj("无法转换", f);
                outList.add(newCell);
                logSB.append(e.getLocalizedMessage()).append("\n");
            }
            if(f.isFile()){
                if(transferName != null && transferName.length() > 0){
                    renameMap.put(f, new File(f.getParentFile(), transferName));
                }
            }else{
                if(recursion){//是否递归子目录
                    recurseDir(useBase64, forward, f, renameMap,logSB);
                }else{
                    if(transferName != null && transferName.length() > 0){
                        renameMap.put(f, new File(f.getParentFile(), transferName));
                    }
                }
            }
        }
        return renameMap;
    }
    
    private void recurseDir(boolean useBase64,boolean forward,File curF,Map<File,File> renameMap,StringBuilder logSB){
        File[] listFiles = curF.listFiles();
        if(listFiles != null){
            for (File file : listFiles) {
                if(file.isDirectory()){
                    recurseDir(useBase64, forward, file, renameMap,logSB);
                }else{
                    try{
                        String transferName = transferName(useBase64, forward, file.getName());
                        renameMap.put(file, new File(file.getParentFile(), transferName));
                    }catch(Exception e){
                        logSB.append(e.getLocalizedMessage()).append("\n");
                    }
                }
            }
        }
        try{
            String transferName = transferName(useBase64, forward, curF.getName());
            renameMap.put(curF, new File(curF.getParentFile(), transferName));
        }catch(Exception e){
            logSB.append(e.getLocalizedMessage()).append("\n");
        }
    }
    
    private String transferName(boolean useBase64,boolean forward,String name){
        String aftername;
        if(useBase64){
            if(forward){
                aftername = Base64.byteArrayToBase64(name.getBytes(Charset.forName("UTF-8")));
            }else{
                aftername = new String(Base64.base64ToByteArray(name),Charset.forName("UTF-8"));
            }
        }else {
            if(forward){
                aftername = HexUtil.byte2HexStr(name.getBytes(Charset.forName("UTF-8")));
            }else{
                aftername = new String(HexUtil.hexStr2Bytes(name),Charset.forName("UTF-8"));
            }
        }
        return aftername;
    }
    public interface BatchResult{
        
    }
    
    public static class MapBean{
        
    }
}
