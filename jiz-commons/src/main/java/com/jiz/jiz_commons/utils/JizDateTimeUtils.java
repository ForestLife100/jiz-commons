package com.jiz.jiz_commons.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JizDateTimeUtils {
	/**
	 * 解析UTC格式的字符串-> LocalDateTime
	 * @param text: 2022-02-22T00:00:00Z
	 * @return
	 */
	public static LocalDateTime parseUtcText(String text) {
		
		DateTimeFormatter df;
		if (text.endsWith("Z")) {
			df = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("Z"));
		}else {
			df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		}

		return LocalDateTime.parse(text, df);
	}
	
//	/**
//	 * 获取系统的本地时间
//	 * @return
//	 */
//	public static LocalDateTime getUTCDateTime() {
//		return  ZonedDateTime.now(ZoneId.of("Z")).toLocalDateTime();
//	}
//	
//	/**
//	 * 获取UTC时间Unix 时间戳
//	 * @return
//	 */
//	public static Long getUnixTimestamp() {
//		return ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli();
//	}
//
//	/**
//	 * 获取UTC时间的字符串
//	 * @return
//	 */
//	public static String getUTCDataTime() {
//		DateTimeFormatter df = DateTimeFormatter.ISO_INSTANT;
//		LocalDateTime dt = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
//		return df.format(dt);
//	}
//	
//	/**
//	 * 转换日期时间所在的区域
//	 * @param dateTimeInZone
//	 * @param zoneIdFrom
//	 * @param zoneIdTo
//	 * @return
//	 */
//	public static LocalDateTime convertDateTimeZone(LocalDateTime srcDateTime, ZoneId zoneIdFrom, ZoneId zoneIdTo) {
//		if (ObjectUtils.isEmpty(srcDateTime) || ObjectUtils.isEmpty(zoneIdFrom) || ObjectUtils.isEmpty(zoneIdTo)) {
//			return null;
//		};
//
//		ZonedDateTime withZone = srcDateTime.atZone(zoneIdFrom);
//
//		return withZone.withZoneSameInstant(zoneIdTo).toLocalDateTime();
//	}
//
//	/**
//	 * 转换日期时间所在的区域
//	 * @param dateTimeInZone
//	 * @param zoneIdFrom
//	 * @param zoneIdTo
//	 * @return
//	 */
//	public static LocalDateTime convertDateTimeZone(LocalDateTime srcDateTime, String zoneIdFrom, String zoneIdTo) {
//		if (ObjectUtils.isEmpty(srcDateTime) || ObjectUtils.isEmpty(zoneIdFrom) || ObjectUtils.isEmpty(zoneIdTo)) {
//			return null;
//		};
//
//		ZoneId zoneFrom = ZoneId.of(zoneIdFrom);
//		ZoneId zoneTo = ZoneId.of(zoneIdTo);
//
//		return convertDateTimeZone(srcDateTime, zoneFrom, zoneTo);
//	}
//
//	/**
//	 * 转换时间所在的区域
//	 * @param srcTime
//	 * @param zoneIdFrom
//	 * @param zoneIdTo
//	 * @return
//	 */
//	public static LocalTime convertTimeZone(LocalTime srcTime, ZoneId zoneIdFrom, ZoneId zoneIdTo) {
//		if (ObjectUtils.isEmpty(srcTime) || ObjectUtils.isEmpty(zoneIdFrom) || ObjectUtils.isEmpty(zoneIdTo)) {
//			return null;
//		};
//
//		LocalDateTime srcDateTime = LocalDateTime.of(LocalDate.now(zoneIdFrom), srcTime);
//
//		ZonedDateTime withZone = srcDateTime.atZone(zoneIdFrom);
//		return withZone.withZoneSameInstant(zoneIdTo).toLocalTime();
//	}
//
//	/**
//	 * 转换时间所在的区域
//	 * @param timeInZone
//	 * @param zoneIdFrom
//	 * @param zoneIdTo
//	 * @return
//	 */
//	public static LocalTime convertTimeZone(LocalTime srcTime, String zoneIdFrom, String zoneIdTo) {
//		if (ObjectUtils.isEmpty(srcTime) || ObjectUtils.isEmpty(zoneIdFrom) || ObjectUtils.isEmpty(zoneIdTo)) {
//			return null;
//		};
//
//		ZoneId zoneFrom = ZoneId.of(zoneIdFrom);
//		ZoneId zoneTo = ZoneId.of(zoneIdTo);
//		
//		return convertTimeZone(srcTime, zoneFrom, zoneTo);
//	}
//
//	/**
//	 * 将UTC时间转换为系统的本地时间
//	 * @param utcDataTime
//	 * @return
//	 */
//	public static LocalDateTime fromUTCToLocalSystem(LocalDateTime srcDataTime) {
//		if (ObjectUtils.isEmpty(srcDataTime)) {
//			return srcDataTime;
//		};
//	
//		return convertDateTimeZone(srcDataTime, ZoneId.of("UTC"), ZoneId.systemDefault());
//	}
//	
//	/**
//	 * 将系统的本地时间转换为UTC时间
//	 * @param srcDataTime
//	 * @return
//	 */
//	public static LocalDateTime fromLocalSystemToUTC(LocalDateTime srcDataTime) {
//		if (ObjectUtils.isEmpty(srcDataTime)) {
//			return srcDataTime;
//		};
//	
//		return convertDateTimeZone(srcDataTime, ZoneId.systemDefault(), ZoneId.of("UTC"));
//	}
//
//	/**
//	 * 获取时间差
//	 * @param inDateTime1
//	 * @param inDateTime2
//	 * @return
//	 */
//	public static long calcDiff(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
//		if (ObjectUtils.isEmpty(fromDateTime) || ObjectUtils.isEmpty(fromDateTime)) {
//			return 0;
//		};
//
//		long from = fromDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
//		long to = toDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
//		
//		return from - to;
//	}
//
//	// 判断是否在某个时间范围内
//	public static boolean inDateTimeRange_jp(LocalDateTime startJpDateTime, LocalDateTime endJpDateTime) {
//		if (ObjectUtils.isEmpty(startJpDateTime) || ObjectUtils.isEmpty(endJpDateTime)) {
//			return false;
//		};
//
//		LocalDateTime now = convertDate(JizDateTimeUtils.getLocalDateTime(), ZoneId.systemDefault(),
//				ZoneId.of("Asia/Tokyo"));
//		return now.isAfter(startJpDateTime) && now.isBefore(endJpDateTime);
//	}
//
//	// 判断是否已经过了开始时间
//	public static boolean isBefore_Now_jp(LocalDateTime startJpDateTime) {
//		if (ObjectUtils.isEmpty(startJpDateTime)) {
//			return false;
//		};
//
//		LocalDateTime now = convertDate(JizDateTimeUtils.getLocalDateTime(),
//				ZoneId.systemDefault(),
//				ZoneId.of("Asia/Tokyo"));
//		
//		return now.isBefore(startJpDateTime);
//	}
//	
//	public static boolean isAfter_Now_jp(LocalDateTime startJpDateTime) {
//		if (ObjectUtils.isEmpty(startJpDateTime)) {
//			return false;
//		};
//
//		LocalDateTime now = convertDate(JizDateTimeUtils.getLocalDateTime(),
//				ZoneId.systemDefault(),
//				ZoneId.of("Asia/Tokyo"));
//		
//		return now.isAfter(startJpDateTime);
//	}

}
