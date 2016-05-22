package service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import entity.BaseEntity;

public abstract class BaseService<T extends BaseEntity<PK>, PK extends Serializable> {

	private static final String SAVE = "save";
	private static final String UPDATE = "update";
	private static final String FIND_ONE = "findOne";
	private static final String FIND_BY_ID = "findById";
	private static final String DELETE_BY_ID = "deleteById";
	private static final String DELETE = "delete";
	private static final String FIND_LIST = "findList";
	private static final String FIND_COLLECTION = "findCollection";
	private static final String COUNTS = "counts";
	protected final String COLLECTION = "collection";
	protected final String COLLECTION_COUNT = "collectionCount";

	@RequestMapping(value = FIND_COLLECTION, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse findCollection(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			Map<String, Object> collectionInfo = new HashMap<String, Object>();
			List<T> entities = daoFindCollection(request);
			if (null == entities) {
				entities = new ArrayList<T>();
			}
			collectionInfo.put(COLLECTION, entities);
			collectionInfo.put(COLLECTION_COUNT, daoFindCollectionCount(request));
			serviceResponse.setResult(collectionInfo);
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}

	@RequestMapping(value = SAVE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse save(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (!daoSave(request)) {
				serviceResponse.changeStatus(ResponseStatus.ERROR, false);
			} else {
				serviceResponse.setResult(true);
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
				serviceResponse.changeStatus(ResponseStatus.ERROR, false);
			} else {
				serviceResponse.setResult(true);
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

	@RequestMapping(value = FIND_BY_ID, method = RequestMethod.POST)
	@ResponseBody
//	public ServiceResponse findById(@RequestBody PK id) { //Can not construct instance of java.io.Serializable, problem: abstract types either need to be mapped to concrete types, have custom deserializer, or be instantiated with additional type information
	public ServiceResponse findById(@RequestBody Long id) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			T entity = daoFindById(id);
			serviceResponse.setResult(entity);
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}
	

	@RequestMapping(value = FIND_LIST, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse findList(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult(daoFindCollection(request));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}


	@RequestMapping(value = DELETE_BY_ID, method = RequestMethod.POST)
	@ResponseBody
//	public ServiceResponse delete(@RequestBody PK id) {
	public ServiceResponse delete(@RequestBody Long id) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (!daoDeleteById(id)) {
				serviceResponse.changeStatus(ResponseStatus.ERROR, false);
			} else {
				serviceResponse.setResult(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}
	
	@RequestMapping(value = DELETE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse delete(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (!daoDelete(request)) {
				serviceResponse.changeStatus(ResponseStatus.ERROR, false);
			} else {
				serviceResponse.setResult(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}

	@RequestMapping(value=COUNTS, method=RequestMethod.POST)
    @ResponseBody
	public ServiceResponse countsWishToBePublished(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult(counts(request));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}
	
	/*
	 * 抽象方法由具体的提供dao实现
	 */
	public abstract boolean daoSave(Map<String, Object> request);

	public abstract boolean daoUpdate(Map<String, Object> request);

	public abstract T daoFindOne(Map<String, Object> request);
	
//	public abstract T daoFindById(PK id);
	public abstract T daoFindById(Long id); //具体化类型,否则jackson 无法实例化主键，mvc绑定参数失败o(︶︿︶)o 唉！

	public abstract List<T> daoFindCollection(Map<String, Object> request);

	public abstract Long daoFindCollectionCount(Map<String, Object> request);

//	public abstract boolean daoDeleteById(PK id);
	public abstract boolean daoDeleteById(Long id);
	
	public abstract boolean daoDelete(Map<String, Object> request);
	
	public abstract Long counts(Map<String, Object> request);
}
