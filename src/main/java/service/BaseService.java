package service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import utils.MyBeanUtil;
import dao.BaseMongoDao;
import entity.BaseEntity;
@SuppressWarnings("unchecked")
public abstract class BaseService<T extends BaseEntity<PK>, PK extends Serializable> {

	final Logger log = LoggerFactory.getLogger(BaseService.class);
	
	protected static final String SAVE = "save";
	protected static final String UPDATE = "update";
	protected static final String SAVE_BY_UPSERT = "saveByUpsert";
	protected static final String FIND_ONE = "findOne";
	protected static final String FIND_BY_ID = "findById";
	protected static final String DELETE_BY_ID = "deleteById";
	protected static final String DELETE = "delete";
	protected static final String FIND_LIST = "findList";
	protected static final String FIND_COLLECTION = "findCollection";
	protected static final String COUNTS = "counts";
	protected static final String BATCH_UPDATE_BY_IDS = "batchUpdateByIds";
	protected static final String BATCH_UPDATE = "batchUpdate";
	protected static final String BATCH_SAVE_BY_UPSERT= "batchSaveByUpsert";
	protected static final String BATCH_INSERT = "batchInsert";
	protected static final String CHECK_EXISTS = "checkExists";
	protected static final String FIND_IDS = "findIds";
	protected static final String GENERATE_PRIMARY_KEY_BY_OFFSET = "generatePrimaryKeyByOffset";
	
	protected static final String COLLECTION = "collection";
	protected static final String COLLECTION_COUNT = "collectionCount";
	protected static final String UPDATES = "updates";
	protected static final String IDS = "ids";

	private Class<T> entityClass; // 实体的运行是类
	private Class<PK> pkClass; // 实体的运行是类
	private String defaultDaoBeanName ; // spring容器中的dao-bean的默认名称
	
	@Autowired
	private ApplicationContext applicationContext ;

	public BaseService() {
		this.entityClass = MyBeanUtil.getSuperClassGenericType(this.getClass());
		this.pkClass = MyBeanUtil.getSuperClassGenericType(this.getClass(), 1);
		// 默认的dao命名约束 entity-name的首字母小写+"Dao"
		this.defaultDaoBeanName =  StringUtils.uncapitalize(entityClass.getSimpleName()+"Dao");
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
//	public <E> 

	@SuppressWarnings("rawtypes")
	public BaseMongoDao<T,PK>  getDao(Class<T> entityClass , Class<PK> pkClass) {
		log.info("========================dao bean name :::"+defaultDaoBeanName);
		BaseMongoDao<T,PK> daoBean = (BaseMongoDao<T, PK>) applicationContext.getBean(defaultDaoBeanName);
		if (null == daoBean) {
			daoBean =  new BaseMongoDao(entityClass,pkClass);
		}
		return daoBean;
	}
	

	@RequestMapping(value = FIND_COLLECTION, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse findCollection(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			Map<String, Object> collectionInfo = new HashMap<String, Object>();
			List<T> entities = getDao(entityClass, pkClass).findCollection(request);
			if (null == entities) {
				entities = new ArrayList<T>();
			}
			collectionInfo.put(COLLECTION, entities);
			collectionInfo.put(COLLECTION_COUNT, getDao(entityClass, pkClass).findCollectionCount(request));
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
			if (!getDao(entityClass, pkClass).save(request)) {
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
			if (!getDao(entityClass, pkClass).update(request)) {
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
	
	@RequestMapping(value = SAVE_BY_UPSERT, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse saveOrUpdate(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (!getDao(entityClass, pkClass).saveByUpsert(request)) {
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
	
	@RequestMapping(value = BATCH_UPDATE_BY_IDS, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse batchUpdateByIds(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			List<PK> ids = (List<PK>) request.remove(IDS);
			if (!getDao(entityClass, pkClass).batchUpdateByIds(ids,request)) {
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

	@RequestMapping(value = BATCH_UPDATE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse batchUpdate(@RequestBody List<Map<String, Object>> allUpdates) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult( getDao(entityClass, pkClass).batchUpdate(allUpdates));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}
	
	@RequestMapping(value = BATCH_SAVE_BY_UPSERT, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse batchSaveOrUpdate(@RequestBody List<Map<String, Object>> allSaveOrUpdates) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult( getDao(entityClass, pkClass).batchSaveByUpsert(allSaveOrUpdates));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}
	
	@RequestMapping(value = BATCH_INSERT, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse batchInsert(@RequestBody List<Map<String, Object>> batchToSave) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult(getDao(entityClass, pkClass).batchInsert(batchToSave));
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
			T entity = getDao(entityClass, pkClass).findOne(request);
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
			T entity = getDao(entityClass, pkClass).findById((PK) id);
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
			serviceResponse.setResult(getDao(entityClass, pkClass).findCollection(request));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}


	@RequestMapping(value = DELETE_BY_ID, method = RequestMethod.POST)
	@ResponseBody
//	public ServiceResponse delete(@RequestBody PK id) {
	public ServiceResponse deleteById(@RequestBody Long id) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (!getDao(entityClass, pkClass).deleteById((PK) id)) {
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
			if (!getDao(entityClass, pkClass).delete(request)) {
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
	public ServiceResponse counts(@RequestBody Map<String, Object> query) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult(getDao(entityClass, pkClass).count(query));
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
			serviceResponse.setResult(getDao(entityClass, pkClass).checkExists(request));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}
	
	@RequestMapping(value=FIND_IDS, method=RequestMethod.POST)
	@ResponseBody
	public ServiceResponse findIds(@RequestBody Map<String, Object> request) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult(getDao(entityClass, pkClass).findIds(request));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}
	
	@RequestMapping(value=GENERATE_PRIMARY_KEY_BY_OFFSET, method=RequestMethod.POST)
    @ResponseBody
	public ServiceResponse generatePrimaryKeyByOffset(@RequestBody Integer offset) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			serviceResponse.setResult(getDao(entityClass, pkClass).generatePrimaryKeyByOffset(offset));
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.changeStatus(ResponseStatus.SERVER_ERROR, null);
		}
		return serviceResponse;
	}
}
