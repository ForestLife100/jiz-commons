package com.jiz.jiz_commons.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class JizConcurrencyUtils {
	/**
	 * 测试用
	 * 
	 * @param <T>
	 * @param numberOfThreads
	 * @param numberOfThreadPool
	 * @param consumer
	 * @param param
	 * @return
	 * @throws InterruptedException
	 */
	public static <T> Long testConcurrency(int numberOfThreads, int numberOfThreadPool, Consumer<T> consumer, T param)
			throws InterruptedException {
		ExecutorService service = Executors.newFixedThreadPool(numberOfThreadPool);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		for (int i = 0; i < numberOfThreads; i++) {
			service.submit(() -> {
				consumer.accept(param);

				latch.countDown();
			});
		}
		
		latch.await();

		return latch.getCount();
	}
}
