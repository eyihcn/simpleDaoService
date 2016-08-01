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
import utils.ServicePaginationHelper;
import utils.ServiceQueryHelper;
import entity.BaseEntity;
import entity.ServerPortSetting;

/**
 * @author tomtop2016
 * @version May 24, 2016 8:34:38 PM
 * @description 
 * 			支持内部DAO Service请求 ,once constructed ,is thread safe
 * @param <T>
 * 			  实体类型
 * @param <PK>
 *            实体主键类型
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class DaoServiceClient<T extends BaseEntity<PK>, PK extends Serializable> extends BaseServiceClient {

	public static final String FIND_COLLECTION = "findCollection";
	public static final String FIND_LIST = "findList";
	public static final String FIND_ONE = "findOne";
	public static final String FIND_BY_ID = "findById";
	public static final String SAVE = "save";
	public static final String UPDATE = "update";
	public static final String SAVE_BY_UPSERT = "saveByUpsert";
	public static final String DELETE = "delete";
	public static final String DELETE_BY_ID = "deleteById";
	public static final String COUNTS = "counts";
	public static final String CHECK_EXISTS = "checkExists";
	public static final String BATCH_UPDATE_BY_IDS = "batchUpdateByIds";
	public static final String BATCH_UPDATE = "batchUpdate";
	public static final String BATCH_SAVE_BY_UPSERT= "batchSaveByUpsert";
	public static final String BATCH_INSERT = "batchInsert";
	public static final String FIND_IDS = "findIds";
	public static final String GENERATE_PRIMARY_KEY_BY_OFFSET = "generatePrimaryKeyByOffset";
	
	public static final String COLLECTION = "collection";
	public static final String COLLECTION_COUNT = "collectionCount";
	public static final String IDS = "ids";
	public static final String CODE = "code";
	public static final String RESULT = "result";
	public static final int MAX_BATCH_INSERT_SIZE=5000;
	public static final int MAX_BATCH_UPDATE_SIZE=5000;
	protected String host; // 主机
	protected String token; // 令牌
	private String modelName;
	private Class<T> entityClass;
	private Class<PK> pkClass; 
	private String entityClassName; // simpleName
	private String[] ormPackageNames;
	
	/**
	 * @description
	 * 			初始化host、token、className、modelName
	 */
	public DaoServiceClient() {
//		String[] hostAndToken = initServiceAddressAndToken(this.getClass().getAnnotation(ServiceCode.class).value());
//		this.host  = hostAndToken[0];
//		this.token = hostAndToken[1];
		initRquestHostAndToken_2(this.getClass().getAnnotation(ServiceCode.class).value());
		this.modelName =  this.getClass().getAnnotation(ModelName.class).value();
		this.pkClass = 	MyBeanUtil.getSuperClassGenericType(this.getClass(), 1);
		this.entityClass = MyBeanUtil.getSuperClassGenericType(this.getClass());
		this.entityClassName = this.entityClass.getSimpleName();
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
	
	
	protected Object  requestForResult(String requestMethodName,Object requestParam) {
		try {
			ServiceResponse serviceResponse = super.request(ServiceResponse.class, _buildDaoServiceRequestURL(requestMethodName), requestParam,headers);
			if (serviceResponse == null) {
				return null;
			}
			return serviceResponse.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected Map<String,Object> getMapResponse(String requestMethodName,Object requestParam) {
		return super.getMapResponse(host,_buildDaoServiceEntry(requestMethodName),token,requestParam);
	}
	
	/**
	 * serviceAdderss[ip:port] + serviceEntry[/模块名/实体名/方法] + ? +serviceToken=[token]
	 */
	private  String _buildDaoServiceRequestURL(String requestMethodName) {
		return super.buildRequestURL(host,_buildDaoServiceEntry(requestMethodName), token);
	}
	/**serviceEntry[/模块名/实体名/方法]*/
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
			return Collections.emptyMap();
		}
		// 3. 将result(Map) 转换为实体
		result.put(COLLECTION, mapToEntity(entityClass,(Collection<Map<String, Object>>)result.get(COLLECTION), ormPackageNames));
		return result;
	}
	
	/**默认的分页查询条件PageNum为1，PageSize为5000*/
	public List<T> findList(Map<String, Object> query) {
		return findList(query,null, ServicePaginationHelper.build(5000, 1));
	}
	
	public List<T> findList(Map<String, Object> query, Map<String, Object> sort, Map<String, Object> pagination) {
		
		Collection<Map<String, Object>> collMap = (Collection<Map<String, Object>>)requestForResult(FIND_LIST,parseToRequestJson(query, sort, pagination));
		if (CollectionUtils.isEmpty(collMap)) {
			return Collections.emptyList();
		}
		return mapToEntity(entityClass, collMap, ormPackageNames);
	}

	public T findOne(Map<String, Object> query) {
		return findOne(query,null);
	}
	
	public T findOne(Map<String, Object> query, Map<String, Object> sort) {
		
		Map<String, Object> entityMap = (Map<String, Object>) requestForResult(FIND_ONE,parseToRequestJson(query, sort, null));
		if (MapUtils.isEmpty(entityMap)) {
			return null;
		}
		return mapToEntity(entityClass, entityMap, ormPackageNames);
	}

	public T findById(PK id) {
		if (null == id) {
			return null;
		}
		return mapToEntity(entityClass, (Map<String, Object>) requestForResult(FIND_BY_ID,id.toString()), ormPackageNames);
	}
	
	public List<T> findByIds(Collection<PK> idsColl) {
		if (CollectionUtils.isEmpty(idsColl)) {
			return Collections.EMPTY_LIST;
		}
		Map<String,Object> query = new HashMap(); 
		ServiceQueryHelper.and(query, "id",idsColl,ServiceQueryHelper.IN);
		return findList(query);
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
	
	public boolean saveByUpsert(T entity) {
		if (entity == null) {
			return false;
		}
		return saveByUpsert(Json.toJson(entity));
	}
	
	public boolean saveByUpsert(Map<String,Object> properties) {
		if (MapUtils.isEmpty(properties)) {
			return false;
		}
		return saveByUpsert(Json.toJson(properties));
	}
	
	public boolean saveByUpsert(String jsonParam) {
		if (StringUtils.isBlank(jsonParam)) {
			return false;
		}
		return checkSuccess(getMapResponse(SAVE_BY_UPSERT,jsonParam));
	}
	
	public boolean batchUpdateByIds(List<Integer> ids, Map<String, Object> updates) {
		if (CollectionUtils.isEmpty(ids) || MapUtils.isEmpty(updates)) {
			return false;
		}
		updates.put(IDS, ids);
		return checkSuccess(getMapResponse(BATCH_UPDATE_BY_IDS,updates));
	}
	/**批量更新结果Map的key为实体的id(这里的String有点坑)，value为更新的结果，true:成功*/
	public Map<String ,Boolean> batchUpdate(List<Map<String, Object>> allUpdates, int batchSize) {
		
		if (batchSize < 0 || batchSize > MAX_BATCH_UPDATE_SIZE) {
			throw new IllegalArgumentException("illegal argument [batchSize] = " + batchSize);
		}
		if (CollectionUtils.isEmpty(allUpdates)){
			return Collections.EMPTY_MAP;
		}
		int updateSize = allUpdates.size();
		if (updateSize <= batchSize) {
			return _batchUpdate(allUpdates);
		}
		long start = System.currentTimeMillis(); // start
		System.out.println(new StringBuilder("start to batchUpdate by single thread: allUpdates.size() = ").append(updateSize).append(", batchSize = ").append(batchSize).toString());

		Map<String,Boolean> result = new HashMap(updateSize);
		List<Map<String,Object>> perUpdates = new ArrayList(batchSize);
		for (int index=0; index < updateSize; index++) {
			perUpdates.add(allUpdates.get(index));
			if (index != 0 && (index % batchSize == 0)) {
				result.putAll(_batchUpdate(perUpdates));
				perUpdates.clear();
			}
		}
		allUpdates = null;
		if (perUpdates.size() > 0) {
			result.putAll(_batchUpdate(perUpdates));
		}
		long costTime = System.currentTimeMillis() - start; //ms
		System.out.println(new StringBuilder("batchUpdate by single thread end : ").append("allUpdates.size() = ").append(updateSize)
				.append(", totalTaskCounts = ").append("totalTaskCounts ,").append("costTime : ").append(costTime).append("ms").toString());
		return result;
	}
	
	/**批量更新结果Map的key为实体的id(这里的String有点坑)，value为更新的结果，true:成功*/
	public Map<String,Boolean> batchUpdate(List<Map<String, Object>> allUpdates) {
		if (CollectionUtils.isEmpty(allUpdates)){
			return Collections.EMPTY_MAP;
		}
		return batchUpdate(allUpdates, MAX_BATCH_UPDATE_SIZE);
	}
	
	private Map<String,Boolean> _batchUpdate(List<Map<String, Object>> allUpdates) {
		if (CollectionUtils.isEmpty(allUpdates)){
			return Collections.EMPTY_MAP;
		}
		return(Map<String,Boolean>)requestForResult(BATCH_UPDATE,allUpdates);
	}
	
	/**批量保存或者更新（没有id或者有id但数据库中无记录，则保存。否则更新),结果Map的key为List集合中的序列(这里的String有点坑)，value为更新的结果，true:成功*/
	public Map<String,Boolean> batchSaveByUpsert(List<T> allSaveByUpserts) {
		if (CollectionUtils.isEmpty(allSaveByUpserts)){
			return Collections.EMPTY_MAP;
		}
		return batchSaveByUpsert(allSaveByUpserts, MAX_BATCH_UPDATE_SIZE);
	}
	
	public Map<String,Boolean> batchSaveByUpsert(List<T> allSaveByUpserts, int batchSize) {
		
		if (batchSize < 0 || batchSize > MAX_BATCH_INSERT_SIZE) {
			throw new IllegalArgumentException("illegal argument [batchSize] = " + batchSize);
		}
		if (CollectionUtils.isEmpty(allSaveByUpserts)){
			return Collections.EMPTY_MAP;
		}
		int insertSize = allSaveByUpserts.size();
		if (insertSize <= batchSize) {
			return _batchSaveByUpsert(allSaveByUpserts);
		}
		long start = System.currentTimeMillis(); // start
		System.out.println(new StringBuilder("start to allSaveOrUpdates by single thread: batchToSave.size() = ").append(insertSize).append(", batchSize = ").append(batchSize).toString());
	
		Map<String,Boolean> result = new HashMap<String,Boolean>(insertSize);
		List<T> per = new ArrayList<T>(batchSize);
		Map<String,Boolean> perResult = null; 
		for (int index=1; index <= insertSize; index++) {
			per.add(allSaveByUpserts.get(index-1));
			if (index % batchSize == 0) {
				perResult = _batchSaveByUpsert(per);
				int seq_start  = (index / batchSize - 1) * batchSize ;
				for (String seq : perResult.keySet()) {
					result.put(Integer.valueOf((Integer.valueOf(seq)+seq_start)).toString(), perResult.get(seq));
				}
				per.clear();
			}
		}
		allSaveByUpserts = null;
		if (per.size() > 0) {
			perResult = _batchSaveByUpsert(per);
			int seq_start  = (insertSize / batchSize)* batchSize;
			for (String seq : perResult.keySet()) {
				result.put(Integer.valueOf((Integer.valueOf(seq)+seq_start)).toString(), perResult.get(seq));
			}
		}
		long costTime = System.currentTimeMillis() - start; //ms
		System.out.println(new StringBuilder("allSaveOrUpdates by single thread end : ").append("batchToSave.size() = ").append(insertSize)
				.append(" ,costTime : ").append(costTime).append("ms").toString());
		return result;
	}
	
	private Map<String,Boolean> _batchSaveByUpsert(List<T> allSaveByUpserts) {
		if (CollectionUtils.isEmpty(allSaveByUpserts)){
			return Collections.EMPTY_MAP;
		}
		return(Map<String,Boolean>)requestForResult(BATCH_SAVE_BY_UPSERT,allSaveByUpserts);
	}
	
	/**批量插入结果Map的key为实体的id(这里的String有点坑)，value为更新的结果，true:成功*/
	public Map<String,Boolean> batchInsert(List<T> batchToSave, int batchSize) {
		
		if (batchSize < 0 || batchSize > MAX_BATCH_INSERT_SIZE) {
			throw new IllegalArgumentException("illegal argument [batchSize] = " + batchSize);
		}
		if (CollectionUtils.isEmpty(batchToSave)){
			return Collections.EMPTY_MAP;
		}
		int insertSize = batchToSave.size();
		if (insertSize <= batchSize) {
			return _batchInsert(batchToSave);
		}
		long start = System.currentTimeMillis(); // start
		System.out.println(new StringBuilder("start to batchInsert by single thread: batchToSave.size() = ").append(insertSize).append(", batchSize = ").append(batchSize).toString());
	
		Map<String,Boolean> result = new HashMap<String,Boolean>(insertSize);
		List<T> perInsert = new ArrayList<T>(batchSize);
		Map<String,Boolean> perResult = null; 
		for (int index=1; index <= insertSize; index++) {
			perInsert.add(batchToSave.get(index-1));
			if (index % batchSize == 0) {
				perResult = _batchInsert(perInsert);
				int seq_start  = (index / batchSize - 1) * batchSize ;
				for (String seq : perResult.keySet()) {
					result.put(Integer.valueOf((Integer.valueOf(seq)+seq_start)).toString(), perResult.get(seq));
				}
				perInsert.clear();
			}
		}
		batchToSave = null;
		if (perInsert.size() > 0) {
			perResult = _batchInsert(perInsert);
			int seq_start  = (insertSize / batchSize)* batchSize;
			for (String seq : perResult.keySet()) {
				result.put(Integer.valueOf((Integer.valueOf(seq)+seq_start)).toString(), perResult.get(seq));
			}
		}
		long costTime = System.currentTimeMillis() - start; //ms
		System.out.println(new StringBuilder("batchInsert by single thread end : ").append("batchToSave.size() = ").append(insertSize)
				.append(" ,costTime : ").append(costTime).append("ms").toString());
		return result;
	}
	
	private Map<String,Boolean>  _batchInsert(List<T> batchToSave) {
		if (CollectionUtils.isEmpty(batchToSave)){
			return Collections.EMPTY_MAP;
		}
		return(Map<String,Boolean>)requestForResult(BATCH_INSERT,batchToSave);
	}
	
	/**批量插入结果Map的key为实体的id(这里的String有点坑)，value为更新的结果，true:成功*/
	public Map<String,Boolean>  batchInsert(List<T> batchToSave) {
		if (CollectionUtils.isEmpty(batchToSave)){
			return Collections.EMPTY_MAP;
		}
		return batchInsert(batchToSave,MAX_BATCH_INSERT_SIZE);
	}
	
	public boolean delete(Map<String, Object> query, Map<String, Object> sort, Map<String, Object> pagination) {
		return checkSuccess(getMapResponse(DELETE,parseToRequestJson(query, sort, pagination)));
	}
	
	public boolean delete(Map<String,Object> query) {
		return delete(query,null,null);
	}

	public boolean deleteById(PK id) {
		if (null == id) {
			return false;
		}
		return checkSuccess(getMapResponse(DELETE_BY_ID,id.toString()));
	}
	
	public boolean deleteByIds(Collection<PK> idsColl) {
		if (CollectionUtils.isEmpty(idsColl)) {
			return false;
		}
		Map<String,Object> query = new HashMap(); 
		ServiceQueryHelper.and(query, "id",idsColl,ServiceQueryHelper.IN);
		return delete(query);
	}

	public long counts(Map<String, Object> query) {
		Object counts = requestForResult(COUNTS,query);
		if (null == counts) {
			return 0;
		}
		return Long.valueOf(counts.toString());
	}
	
	public long counts() {
		return counts(null);
	}
	
	public boolean checkExists(Map<String, Object> query, Map<String, Object> sort, Map<String, Object> pagination) {
		return Boolean.valueOf(requestForResult(CHECK_EXISTS,parseToRequestJson(query, sort, pagination)).toString());
	}
	
	public boolean checkExists(Map<String, Object> query) {
		return checkExists(query,null,null);
	}
	
	public List<PK> findIds(Map<String, Object> query, Map<String, Object> sort, Map<String, Object> pagination) {
		return (List<PK>) requestForResult(FIND_IDS,parseToRequestJson(query, sort, pagination));
	}
	
	public List<PK> findIds(Map<String, Object> query) {
		return findIds(query, null ,null);
	}

	/**
	 * 根据主键的生产方式和偏移量，生产主键
	 * @param offset 生成主键的数量
	 */
	public List<PK> generatePrimaryKeys( int offset ) {
		if (offset < 1) {
			throw new IllegalArgumentException("offset can not be less than zero !!!");
		}
		Object pkObj = requestForResult(GENERATE_PRIMARY_KEY_BY_OFFSET,Integer.valueOf(offset));
		if (null == pkObj) {
			return Collections.emptyList();
		}
		// 坑！！！ ，Dao返回的Long类型竟然给我搞成了Integer
		Long endId = Long.valueOf(pkObj.toString());
		List<PK> pks = new ArrayList<PK>(offset);
		for (long counts = offset-1; counts >= 0; counts --) {
			try {
				pks.add(pkClass.getConstructor(String.class).newInstance(Long.valueOf(endId - counts).toString()));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return pks;
	}
	
	/** 获取每个用户中Session中的查询条件*/
//	protected String  getCollectionRequestJson(Map<String, Object> query, Boolean excludeCount) {
//		
//		SorterSession sorterSession = new SorterSession(null);
//		FilterSession filterSession = new FilterSession();
//		Filter filter = filterSession.getFilter();
//		PagerSession pagerSession = new PagerSession();
//		if (null == query) {
//			query = new LinkedHashMap();
//		}
//		Map<String,Object> sort = null;
//		if (null != sorterSession.getSorter()) {
//			String sorterKey = sorterSession.getSorter().getKey();
//			Integer braceIndex = Integer.valueOf(sorterKey.indexOf("["));
//			if (braceIndex.intValue() > 0) {
//				sorterKey = sorterKey.substring(0, braceIndex.intValue());
//			}
//			sort = ServiceSorterHelper.build(sorterKey, sorterSession.getSorter().getDirection());
//		}
//		return parseToRequestJson(
//				ServiceQueryHelper.and(query, filter.getQuery()),
//				sort,
//				ServicePaginationHelper.build(Integer.valueOf(pagerSession.getPager().getPageLimit()),
//						Integer.valueOf(pagerSession.getPager().getCurrentPage())), excludeCount);
//	}
	/** 获取每个用户中Session中的查询条件*/
	protected String  getCollectionRequestJson(Map<String, Object> query, Boolean excludeCount) {
		return parseToRequestJson(query, null, null);
	}

	protected String parseToRequestJson(Map<String,Object> query, Map<String,Object> sort, Map<String,Object> pagination) {
		return parseToRequestJson(query, sort, pagination, Boolean.valueOf(true));
	}

	/** 将map中的查询条件转换为json字符串，作为请求的参数*/
	protected String parseToRequestJson(Map<String,Object> query, Map<String,Object> sort, Map<String,Object> pagination, Boolean excludeCount) {
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

	protected String parseRequestQueryGroupToRequestJson(Map<String,Object> query, Map<String,Object> group) {
		Map<String, Object> request = new HashMap();
		if (null != query) {
			request.put("query", query);
		}
		request.put("group", group);
		return Json.toJson(request);
	}

	protected boolean checkSuccess(Map<String,Object> serviceResponseMap) {
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
	
	/** 提供默认实现，也可以被覆盖，提高orm的所有包名*/
	protected String[] prepareOrmPackageNames() {
		String[] names = {"com.tomtop.application.orm"};
		return names;
	}
}
