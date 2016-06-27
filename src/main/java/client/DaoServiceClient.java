package client;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import service.ResponseStatus;
import service.ServiceResponse;
import utils.Json;
import utils.MyBeanUtil;
import entity.BaseEntity;
import entity.ServerPortSetting;

/**
 * daoService请求的client 抽象基本的crud ,数据库项目按系统模块部署到不同的服务器 1. 可以使用自己定义的请求Entry 2.
 * 可以使用系统约定的crud的Entry
 * 
 * 对于DaoService的分布在不同的服务器 
 * 模块名-------》主机IP
 * 
 * @author eyihcn
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class DaoServiceClient<T extends BaseEntity<PK>, PK extends Serializable> extends BaseServiceClient {

	public static final String FIND_COLLECTION = "findCollection";
	public static final String FIND_LIST = "findList";
	public static final String FIND_ONE = "findOne";
	public static final String FIND_BY_ID = "findById";
	public static final String SAVE = "save";
	public static final String UPDATE = "update";
	public static final String SAVE_OR_UPDATE = "saveOrUpdate";
	public static final String DELETE = "delete";
	public static final String DELETE_BY_ID = "deleteById";
	public static final String COUNTS = "counts";
	public static final String CHECK_EXISTS = "checkExists";
	public static final String BATCH_UPDATE_BY_IDS = "batchUpdateByIds";
	public static final String BATCH_UPDATE = "batchUpdate";
	public static final String BATCH_INSERT = "batchInsert";
	public static final String COLLECTION = "collection";
	public static final String COLLECTION_COUNT = "collectionCount";
	public static final String IDS = "ids";
	public static final String CODE = "code";
	public static final String RESULT = "result";
	public static final int MAX_BATCH_INSERT_SIZE=10000;
	public static final int MAX_BATCH_UPDATE_SIZE=10000;
	private String modelName;
	private Class<T> entityClass;
	private String entityClassName; // simpleName
	private String[] ormPackageNames;

	public DaoServiceClient() {
//		initServiceAddressAndToken(this.getClass().getAnnotation(ServiceCode.class).value());
		initRquestHostAndToken_2(this.getClass().getAnnotation(ServiceCode.class).value());
		ModelName modelNameClass = this.getClass().getAnnotation(ModelName.class);
		if (modelNameClass != null) {
			this.entityClass = MyBeanUtil.getSuperClassGenericType(this.getClass());
			this.entityClassName = this.entityClass.getSimpleName();
			this.modelName =  modelNameClass.value();
		}
		this.ormPackageNames = prepareOrmPackageNames();
	}
	
	/**
	 * 初始化请求的Host和Token
	 */
	protected void initRquestHostAndToken_2(String serviceTokenCode) {
		String serviceAddressKey = "JTOMTOPERP_" + serviceTokenCode + "_SERVICE_ADDRESS";
		String serviceTokenKey = "JTOMTOPERP_" + serviceTokenCode + "_SERVICE_TOKEN";
		// 1. 先从缓存取host 和 token
		Map<String, String> serviceConfig = serviceRouterConfigs.get(serviceTokenCode);
		if (null != serviceConfig) {
			host = ((String) serviceConfig.get("ADDRESS"));
			token = ((String) serviceConfig.get("TOKEN"));
			return;
		}
		// 2. 若缓存中没有，从系统变量中获取
		host = readValue("dao_service_router.properties", serviceAddressKey);
		if(host != null) {
			token = readValue("dao_service_router.properties", serviceTokenKey);
			return ;
		}
		// 3. 若系统变量中没有，查询数据库配置
		ServerPortSetting sp =	new ServerSettingService().fetchServerPortSettingByCode(serviceTokenCode);
		if (null == sp) {
			throw new RuntimeException("no server config! serviceTokenCode=[" +serviceTokenCode+"]");
		}
		host = sp.getAddress();
		token = sp.getToken();
		// 缓存
		serviceConfig = new HashMap<String, String>();
		serviceConfig.put("ADDRESS", host);
		serviceConfig.put("TOKEN", token);
		serviceRouterConfigs.put(serviceTokenCode, serviceConfig);
	}
	
	 //根据key读取value
	 private  String readValue(String filePath,String key) {
	  Properties props = new Properties();
	        try {
	         InputStream in = new BufferedInputStream (this.getClass().getClassLoader().getResourceAsStream(filePath));
	         props.load(in);
	         String value = props.getProperty (key);
	         log.info(new StringBuilder("read properties : ").append(key).append(" = ").append(value).toString());
            return value;
	        } catch (Exception e) {
	         e.printStackTrace();
	         return null;
	        }
	 }
	
	public Object requestForResult(String requestMethodName,Object requestParam) {
		try {
			ServiceResponse serviceResponse = request(ServiceResponse.class, _buildDaoServiceRequestURL(requestMethodName), requestParam,headers);
			if (serviceResponse == null) {
				return null;
			}
			return serviceResponse.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 返回请求响应的result(内部DAO)
	 * @param requestParam
	 * @return
	 */
	public Map<String,Object> getMapResponse(String requestMethodName,Object requestParam) {
		return super.getMapResponse(_buildDaoServiceEntry(requestMethodName),requestParam);
	}
	
	/**
	 * serviceAdderss[ip:port] + serviceEntry[/模块名/实体名/方法] + ? +serviceToken=[token]
	 * @return
	 */
	private  String _buildDaoServiceRequestURL(String requestMethodName) {
		return super.buildRequestURL(host,_buildDaoServiceEntry(requestMethodName), token);
	}
	
	private String _buildDaoServiceEntry(String requestMethodName) {
		return new StringBuilder(SEPARATOR).append(modelName)
					.append(SEPARATOR).append(entityClassName)
					.append(SEPARATOR).append(requestMethodName).toString();
	}
	
	public Map<String, Object> findCollection() {
		return findCollection(null);
	}

	public Map<String, Object> findCollection(Map<String, Object> query) {
		// 1. 将query转换为请求json参数
		String requestJson = getCollectionRequestJson(query, false);
		// 2. 请求daoService，拿到返回的result
		Map<String, Object> result = (Map<String, Object>) requestForResult(FIND_COLLECTION,requestJson);
		if (MapUtils.isEmpty(result)) {
			return Collections.EMPTY_MAP;
		}
		// 3. 将result(Map) 转换为实体
		result.put(COLLECTION, mapToEntity(entityClass,(Collection<Map<String, Object>>)result.get(COLLECTION), ormPackageNames));
		return result;
	}

	public List<T> findList(Map<String, Object> query, Map<String, Object> sort, Map<String, Object> pagination) {
		return mapToEntity(entityClass, (Collection<Map<String, Object>>)requestForResult(FIND_LIST,parseToRequestJson(query, sort, pagination)), ormPackageNames);
	}

	public T findOne(Map<String, Object> query) {
		return findOne(query,null);
	}
	
	public T findOne(Map<String, Object> query, Map<String, Object> sort) {
		return mapToEntity(entityClass, (Map<String, Object>) requestForResult(FIND_ONE,parseToRequestJson(query, sort, null)), ormPackageNames);
	}

	public T findById(PK id) {
		if (null == id) {
			return null;
		}
		return mapToEntity(entityClass, (Map<String, Object>) requestForResult(FIND_BY_ID,id.toString()), ormPackageNames);
	}
	
	public boolean checkExists(Map<String, Object> query) {
		return checkExists(query,null);
	}

	public boolean checkExists(Map<String, Object> query, Map<String, Object> sort) {
		return Boolean.valueOf(requestForResult(CHECK_EXISTS,parseToRequestJson(query, sort, null)).toString()).booleanValue();
	}
	
	public boolean create(T entity) {
		if (entity == null) {
			return false;
		}
		return create(Json.toJson(entity));
	}
	
	public boolean create(Map<String,Object> properties) {
		if (MapUtils.isEmpty(properties)) {
			return false;
		}
		return create(Json.toJson(properties));
	}
	
	public boolean create(String entityJson) {
		if (StringUtils.isBlank(entityJson)) {
			return false;
		}
		return checkSuccess(getMapResponse(SAVE,entityJson));
	}

	public boolean update(T entity) {
		if (entity == null) {
			return false;
		}
		return update(Json.toJson(entity));
	}
	
	public boolean update(Map<String,Object> properties) {
		if (MapUtils.isEmpty(properties)) {
			return false;
		}
		return update(Json.toJson(properties));
	}
	
	public boolean update(String jsonParam) {
		if (StringUtils.isBlank(jsonParam)) {
			return false;
		}
		return checkSuccess(getMapResponse(UPDATE,jsonParam));
	}
	
	public boolean saveOrUpdate(T entity) {
		if (entity == null) {
			return false;
		}
		return saveOrUpdate(Json.toJson(entity));
	}
	
	public boolean saveOrUpdate(Map<String,Object> properties) {
		if (MapUtils.isEmpty(properties)) {
			return false;
		}
		return saveOrUpdate(Json.toJson(properties));
	}
	
	public boolean saveOrUpdate(String jsonParam) {
		if (StringUtils.isBlank(jsonParam)) {
			return false;
		}
		return checkSuccess(getMapResponse(SAVE_OR_UPDATE,jsonParam));
	}
	
	public boolean batchUpdateByIds(List<Integer> ids, Map<String, Object> updates) {
		if (CollectionUtils.isEmpty(ids) || MapUtils.isEmpty(updates)) {
			return false;
		}
		updates.put(IDS, ids);
		return checkSuccess(getMapResponse(BATCH_UPDATE_BY_IDS,updates));
	}
	
	public Map<Integer,Boolean> batchUpdate(List<Map<String, Object>> allUpdates, int batchSize) {
		
		if (batchSize < 0 || batchSize > MAX_BATCH_UPDATE_SIZE) {
			throw new IllegalArgumentException("illegal argument [batchSize] = " + batchSize);
		}
		if (CollectionUtils.isEmpty(allUpdates)){
			return Collections.EMPTY_MAP;
		}
		int updateSize = allUpdates.size();
		if (updateSize <= batchSize) {
			return batchUpdate(allUpdates);
		}
		Map<Integer,Boolean> result = new HashMap(updateSize);
		List<Map<String,Object>> perUpdates = new ArrayList(batchSize);
		for (int index=0; index < updateSize; index++) {
			perUpdates.add(allUpdates.get(index));
			if (index != 0 && (index % batchSize == 0)) {
				result.putAll(batchUpdate(perUpdates));
				perUpdates.clear();
			}
		}
		if (perUpdates.size() > 0) {
			result.putAll(batchUpdate(perUpdates));
		}
		return result;
	}
	
	public Map<Integer,Boolean> batchUpdate(List<Map<String, Object>> allUpdates) {
		if (CollectionUtils.isEmpty(allUpdates)){
			return Collections.EMPTY_MAP;
		}
		return(Map<Integer,Boolean>)requestForResult(BATCH_UPDATE,allUpdates);
	}
	
	public Map<Integer,Boolean>  batchInsert(List<Map<String, Object>> batchToSave) {
		if (CollectionUtils.isEmpty(batchToSave)){
			return Collections.EMPTY_MAP;
		}
		return(Map<Integer,Boolean>)requestForResult(BATCH_INSERT,batchToSave);
	}
	
	public Map<Integer,Boolean> batchInsert(List<T> batchToSave, int batchSize) {
		
		if (batchSize < 0 || batchSize > MAX_BATCH_INSERT_SIZE) {
			throw new IllegalArgumentException("illegal argument [batchSize] = " + batchSize);
		}
		if (CollectionUtils.isEmpty(batchToSave)){
			return Collections.EMPTY_MAP;
		}
		int insertSize = batchToSave.size();
		if (insertSize <= batchSize) {
			return batchInsert(batchToSave);
		}
		Map<Integer,Boolean> result = new HashMap(insertSize);
		List<T> perInsert = new ArrayList(batchSize);
		for (int index=0; index < insertSize; index++) {
			perInsert.add(batchToSave.get(index));
			if (index != 0 && (index % batchSize == 0)) {
				result.putAll(batchInsert(perInsert));
				perInsert.clear();
			}
		}
		if (perInsert.size() > 0) {
			result.putAll(batchInsert(perInsert));
		}
		return result;
	}
	
	public Map<Integer,Boolean>  batchInsert(Collection<T> batchToSave) {
		if (CollectionUtils.isEmpty(batchToSave)){
			return Collections.EMPTY_MAP;
		}
		return(Map<Integer,Boolean>)requestForResult(BATCH_INSERT,batchToSave);
	}
	
	public boolean delete(Map<String,Object> query) {
		return checkSuccess(getMapResponse(DELETE,query));
	}

	public boolean deleteById(PK id) {
		if (null == id) {
			return false;
		}
		return checkSuccess(getMapResponse(DELETE_BY_ID,id.toString()));
	}

	public long counts(Map<String, Object> query) {
		return Long.valueOf(requestForResult(COUNTS,query).toString());
	}
	
	/**
	 * 查询集合数量
	 * @return
	 */
	public long counts() {
		return counts(null);
	}

	/**
	 * 获取每个用户中Session中的查询条件
	 * @param query
	 * @param excludeCount
	 */
	public String  getCollectionRequestJson(Map<String, Object> query, Boolean excludeCount) {
		if (null == query) {
			return "{}";
		}
		return Json.toJson(query);
	}

	public String parseToRequestJson(Object query, Object sort, Object pagination) {
		return parseToRequestJson(query, sort, pagination, Boolean.valueOf(true));
	}

	/**
	 * 将map中的查询条件转换为json字符串，作为请求的参数
	 * @param query
	 * @param sort
	 * @param pagination
	 * @param excludeCount
	 */
	public String parseToRequestJson(Object query, Object sort, Object pagination, Boolean excludeCount) {
		HashMap<String, Object> request = new HashMap();
		if (null != query) {
			request.put("query", query);
		}
		if (null != sort) {
			request.put("sort", sort);
		}
		if (null != pagination) {
			request.put("pagination", pagination);
		}
		if (excludeCount.booleanValue()) {
			request.put("excludeCount", "1");
		}
		return Json.toJson(request);
	}

	public String parseRequestQueryGroupToRequestJson(Object query, Object group) {
		Map<String, Object> request = new HashMap();
		if (null != query) {
			request.put("query", query);
		}
		request.put("group", group);
		return Json.toJson(request);
	}

	public boolean checkSuccess(Map<String,Object> serviceResponseMap) {
		if (null == serviceResponseMap) {
			return false;
		}
		if (null == serviceResponseMap.get(CODE)) {
			return false;
		}
		if (!ResponseStatus.SUCCESS.getCode().equals(serviceResponseMap.get(CODE))) {
			return false;
		}
		if (null == serviceResponseMap.get(RESULT)) {
			return false;
		}
		return true;
	}
	/**
	 * 若无法满足实体映射可以被覆盖，提供自己的实现
	 * 
	 * @param clazz
	 *            clazz 实体的Class类型
	 * @param properties
	 *            实体Map 字段-->值
	 * @param ormPackageNames
	 *            一个或者多个自定义orm的包名
	 * @return
	 */
	protected T mapToEntity(Class<T> clazz, Map<String, Object> properties, String... ormPackageNames) {
		return MyBeanUtil.mapToEntity(clazz, properties, ormPackageNames);
	}
	
	protected List<T> mapToEntity(Class<T> clazz, Collection<Map<String, Object>> propertiesCol, String... ormPackageNames) {
		return MyBeanUtil.mapToEntity(clazz, propertiesCol, ormPackageNames);
	}
	
	/**
	 * 提供默认实现，也可以被覆盖，提高orm的所有包名
	 * 
	 * @return
	 */
	protected String[] prepareOrmPackageNames() {
		String[] names = { "" };
		return names;
	}
	
}
