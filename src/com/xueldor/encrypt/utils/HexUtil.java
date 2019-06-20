package com.xueldor.encrypt.utils;

import java.util.Locale;

public class HexUtil {

	/**
	 * The digits for 16 radix.
	 */
	private static final char[] LOWER_CASE_DIGITS = { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static final char[] UPPER_CASE_DIGITS = { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 检查16进制字符串是否有效
	 * 
	 * @param sHex
	 *            String 16进制字符串
	 * @return boolean 以下情况返回false：参数是null;参数是空字符串；参数中含有非16进制的字符;参数长度是奇数
	 */
	public static boolean checkHexStr(String sHex) {
		boolean valid = false;
		if (sHex != null && sHex.length() > 0 && sHex.length() % 2 == 0) {
			valid = true;
			for (int i = 0; i < sHex.length(); i++) {
				char c = sHex.charAt(i);
				if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
						&& c <= 'f')) {
					valid = false;
					break;
				}
			}
		}
		return valid;
	}

	/**
	 * bytes转换成大写的十六进制字符串
	 * 
	 * @param b
	 *            byte[] byte数组
	 */
	public static String byte2HexStr(byte[] b) {
		StringBuilder sb = new StringBuilder();
		int len = b.length;
		for (int n = 0; n < len; n++) {
			sb.append(UPPER_CASE_DIGITS[(b[n] & 0xFF) >> 4]);
			sb.append(UPPER_CASE_DIGITS[b[n] & 0x0F]);
		}
		return sb.toString();
	}
	/**
	 * bytes转换成大写的十六进制字符串
	 * 
	 * @param b
	 *            byte[] byte数组
	 * @param iLen
	 *            int 数组有效数据长度，取前N位处理 N=iLen,必须满足iLen<=b.length
	 */
	public static String byte2HexStr(byte[] b, int iLen) {
		StringBuilder sb = new StringBuilder();
		int len = Math.min(iLen, b.length);
		for (int n = 0; n < len; n++) {
			sb.append(UPPER_CASE_DIGITS[(b[n] & 0xFF) >> 4]);
			sb.append(UPPER_CASE_DIGITS[b[n] & 0x0F]);
		}
		return sb.toString();
	}

	/**
	 * bytes转换成的十六进制字符串
	 * 
	 * @param b
	 *            byte[] byte数组
	 * @param iLen
	 *            int 数组有效数据长度，取前N位处理 N=iLen,必须满足iLen<=b.length
	 */
	public static String byte2HexStr(byte[] b, int iLen, boolean upperCase) {
		StringBuilder sb = new StringBuilder();
		int len = Math.min(iLen, b.length);
		char[] digits = null;
		if (upperCase) {
			digits = UPPER_CASE_DIGITS;
		} else {
			digits = LOWER_CASE_DIGITS;
		}
		for (int n = 0; n < len; n++) {
			sb.append(digits[(b[n] & 0xFF) >> 4]);
			sb.append(digits[b[n] & 0x0F]);
		}
		return sb.toString();
	}

	/**
	 * 16进制字符串转换为byte数组
	 * 
	 * @param src
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src) {
		//对输入值进行规范化整理
		src = src.trim().replace(" ", "").toUpperCase(Locale.US);
		if (!checkHexStr(src)) {
			throw new NumberFormatException("Hex format is not conformed!");
		}
		int len = src.length() / 2;
		byte[] ret = new byte[len]; // 分配存储空间
		int m,high,low;
		for (int i = 0; i < len; i++) {
			m = i* 2;
			high = src.charAt(m) - '0';
			if(high > 16){
				high -=7;//'A'-('9'+1)=7
			}
			low = src.charAt(m + 1) - '0';
			if(low > 16){
				low -=7;//'A'-('9'+1)=7
			}
			ret[i] = (byte)(high<<4 | low);
		}
		return ret;
	}
	

}
