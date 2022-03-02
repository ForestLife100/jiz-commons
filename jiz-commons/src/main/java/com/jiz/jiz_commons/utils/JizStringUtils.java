package com.jiz.jiz_commons.utils;

public class JizStringUtils {
	
//	public static String getUUID() {
//		UUID uuid = UUID.randomUUID();
//		
//		return uuid.toString();
//	}
//	
//	public static String MD5(String md5) {
//		try {
//			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
//			byte[] array = md.digest(md5.getBytes());
//			StringBuffer sb = new StringBuffer();
//			for (int i = 0; i < array.length; ++i) {
//				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
//			}
//			return sb.toString();
//		} catch (java.security.NoSuchAlgorithmException e) {
//		}
//		return null;
//	}
//
//	public static boolean checkEmailPattern(String email) {
//		String pattern = "^(?=.{1,254}$)(?=.{1,64}@)[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
//		Pattern regex = Pattern.compile(pattern);
//
//		Matcher matcher = regex.matcher(email);
//		return matcher.matches();
//	}
//	
//	public static boolean checkPhonePattern(String tel) {
//		String pattern = "^(?:\\d{10}|\\d{11}|\\d{3}-\\d{3}-\\d{4}|\\d{2}-\\d{4}-\\d{4}|\\d{3}-\\d{4}-\\d{4}|\\d{4}-\\d{2}-\\d{4}|\\d{4}-\\d{4}-\\d{2}|\\d{4}-\\d{3}-\\d{3})$";  
//	     Pattern regex = Pattern.compile(pattern);
//	     
//	     Matcher matcher = regex.matcher(tel);  
//	     return  matcher.matches();  
//	}
//	
//	public static boolean checkNumPattern(String num) {
//		String pattern = "^[0-9]+$";  
//	     Pattern regex = Pattern.compile(pattern);
//	     
//	     Matcher matcher = regex.matcher(num);  
//	     return  matcher.matches();
//	}
//	
//	public static boolean checkLength(String field, Integer minLength, Integer maxLength) {
//	     return  field.length() >= minLength && field.length() <= maxLength;
//	}
//
//	public static boolean checkValue(String value, Integer min, Integer max) {
//		Long intValue = Long.parseLong(value);
//		return  intValue >= min && intValue <= max;
//	}
//	
//	/**
//	 * 字符串->Long
//	 * @param value
//	 * @param defaultValue
//	 * @return
//	 */
//	public static Long longValueOf(String value, Long defaultValue) {
//		try {
//			if (ObjectUtils.isEmpty(value)) {
//				return defaultValue;
//			}
//			
//			return Long.valueOf(value);
//		} catch (Exception e) {
//		}
//
//		return defaultValue;
//	}
}
