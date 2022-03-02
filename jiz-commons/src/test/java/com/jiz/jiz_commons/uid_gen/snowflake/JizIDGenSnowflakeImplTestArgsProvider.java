package com.jiz.jiz_commons.uid_gen.snowflake;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import com.jiz.jiz_commons.utils.JizDateTimeUtils;

class JizIDGenSnowflakeImplTestArgsProvider {

	static Stream<Arguments> initArgsProvider() {
		// 1: 时间戳部分: (当前时间戳 - 固定开始时间戳）的差值
		// 2: 数据中心占用的位数
		// 3: 机器标识占用的位数
		// 4: 序列号占用的位数

		// 5: 数据中心ID
		// 6: 机器标识ID
		// 7: 起始的时间戳

		return Stream.of(
				// 正常系
				Arguments.arguments(46, 1, 2, 12, 1, 1, JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00")),

				// 异常系：超过数据的最大bit数
				// 1: 时间戳部分: (当前时间戳 - 固定开始时间戳）的差值
				// 2: 数据中心占用的位数
				// 3: 机器标识占用的位数
				// 4: 序列号占用的位数
				Arguments.arguments(47, 2, 2, 13, 4, 1, JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00")),

				// 异常系：超过数据中心ID允许的最大值数
				// 5: 数据中心ID
				Arguments.arguments(40, 2, 3, 12, 16, 1, JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00")),
				Arguments.arguments(40, 2, 3, 12, -1, 1, JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00")),

				// 异常系：超过机器标识ID允许的最大值数
				// 6: 机器标识ID
				Arguments.arguments(42, 2, 4, 12, 1, 65, JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00")),
				Arguments.arguments(42, 2, 4, 12, 1, -1, JizDateTimeUtils.parseUtcText("2022-02-22T00:00:00")),

				// 异常系：起始的时间戳现有当前时间
				// 7: 起始的时间戳
				Arguments.arguments(47, 2, 2, 12, 1, 1, JizDateTimeUtils.parseUtcText("2020-02-22T00:00:00")));
	}

}
