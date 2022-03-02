package com.jiz.jiz_commons.uid_gen.snowflake;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Random;

import com.jiz.jiz_commons.uid_gen.JizIDGen;
import com.jiz.jiz_commons.uid_gen.JizIDGenReslut;
import com.jiz.jiz_commons.uid_gen.JizIDGenStatus;

public class JizIDGenSnowflakeImpl implements JizIDGen {
	// 1位标识：0
	private long timeStampBits = 47L; // 时间戳的位数: (当前时间戳 - 固定开始时间戳）的差值
	private long dataCenterBits = 2L; // 数据中心占用的位数
	private long machineBits = 2L; // 机器标识占用的位数
	private long sequenceBits = 12L; // 序列号占用的位数

	private long dataCenterId = 1L; // 数据中心ID
	private long machineId = 1L; // 机器标识ID
	private long startTimeStamp = 1489111610226L; // 起始的时间戳:2015-01-01（UTC）

	// 算法中用的数据，由上面的设定信息生成
	private long offsetTimeStamp; // bit偏移量:时间戳
	private long offsetDataCenter; // bit偏移量:数据中心
	private long offsetMachine; // bit偏移量:机器标识
	private long sequenceMask; // 序列号的mask

	// 运行期间数据
	private Random sequenceRandom = new Random();

	private long sequence = 0L;
	private long lastTimestamp = -1L;
	
	public JizIDGenSnowflakeImpl() {
		initDataEnv();
	}
	
	public long getTimeStampBits() {
		return timeStampBits;
	}

	public long getDataCenterBits() {
		return dataCenterBits;
	}

	public long getMachineBits() {
		return machineBits;
	}

	public long getSequenceBits() {
		return sequenceBits;
	}

	public long getDataCenterId() {
		return dataCenterId;
	}

	public long getMachineId() {
		return machineId;
	}

	public long getStartTimeStamp() {
		return startTimeStamp;
	}

	public long getOffsetTimeStamp() {
		return offsetTimeStamp;
	}

	public long getOffsetDataCenter() {
		return offsetDataCenter;
	}

	public long getOffsetMachine() {
		return offsetMachine;
	}

	public long getSequenceMask() {
		return sequenceMask;
	}

	public long getSequence() {
		return sequence;
	}

	public long getLastTimestamp() {
		return lastTimestamp;
	}

	
	/**
	 * 初始化ID的数据结构
	 * 
	 * @param timeStampBits  时间戳部分占用的位数
	 * @param dataCenterBits 数据中心占用的位数
	 * @param machineBits    机器标识占用的位数
	 * @param sequenceBits   序列号占用的位数
	 */
	public void setDataBits(long timeStampBits, long dataCenterBits, long machineBits, long sequenceBits) {
		this.dataCenterBits = dataCenterBits;
		this.machineBits = machineBits;
		this.timeStampBits = timeStampBits;
		this.sequenceBits = sequenceBits;
		
		initDataEnv();;
	}

	/**
	 * 初始化ID的数据
	 * 
	 * @param dataCenterId   数据中心ID
	 * @param machineId      机器标识ID
	 * @param startTimeStamp 起始的时间戳（UTC）
	 */
	public void setDataInitInfo(long dataCenterId, long machineId, LocalDateTime startTimeStamp) {
		this.startTimeStamp = startTimeStamp.toInstant(ZoneOffset.UTC).toEpochMilli();

		setDataInitInfo(dataCenterId, machineId);
	}

	/**
	 * 初始化ID的数据
	 * 
	 * @param dataCenterId 数据中心ID
	 * @param machineId    机器标识ID
	 */
	public void setDataInitInfo(long dataCenterId, long machineId) {
		this.dataCenterId = dataCenterId;
		this.machineId = machineId;
	}

	/**
	 * 检测基本基本设置
	 */
	public JizIDGenStatus initGenerator() {
		JizIDGenStatus result = JizIDGenStatus.err_init;
		result = checkMaxBits();
		if (result != JizIDGenStatus.success) {
			return result;
		}

		// 检测数值的有效
		long maxDataCenterNum = -1L ^ (-1L << this.dataCenterBits); // 机器标识占用的位数
		if (dataCenterId > maxDataCenterNum || dataCenterId < 0) {
			return JizIDGenStatus.err_init;
		}

		long maxMachineNum = -1L ^ (-1L << this.machineBits); // 数据中心占用的位数
		if (machineId > maxMachineNum || machineId < 0) {
			return JizIDGenStatus.err_init;
		}

		// 时钟修正
		this.lastTimestamp = getCurrTimeStamp();
		if (lastTimestamp < startTimeStamp) {
			return JizIDGenStatus.err_init;
		}

		this.sequenceRandom.setSeed(lastTimestamp);
		this.sequence = sequenceRandom.nextInt(100);

//		System.out.println(String.format(
//				"timeStampBits:%s, dataCenterBits:%d, machineBits:%d, sequenceBits:%d, offsetTimeStamp:%d, offsetDataCenter:%d, offsetMachine:%d, sequenceMask:%d",
//				this.timeStampBits, this.dataCenterBits, this.machineBits, this.sequenceBits, this.offsetTimeStamp,
//				this.offsetDataCenter, this.offsetMachine, sequenceMask));

		return JizIDGenStatus.success;
	}

	/**
	 * 
	 * @return
	 */
	protected JizIDGenStatus checkMaxBits() {
		if (1L + this.timeStampBits + this.dataCenterBits + this.machineBits + this.sequenceBits > 64) {
			return JizIDGenStatus.err_init;
		}

		return JizIDGenStatus.success;
	}
	
	protected void initDataEnv() {
		// 设置数据bit offset
		this.offsetMachine = this.sequenceBits;
		this.offsetDataCenter = this.offsetMachine + this.machineBits;
		this.offsetTimeStamp = this.offsetDataCenter + this.dataCenterBits;

		this.sequenceMask = -1L ^ (-1L << this.sequenceBits);
	}

	/**
	 * 获取当前的时间戳
	 * 
	 */
	protected long getCurrTimeStamp() {
		// 时间戳部分使用毫秒
		return System.currentTimeMillis();
	}

	/**
	 * 
	 * @param lastTimestamp
	 * @return
	 * @throws RuntimeException
	 */
	protected long getNextTimeStamp(Boolean doWait) throws RuntimeException {
		long currTimeStamp = getCurrTimeStamp();

		// 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
		if (currTimeStamp < this.lastTimestamp || currTimeStamp < this.startTimeStamp) {
			throw new RuntimeException(
					String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
							lastTimestamp - currTimeStamp));
		}
		// 获取下一个时钟
		else if (doWait && currTimeStamp == lastTimestamp) {
			try {
					wait(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage());
			}

			currTimeStamp = getCurrTimeStamp();
		}

		return currTimeStamp;
	}

	/**
	 * 获取起始的时间戳（UTC）
	 * 
	 * @return
	 */
	public LocalDateTime getStartTimeStampUTC() {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.startTimeStamp), ZoneId.of("Z"));
	}

	/**
	 * 生成全局唯一Id
	 */
	public synchronized JizIDGenReslut nextId(String key) throws RuntimeException {
		long currTimeStamp = getNextTimeStamp(false);

		// 相同时钟区间内（单位：毫秒）内，序列号进行自增
		if (lastTimestamp == currTimeStamp) {
//			System.out.println(String.format("ThreadID:%s=>sequence:%d", Thread.currentThread().getId(), sequence));

			sequence = (sequence + 1) & sequenceMask;

			// 相同时钟区间内,序列号已经用完
			if (sequence == 0) {
				lastTimestamp = getNextTimeStamp(true);

				// 下一毫秒时间开始对序列号做随机
				sequenceRandom.setSeed(lastTimestamp);
				sequence = sequenceRandom.nextInt(100);
			}
		}
		// 相同时钟区间外（单位：毫秒）
		else {
			lastTimestamp = currTimeStamp;

			// 对序列号做随机
			sequenceRandom.setSeed(lastTimestamp);
			sequence = sequenceRandom.nextInt(100);
		}

		long timeStampDiff = lastTimestamp - startTimeStamp;

//		System.out.println(
//				String.format("ThreadID:%s=>lastTimestamp:%d,startTimeStamp:%d, timeStampDiff:%d, sequence:%d,",
//						Thread.currentThread().getId(), lastTimestamp, startTimeStamp, timeStampDiff, sequence));

		long id = timeStampDiff << offsetTimeStamp // 时间戳部分
				| dataCenterId << offsetDataCenter // 数据中心部分
				| machineId << offsetMachine // 机器标识部分
				| sequence; // 序列号部分

//		 System.out.println(String.format("ThreadID:%s=>ID:%d", Thread.currentThread().getId(), id));

		return new JizIDGenReslut(id, JizIDGenStatus.success);
	}
}
