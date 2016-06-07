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

import dao.CommonDaoInter;
import entity.BaseEntity;
@SuppressWarnings("unchecked")
public abstract class BaseService<T extends BaseEntity<PK>, PK extends Serializable> {

	protected static final String SAVE = "save";
	protected static final String UPDATE = "update";
	protected static final String SAVE_OR_UPDATE = "saveOrUpdate";
	protected static final String FIND_ONE = "findOne";
	protected static final String FIND_BY_ID = "findById";
	protected static final String DELETE_BY_ID = "deleteById";
	protected static final String DELETE = "delete";
	protected static final String FIND_LIST = "findList";
	protected static final String FIND_COLLECTION = "findCollection";
	protected static final String COUNTS = "counts";
	protected static final String CHECK_EXISTS = "checkExists";
	protected final String COLLECTION = "collection";
	protected final String COLLECTION_COUNT = "collectionCount";

	protected CommonDaoInter<T, PK> commonDaoInter;

	public void setCommonDaoInter(CommonDaoInter<T, PK> commonDaoInter) {
		this.commonDaoInter = commonDaoInter;
	}

	@RequestMapping(value = FIND_COLLECTION, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse findCollection(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			Map<String, Object> collectionInfo = new HashMap<String, Object>();
			List<T> entities = commonDaoInter.findCollection(request);
			if (null == entities) {
				entities = new ArrayList<T>();
			}
			collectionInfo.put(COLLECTION, entities);
			collectionInfo.put(COLLECTION_COUNT, commonDaoInter.findCollectionCount(request));
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
			if (!commonDaoInter.save(request)) {
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
			if (!commonDaoInter.update(request)) {
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
	
	@RequestMapping(value = SAVE_OR_UPDATE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse saveOrUpdate(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (!commonDaoInter.saveOrUpdate(request)) {
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
			T entity = commonDaoInter.findOne(request);
			serviceResponse.setResult(entity);
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}

	@RequestMapping(value = FIND_BY_ID, method = RequestMethod.POST)
	@ResponseBody
	//	public ServiceResponse findById(@RequestBody PK id) { 
	// Can not construct instance of java.io.Serializable, problem: abstract types either need to be mapped to concrete types, have custom deserializer, or be instantiated with additional type information
	// 具体化类型,否则jackson 无法实例化主键，mvc绑定参数失败o(︶︿︶)o 唉！
	public ServiceResponse findById(@RequestBody Long id) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			T entity = commonDaoInter.findById((PK) id);
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
			serviceResponse.setResult(commonDaoInter.findCollection(request));
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
			if (!commonDaoInter.deleteById((PK) id)) {
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
			if (!commonDaoInter.delete(request)) {
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
	public ServiceResponse counts(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult(commonDaoInter.findCollectionCount(request));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}
	
	@RequestMapping(value=CHECK_EXISTS, method=RequestMethod.POST)
    @ResponseBody
	public ServiceResponse checkExists(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult(commonDaoInter.checkExists(request));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}

}
