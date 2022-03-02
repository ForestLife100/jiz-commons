package com.jiz.jiz_commons.uid_gen.snowflake;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jiz.jiz_commons.uid_gen.JizIDGenReslut;
import com.jiz.jiz_commons.uid_gen.JizIDGenStatus;
import com.jiz.jiz_commons.utils.JizConcurrencyUtils;
import com.jiz.jiz_commons.utils.JizDateTimeUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("创建全局唯一ID")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JizIDGenSnowflakeImplTest {
	static final Logger logger = Logger.getLogger(JizIDGenSnowflakeImplTest.class.getName());

	JizIDGenSnowflakeImpl idGenerator;

	JizIDGenSnowflakeImplTest() {
	}

	@BeforeEach
	void setUp() throws Exception {
		idGenerator = new JizIDGenSnowflakeImpl();
	}

	@Test
	@DisplayName("构造函数")
	void init_startTimeStamp_inMillisecond() {
		Assertions.assertDoesNotThrow(() -> idGenerator = new JizIDGenSnowflakeImpl(), "构造函数失败");
	}

	@Nested
	@DisplayName("通过不同的设定值，初始化生成器")
	class SettingParameters {

		@DisplayName("初始化")
		@ParameterizedTest
		@MethodSource("com.jiz.jiz_commons.uid_gen.snowflake.JizIDGenSnowflakeImplTestArgsProvider#initArgsProvider")
		void with_args(long timeStampBits, long dataCenterBits, long machineBits, long sequenceBits, long dataCenterId,
				long machineId, LocalDateTime startTimeStamp) {
			idGenerator.setDataBits(timeStampBits, dataCenterBits, machineBits, sequenceBits);
			idGenerator.setDataInitInfo(dataCenterId, machineId, startTimeStamp);

			boolean maxBitsLen = (1L + timeStampBits + dataCenterBits + machineBits + sequenceBits) <= 64;

			// 检测数值的有效
			long maxDataNumb = -1L ^ (-1L << dataCenterBits); // 机器标识占用的位数
			boolean maxDataCenterIdLen = dataCenterId <= maxDataNumb && dataCenterId >= 0;

			maxDataNumb = -1L ^ (-1L << machineBits); // 数据中心占用的位数
			boolean maxMachineIdLen = machineId <= maxDataNumb && machineId >= 0;

			// 时钟修正
			boolean isLessthanCurr = System.currentTimeMillis()
					- startTimeStamp.toInstant(ZoneOffset.UTC).toEpochMilli() >= 0;

			// 正常系
			Assumptions.assumingThat(maxBitsLen && maxDataCenterIdLen && maxMachineIdLen && isLessthanCurr, () -> {
				JizIDGenStatus stauts = idGenerator.initGenerator();
				Assertions.assertEquals(stauts, JizIDGenStatus.success, "全局生成器，初始化失败。");
			});

			// 异常系：超过数据的最大bit数
			// 1: 时间戳的位数: (当前时间戳 - 固定开始时间戳）的差值
			// 2: 数据中心占用的位数
			// 3: 机器标识占用的位数
			// 4: 序列号占用的位数
			Assumptions.assumingThat(!maxBitsLen, () -> {
				JizIDGenStatus stauts = idGenerator.initGenerator();
				Assertions.assertEquals(stauts, JizIDGenStatus.err_init, "超过数据的最大bit数。");
			});

			// 异常系：超过数据中心ID允许的最大值数
			// 5: 数据中心ID
			Assumptions.assumingThat(!maxDataCenterIdLen, () -> {
				JizIDGenStatus stauts = idGenerator.initGenerator();
				Assertions.assertEquals(stauts, JizIDGenStatus.err_init, "超过数据中心ID允许的最大值数。");
			});

			// 异常系：超过机器标识ID允许的最大值数
			// 6: 机器标识ID
			Assumptions.assumingThat(!maxMachineIdLen, () -> {
				JizIDGenStatus stauts = idGenerator.initGenerator();
				Assertions.assertEquals(stauts, JizIDGenStatus.err_init, "超过机器标识ID允许的最大值数。");
			});

			// 异常系：起始的时间戳现有当前时间
			// 7: 起始的时间戳
			Assumptions.assumingThat(!isLessthanCurr, () -> {
				JizIDGenStatus stauts = idGenerator.initGenerator();
				Assertions.assertEquals(stauts, JizIDGenStatus.err_init, "起始的时间戳现有当前时间。");
			});
		}

	}

	@Nested
	@DisplayName("多线程处理")
	class MultiThreadInMillisecond {
		int numberOfThreads = 10000;
		int numberOfThreadPool = 100;

		List<Long> createdIds = Collections.synchronizedList(new ArrayList<Long>());

		@BeforeEach
		@DisplayName("初始化全局Id生成器")
		void setUp() throws Exception {
			numberOfThreads = 50;
			numberOfThreadPool = 50;

			createdIds = Collections.synchronizedList(new ArrayList<Long>());
		}

		@AfterEach
		void tearDown() throws Exception {
		}

		@Test
		@DisplayName("同时创建多个全局Id：生成的ID不能重复")
		void createGlobaIds_exclusive() throws Exception {
			// 多线程异步处理
			Long testCount = JizConcurrencyUtils.testConcurrency(numberOfThreads, numberOfThreadPool, (param) -> {

				JizIDGenReslut result = idGenerator.nextId(null);
				Assertions.assertEquals(result.getStatus(), JizIDGenStatus.success, "创建全局Id失败。");

				createdIds.add(result.getCode());

			}, null);

			// 处理过程中，是否有异常线程退出
			Assertions.assertEquals(0, testCount, "同步线程中，存在部分没有结束的情况。");

			// 处理结果：生成的ID重复
			Assertions.assertEquals(numberOfThreads, createdIds.size(), "预计生成的全局Id数不正确。");
			for (int index = 0; index < createdIds.size(); index++) {
				final long currValue = createdIds.get(index);
				final long count = createdIds.stream().filter(item -> item == currValue).count();

				Assertions.assertEquals(1, count, String.format("重复的ID(%d)数是：%d个。", currValue, count));
			}
		}

		@Test
		@DisplayName("同时创建多个全局Id：生成的ID顺序递增")
		void createGlobaIds_order() throws Exception {
			Object syncObject = new Object();

			// 多线程异步处理
			Long testCount = JizConcurrencyUtils.testConcurrency(numberOfThreads, numberOfThreadPool,
					(syncObjectParam) -> {
						synchronized (syncObjectParam) {
							JizIDGenReslut result = idGenerator.nextId(null);
							Assertions.assertEquals(result.getStatus(), JizIDGenStatus.success, "创建全局Id失败。");

							createdIds.add(result.getCode());
						}

					}, syncObject);

			// 处理过程中，是否有异常线程退出
			Assertions.assertEquals(0, testCount, "同步线程中，存在部分没有结束的情况。");

			// 处理结果：生成的ID重复
			Assertions.assertEquals(numberOfThreads, createdIds.size(), "预计生成的全局Id数不正确。");

			for (int index = 0; index < createdIds.size() - 1; index++) {
				final long privateValue = createdIds.get(index);
				final long currValue = createdIds.get(index + 1);

				MatcherAssert.assertThat(String.format("生成的全局Id不是递增序列:%d < %d。", privateValue, currValue), privateValue,
						Matchers.lessThan(currValue));
			}
		}
	}

	@Nested
	@DisplayName("模拟系-1")
	class MockTest {
		JizIDGenSnowflakeImpl spyIdGenerator;

		@BeforeEach
		void setUp() throws Exception {
			spyIdGenerator = Mockito.spy(idGenerator);
		}

		@AfterEach
		void tearDown() throws Exception {
			spyIdGenerator = null;
		}

		@Order(1)
		@Test
		@DisplayName("起始的时间戳-1")
		void startTimeStamp_invalid() {
			LocalDateTime startTimeStamp = JizDateTimeUtils.parseUtcText("2020-02-22T00:00:00");
			long startTimeStampLon = startTimeStamp.toInstant(ZoneOffset.UTC).toEpochMilli();

			Mockito.doReturn(startTimeStampLon - 2).when(spyIdGenerator).getCurrTimeStamp();

			spyIdGenerator.setDataInitInfo(1, 1, startTimeStamp);
			JizIDGenStatus result1 = spyIdGenerator.initGenerator();

			Assertions.assertEquals(result1, JizIDGenStatus.err_init, "当前时间，小于起始时间。");
		}

		@Order(2)
		@Test
		@DisplayName("起始的时间戳-2")
		void exception_getNextTimeStamp_getCurrTimeStamp() {
			LocalDateTime startTimeStamp = JizDateTimeUtils.parseUtcText("2020-02-22T00:00:00");

			spyIdGenerator.setDataInitInfo(1, 1, startTimeStamp);
			spyIdGenerator.initGenerator();

			long startTimeStampLon = startTimeStamp.toInstant(ZoneOffset.UTC).toEpochMilli();
			Mockito.doReturn(startTimeStampLon - 2).when(spyIdGenerator).getCurrTimeStamp();

			RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
				spyIdGenerator.nextId(null);
			}, "起始的时间戳：超过了当前时间");
			Assertions.assertNotNull(exception, "起始的时间戳：超过了当前时间");
		}

		@Order(3)
		@Test
		@DisplayName("起始的时间戳-3")
		void exception_getNextTimeStamp_startTimeStamp()
				throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			LocalDateTime startTimeStamp = JizDateTimeUtils.parseUtcText("2020-02-22T00:00:00");

			spyIdGenerator.setDataInitInfo(1, 1, startTimeStamp);
			spyIdGenerator.initGenerator();

			// 初始的时候，序列号为最大值
			Field fieldStartTimeStamp = JizIDGenSnowflakeImpl.class.getDeclaredField("startTimeStamp");
			fieldStartTimeStamp.setAccessible(true);

			startTimeStamp = JizDateTimeUtils.parseUtcText("2028-02-22T00:00:00");
			long startTimeStampLon = startTimeStamp.toInstant(ZoneOffset.UTC).toEpochMilli();
			fieldStartTimeStamp.set(spyIdGenerator, startTimeStampLon);

			RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
				spyIdGenerator.nextId(null);
			}, "起始的时间戳：超过了当前时间");
			Assertions.assertNotNull(exception, "起始的时间戳：超过了当前时间");
		}

		@Order(4)
		@Test
		@DisplayName("起始的时间戳-4")
		void exception_getNextTimeStamp_max_sequence()
				throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			LocalDateTime startTimeStamp = JizDateTimeUtils.parseUtcText("2020-02-22T00:00:00");

			long startTimeStampLon = startTimeStamp.toInstant(ZoneOffset.UTC).toEpochMilli();
			Mockito.doReturn(startTimeStampLon).when(spyIdGenerator).getCurrTimeStamp();

			// 更加条件应该创建的ID
			Random sequenceRandom = new Random();
			sequenceRandom.setSeed(startTimeStampLon);
			long sequence = sequenceRandom.nextInt(100);

			long destId = (startTimeStampLon - startTimeStampLon) << spyIdGenerator.getOffsetTimeStamp() // 时间戳部分
					| spyIdGenerator.getDataCenterId() << spyIdGenerator.getOffsetDataCenter() // 数据中心部分
					| spyIdGenerator.getMachineId() << spyIdGenerator.getOffsetMachine() // 机器标识部分
					| sequence; // 序列号部分

			// 实际创建的ID
			spyIdGenerator.setDataInitInfo(1, 1, startTimeStamp);
			spyIdGenerator.initGenerator();

			long sequenceMask = spyIdGenerator.getSequenceMask();

			Field fieldlLastTimestamp = JizIDGenSnowflakeImpl.class.getDeclaredField("lastTimestamp");
			fieldlLastTimestamp.setAccessible(true);
			fieldlLastTimestamp.set(spyIdGenerator, startTimeStampLon);

			// 初始的时候，序列号为最大值
			Field fieldSequence = JizIDGenSnowflakeImpl.class.getDeclaredField("sequence");
			fieldSequence.setAccessible(true);
			fieldSequence.set(spyIdGenerator, -1L & sequenceMask);

			JizIDGenReslut result = spyIdGenerator.nextId(null);

			Assertions.assertEquals(result.getStatus(), JizIDGenStatus.success, "创建全局Id失败。");
			Assertions.assertEquals(result.getCode(), destId, "创建全局Id错误。");
		}
	}

	@Nested
	@DisplayName("模拟系 - 注解mock")
	class InjectMocksTest {
		private AutoCloseable mockIdGeneratorCloseable;

		@Mock
		Random random;
		@InjectMocks
		JizIDGenSnowflakeImpl mockIdGenerator = new JizIDGenSnowflakeImpl();

		@BeforeEach
		void setUp() throws Exception {
			mockIdGeneratorCloseable = MockitoAnnotations.openMocks(this);
		}

		@AfterEach
		void tearDown() throws Exception {
			mockIdGeneratorCloseable.close();
		}

		@Test
		@DisplayName("随机数")
		void exception_getNextTimeStamp_startTimeStamp()
				throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			
			   // startTimeStampLon == 1582329600000
//			LocalDateTime startTimeStamp = JizDateTimeUtils.parseUtcText("2020-02-22T00:00:00");
//			long startTimeStampLon = startTimeStamp.toInstant(ZoneOffset.UTC).toEpochMilli();
			
			Mockito.when(random.nextInt(ArgumentMatchers.anyInt())).thenReturn(500);

//			try (MockedStatic<System> mocked = Mockito.mockStatic(System.class)) {
				mockIdGenerator.initGenerator();
				
				Assertions.assertEquals(mockIdGenerator.getSequence(), 500, "序列号生成错误。");

//			}catch (Exception e) {
//				e.printStackTrace();
//			}


			Mockito.verify(random).nextInt(ArgumentMatchers.anyInt());

		}
	}

}
