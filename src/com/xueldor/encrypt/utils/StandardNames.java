/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xueldor.encrypt.utils;

/**
 *
 * @author xuexiangyu
 * refer to https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html
 * 算法标准名称
 */
public final class StandardNames {
    /**
     * MessageDigest Algorithms
     */
    static final public class MessageDigestName{
        public static final String MD2 = "MD2";
        public static final String MD5 = "MD5";
        public static final String SHA1 = "SHA-1";
        public static final String SHA224 = "SHA-224";
        public static final String SHA256 = "SHA-256";
        public static final String SHA384 = "SHA-384";
        public static final String SHA512 = "SHA-512";
    }
    
    /**
     * SSLContext Algorithms
     */
    static final public class SSLContextName{
        public static final String SSL = "SSL";
        public static final String SSLv2 = "SSLv2";
        public static final String SSLv3 = "SSLv3";
        public static final String TLS = "TLS";
        public static final String TLSv1 = "TLSv1";
        public static final String TLSv1_1 = "TLSv1.1";
        public static final String TLSv1_2 = "TLSv1.2";
    }
}
