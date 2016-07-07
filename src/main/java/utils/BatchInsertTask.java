package utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import client.DaoServiceClient;
import entity.BaseEntity;

public class BatchInsertTask<T extends BaseEntity<PK>, PK extends Serializable>
		implements Runnable {

	private DaoServiceClient<T, PK> serviceClient;
	private List<T> perInsert;
	private AtomicInteger totalTaskCounts;
	private Map<Integer, Boolean> result;

	public BatchInsertTask() {
		super();
	}

	public BatchInsertTask(DaoServiceClient<T, PK> serviceClient,
			List<T> perInsert, AtomicInteger totalTaskCounts,
			Map<Integer, Boolean> result) {
		super();
		this.serviceClient = serviceClient;
		this.perInsert = perInsert;
		this.totalTaskCounts = totalTaskCounts;
		this.result = result;
	}

	public void run() {
		try {
			result.putAll(serviceClient.batchInsert(perInsert));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			totalTaskCounts.decrementAndGet();
		}
		 
	}

}
