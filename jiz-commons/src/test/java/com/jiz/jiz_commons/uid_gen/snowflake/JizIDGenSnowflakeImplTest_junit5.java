package com.jiz.jiz_commons.uid_gen.snowflake;
//package com.jiz.jiz_commons.uid_gen.snowflake;
//
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.when;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZoneOffset;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.logging.Logger;
//
//import org.hamcrest.MatcherAssert;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.RepeatedTest;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import org.mockito.Mockito;
//import org.mockito.internal.util.MockUtil;
//import org.mockito.internal.util.reflection.ReflectionMemberAccessor;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.jiz.jiz_commons.uid_gen.JizIDGenReslut;
//import com.jiz.jiz_commons.uid_gen.JizIDGenStatus;
//import com.jiz.jiz_commons.utils.JizConcurrencyUtils;
//import com.jiz.jiz_commons.utils.JizDateTimeUtils;
//
//import org.hamcrest.Matchers;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("创建全局唯一ID")
////@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class JizIDGenSnowflakeImplTest {
//	static final Logger logger = Logger.getLogger(JizIDGenSnowflakeImplTest.class.getName());
//
//	JizIDGenSnowflakeImpl idGenerator;
//
//	@BeforeEach
//	void setUp() throws Exception {
//		idGenerator = new JizIDGenSnowflakeImpl();
//	}
//
//	@Nested
//	@DisplayName("初始化")
//	class Init {
//		@Test
//		@DisplayName("Id的最大bit数")
//		void init_checkMaxBits() {
//			JizIDGenStatus stauts = idGenerator.initDataBits(48, 2, 2, 12);
//
//			Assertions.assertEquals(stauts, JizIDGenStatus.err_init, "初始化Id的最大bit数失败。");
//		}
//
//
//		@Test
//		@DisplayName("起始的时间戳:毫秒级")
//		void init_startTimeStamp_inMillisecond() {
//			LocalDateTime destStartTimeStamp = JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00");
//
//			idGenerator.initData(1, 1, destStartTimeStamp);
//
//			JizIDGenStatus stauts = idGenerator.initGenerator();
//			Assertions.assertEquals(stauts, JizIDGenStatus.success, "初始化全局Id生成器失败。");
//
//			LocalDateTime startTimeStamp = idGenerator.getStartTimeStampUTC();
//			Assertions.assertEquals(destStartTimeStamp, startTimeStamp, "初始化时间失败。");
//		}
//		
//		@Test
//		@DisplayName("起始的时间戳:秒级")
//		void init_startTimeStamp_inSecond() {
//			LocalDateTime destStartTimeStamp = JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00");
//
//			idGenerator.initData(1, 1, destStartTimeStamp, false);
//
//			JizIDGenStatus stauts = idGenerator.initGenerator();
//			Assertions.assertEquals(stauts, JizIDGenStatus.success, "初始化全局Id生成器失败。");
//
//			LocalDateTime startTimeStamp = idGenerator.getStartTimeStampUTC();
//			Assertions.assertEquals(destStartTimeStamp, startTimeStamp, "初始化时间失败。");
//		}
//		
//		@Test
//		@DisplayName("数据中心ID")
//		void init_dataCenterId() {
//			LocalDateTime destStartTimeStamp = JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00");
//			
//			idGenerator.initDataBits(47, 2, 2, 12);
//			idGenerator.initData(5, 1, destStartTimeStamp);
//			JizIDGenStatus stauts = idGenerator.initGenerator();
//			
//			Assertions.assertEquals(stauts, JizIDGenStatus.err_init, "初始化数据中心ID失败。");
//		}
//
//		@Test
//		@DisplayName("机器标识ID")
//		void init_machineId() {
//			LocalDateTime destStartTimeStamp = JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00");
//			
//			idGenerator.initDataBits(47, 2, 2, 12);
//			idGenerator.initData(1, 5, destStartTimeStamp);
//			JizIDGenStatus stauts = idGenerator.initGenerator();
//
//			Assertions.assertEquals(stauts, JizIDGenStatus.err_init, "初始化机器标识ID失败。");
//		}
//
//	}
//	
//	@Nested
//	@DisplayName("多线程处理-时间戳为毫秒级")
//	class MultiThreadInMillisecond {
//		int numberOfThreads = 10000;
//		int numberOfThreadPool = 100;
//
//		List<Long> createdIds = Collections.synchronizedList(new ArrayList<Long>());
//
//		@BeforeEach
//		@DisplayName("初始化全局Id生成器")
//		void setUp() throws Exception {
//			numberOfThreads = 50;
//			numberOfThreadPool = 50;
//
////			idGenerator = new JizIDGenSnowflakeImpl();
//			createdIds = Collections.synchronizedList(new ArrayList<Long>());
//
//			JizIDGenStatus stauts = idGenerator.initGenerator();
//			Assertions.assertEquals(stauts, JizIDGenStatus.success, "初始化全局Id生成器失败。");
//		}
//
//		@AfterEach
//		@DisplayName("处理结果")
//		void tearDown() throws Exception {
//			Assertions.assertEquals(numberOfThreads, createdIds.size(), "预计生成的全局Id数不正确。");
//
//			for (int index = 0; index < createdIds.size(); index++) {
//				final long currValue = createdIds.get(index);
//				final long count = createdIds.stream().filter(item -> item == currValue).count();
//
//				Assertions.assertEquals(1, count, String.format("重复的ID(%d)数是：%d个。", currValue, count));
//			}
//		}
//
//		@Test
//		@DisplayName("多线程异步处理：同时创建多个全局Id")
//		void createGlobaIds() throws Exception {
//			// 同步测试
//			Long testCount = JizConcurrencyUtils.testConcurrency(numberOfThreads, numberOfThreadPool, (param) -> {
//
//				JizIDGenReslut result = idGenerator.nextId(null);
//				Assertions.assertEquals(result.getStatus(), JizIDGenStatus.success, "创建全局Id失败。");
//
//				createdIds.add(result.getCode());
//
//			}, null);
//
//			Assertions.assertEquals(0, testCount, "同步线程没有全部完成。");
//		}
//	}
//
//	@Nested
//	@DisplayName("多线程处理-时间戳为秒级")
//	class MultiThread {
//		int numberOfThreads = 100000;
//		int numberOfThreadPool = 1000;
//		List<Long> createdIds = Collections.synchronizedList(new ArrayList<Long>());
//
//		@BeforeEach
//		@DisplayName("初始化全局Id生成器")
//		void setUp() throws Exception {
//			numberOfThreads = 100;
//			numberOfThreadPool = 100;
//
////			idGenerator = new JizIDGenSnowflakeImpl();
//			createdIds = Collections.synchronizedList(new ArrayList<Long>());
//
//			LocalDateTime destStartTimeStamp = JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00");
//			idGenerator.initData(1, 1, destStartTimeStamp, false);
//
//			JizIDGenStatus stauts = idGenerator.initGenerator();
//			Assertions.assertEquals(stauts, JizIDGenStatus.success, "初始化全局Id生成器失败。");
//		}
//
//		@AfterEach
//		@DisplayName("处理结果")
//		void tearDown() throws Exception {
//			Assertions.assertEquals(numberOfThreads, createdIds.size(), "预计生成的全局Id数不正确。");
//
//			for (int index = 0; index < createdIds.size() - 1; index++) {
//				final long privateValue = createdIds.get(index);
//				final long currValue = createdIds.get(index + 1);
//
//				MatcherAssert.assertThat(String.format("生成的全局Id不是递增序列:%d < %d。", privateValue, currValue), privateValue,
//						Matchers.lessThan(currValue));
//			}
//		}
//
//		@Test
//		@DisplayName("多线程异步处理：同时创建多个全局Id")
//		void createGlobaIds() throws Exception {
//
//			// 初始的时候，序列号为最大值
//			Field fieldSequence = JizIDGenSnowflakeImpl.class.getDeclaredField("sequence");
//			fieldSequence.setAccessible(true);
//
//			Field fieldSequenceMask = JizIDGenSnowflakeImpl.class.getDeclaredField("sequenceMask");
//			fieldSequenceMask.setAccessible(true);
//
//			long sequenceMask = fieldSequenceMask.getLong(idGenerator);
//
//			long sequence = -1L & sequenceMask;
//			fieldSequence.set(idGenerator, sequence - 1);
//
//			// 同步测试
//			Object syncObject = new Object();
//			Long testCount = JizConcurrencyUtils.testConcurrency(numberOfThreads, numberOfThreadPool,
//					(syncObjectParam) -> {
//						synchronized (syncObjectParam) {
//							JizIDGenReslut result = idGenerator.nextId(null);
//							Assertions.assertEquals(result.getStatus(), JizIDGenStatus.success, "创建全局Id失败。");
//
//							createdIds.add(result.getCode());
//						}
//					}, syncObject);
//
//			Assertions.assertEquals(0, testCount, "同步线程没有全部完成。");
//		}
//	}
//
//	@Nested
//	@DisplayName("异常系")
//	class ExceptionTest {
//		@BeforeEach
//		void setUp() throws Exception {
////			idGenerator = new JizIDGenSnowflakeImpl();
//		}
//
//		@Test
//		@DisplayName("起始的时间戳")
//		void exception_init_StartTimeStamp() {
//			LocalDateTime destStartTimeStamp = JizDateTimeUtils.parseUtcText("6022-02-22T00:00:00");
//
//			idGenerator.initData(1, 1, destStartTimeStamp);
//			JizIDGenStatus result = idGenerator.initGenerator();
//
//			Assertions.assertEquals(result, JizIDGenStatus.err_init, "起始的时间戳：超过了当前时间");
//		}
//
//		@Test
//		@DisplayName("起始的时间戳")
//		void exception_next_StartTimeStamp() {
//			LocalDateTime destStartTimeStamp = JizDateTimeUtils.parseUtcText("6022-02-22T00:00:00");
//
//			idGenerator.initDataBits(47, 2, 2, 12);
//			idGenerator.initData(1, 1, destStartTimeStamp);
//			idGenerator.initGenerator();
//
//			RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
//				idGenerator.nextId(null);
//			}, "起始的时间戳：超过了当前时间");
//			Assertions.assertNotNull(exception, "起始的时间戳：超过了当前时间");
//		}
//	}
//
////	@Nested
////	@DisplayName("模拟系")
////	@PrepareForTest(JizIDGenSnowflakeImpl.class)
////	class MockTest {
////		@BeforeEach
////		void setUp() throws Exception {
////			idGenerator = Mockito.spy(JizIDGenSnowflakeImpl.class);
////
////		}
////
////		@Test
////		@DisplayName("起始的时间戳")
////		void exceptionTesting()
////				throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
////
////			LocalDateTime startTimeStamp = JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00");
////			LocalDateTime lastTimestamp = JizDateTimeUtils.parseUtcText("2022-02-22T00:00:10");
////
//////			long startTimeStampEpochMilli = startTimeStamp.toInstant(ZoneOffset.UTC).toEpochMilli();
////			long lastTimestampEpochMilli = lastTimestamp.toInstant(ZoneOffset.UTC).toEpochMilli();
////
////			// 将私有方法可视化
////			Method priateGetCurrTimeStamp = JizIDGenSnowflakeImpl.class.getDeclaredMethod("getCurrTimeStamp");
////			priateGetCurrTimeStamp.setAccessible(true);
////
////			Object ss = priateGetCurrTimeStamp.invoke(doReturn(lastTimestampEpochMilli).when(idGenerator));
////
////			Method getNextTimeStamp = JizIDGenSnowflakeImpl.class.getDeclaredMethod("getNextTimeStamp", Boolean.class);
////			getNextTimeStamp.setAccessible(true);
////
//////			Mockito.doReturn(lastTimestampEpochMilli).when(idGenerator).getNextTimeStamp(true);
////
////			idGenerator.setIdBits(47, 2, 2, 12);
////			idGenerator.setIdData(1, 1, startTimeStamp);
////			idGenerator.init();
////
////			JizIDGenStatus result1 = idGenerator.init();
////			JizIDGenStatus result2 = idGenerator.init();
////
////			Assertions.assertEquals(result1.getId(), result2.getId() + 1, "同步线程没有全部完成。");
////		}
////	}
//
////	@Order(1)
////	@Test
////	@DisplayName("初始化全局Id生成器")
////	void init() {
////		idGenerator = new JizIDGenSnowflakeImpl();
////
////		JizIDGenStatus stauts = idGenerator.init();
////		Assertions.assertEquals(stauts, JizIDGenStatus.success, "初始化全局Id生成器失败。");
////	}
////
////	@Order(2)
////	@Test
////	@DisplayName("多线程异步处理：同时创建多个全局Id")
////	@RepeatedTest(value = 9, name = "重复测试：{currentRepetition}/{totalRepetitions}")
////	void createGlobaIds() throws Exception {
////
////		// 同步测试
////		Long testCount = ConcurrencyUtils.testConcurrency(100, 10, (param) -> {
////
////			JizIDGenReslut result;
////			try {
////				result = idGenerator.nextId(null);
////				Assertions.assertEquals(result.getStatus(), JizIDGenStatus.success, "创建全局Id失败。");
////
////				createdIds.add(result.getCode());
////			} catch (Exception e) {
////				Assertions.fail("同步线程中，出现异常。");
////			}
////
////		}, null);
////
////		Assertions.assertEquals(0, testCount, "同步线程没有全部完成。");
////	}
////
////	@Order(3)
////	@Test
////	@DisplayName("多线程异步处理结果")
////	void checkResult() {
////		logger.info("michael 3");
////		Assertions.assertEquals(1000, createdIds.size(), "预计生成的全局Id数不正确。");
////
////		for (int index = 0; index < createdIds.size(); index++) {
////			final long currValue = createdIds.get(index);
////			final long count = createdIds.stream().filter(item -> item == currValue).count();
////
////			Assertions.assertEquals(1, count, String.format("重复的ID(%d)数是：%d个。", currValue, count));
////		}
////	}	
//}
