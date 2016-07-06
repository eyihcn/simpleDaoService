package utils;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolSingleton {

	private ExecutorService executor;

	private ThreadPoolSingleton(int corePoolSize,int maximumPoolSize, int queueCapacity) {
		this.executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(queueCapacity));
	}
	
	private ThreadPoolSingleton (){}
	
	private static class SingletonHolder {
		private SingletonHolder (){}
		private static final ThreadPoolSingleton singleton = new ThreadPoolSingleton(Runtime.getRuntime().availableProcessors() * 2,25,100); 
	}
	
	public static ThreadPoolSingleton getInstance() {
		return SingletonHolder.singleton;
	}
	
	public void execute(Runnable task) {
		executor.execute(task);
	}

	public void execute(Collection<? extends Runnable> tasks) {
		if (null != tasks) {
			for (Runnable task : tasks) {
				execute(task);
			}
		}
	}
	
	public void shutDown() {
		executor.shutdown();
	}

}
