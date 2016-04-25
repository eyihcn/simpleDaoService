package service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import entity.BaseEntity;
import eyihcn.utils.Json;

public abstract class BaseService<T extends BaseEntity, PK extends Serializable> {

	private static final String SAVE = "save";
	private static final String UPDATE = "update";
	private static final String FIND_ONE = "findOne";
	private static final String DELETE_BY_ID = "deleteById";
	private static final String FIND_LIST = "findList";
	protected final String COLLECTION = "collection";
	protected final String COLLECTION_COUNT = "collectionCount";
	private String request = "";

	public void _execute() throws Exception {
	}


	@RequestMapping(value = SAVE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse save(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (!daoSave(request)) {
				serviceResponse.changeStatus(ResponseStatus.ERROR, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}

	@RequestMapping(value = UPDATE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse update(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (!daoUpdate(request)) {
				serviceResponse.changeStatus(ResponseStatus.ERROR, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}


	@RequestMapping(value = FIND_ONE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse findOne(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			T entity = daoFindOne(request);
			serviceResponse.setResult(entity);
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}


	@RequestMapping(value = FIND_LIST, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse findCollection(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			Map<String, Object> collectionInfo = new HashMap<String, Object>();
			collectionInfo.put(COLLECTION, daoFindCollection(request));
			collectionInfo.put(COLLECTION_COUNT, daoFindCollectionCount(request));
			serviceResponse.setResult(collectionInfo);
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}


	@RequestMapping(value = DELETE_BY_ID, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse delete(@RequestBody PK id) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (!daoDeleteById(id)) {
				serviceResponse.changeStatus(ResponseStatus.ERROR, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}

	public abstract boolean daoDeleteById(@RequestBody PK id);

	@SuppressWarnings("unchecked")
	public Map<String, Object> getRequest() {
		return Json.fromJson(request, LinkedHashMap.class);
	}

	public void setRequest(String request) {
		this.request = request;
	}

	/*
	 * 以下的抽象查询方法由具体的提供dao实现
	 */
	public abstract boolean daoSave(Map<String, Object> request);

	public abstract boolean daoUpdate(Map<String, Object> request);

	public abstract T daoFindOne(Map<String, Object> request);

	public abstract List<T> daoFindCollection(Map<String, Object> request);

	public abstract Long daoFindCollectionCount(Map<String, Object> request);

}
