package client;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import entity.BaseEntity;

public class BatchUpdateTask<T extends BaseEntity<PK>, PK extends Serializable>
		implements Runnable {

	private DaoServiceClient<T, PK> serviceClient;
	private List<Map<String, Object>> perUpdate;
	private AtomicInteger totalTaskCounts;
	private Map<String, Boolean> result;

	public BatchUpdateTask() {
		super();
	}

	public BatchUpdateTask(DaoServiceClient<T, PK> serviceClient,
			List<Map<String, Object>> perUpdate, AtomicInteger totalTaskCounts,
			Map<String, Boolean> result) {
		super();
		this.serviceClient = serviceClient;
		this.perUpdate = perUpdate;
		this.totalTaskCounts = totalTaskCounts;
		this.result = result;
	}

	public void run() {
		try {
			result.putAll(serviceClient.batchUpdate(perUpdate));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			totalTaskCounts.decrementAndGet();
		}
		 
	}

}
