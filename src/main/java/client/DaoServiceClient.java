package client;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import service.ResponseStatus;
import service.ServiceResponse;
import utils.Json;
import utils.MyBeanUtils;
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
public abstract class DaoServiceClient<T extends BaseEntity<PK>, PK extends Serializable> {

	final Logger log = LoggerFactory.getLogger(DaoServiceClient.class);
	
	private RestTemplate restTemplate = new RestTemplate();
	private static Map<String, Map<String, String>> serviceRouterConfigs = new HashMap<String, Map<String, String>>();

	protected static final String COLLECTION = "collection";
	protected static final String COLLECTION_COUNT = "collectionCount";
	protected static final String SEPARATOR = "/";

	private String modelName;
	private Class<T> entityClass;
	private String entityClassName; // simpleName
	private String[] ormPackageNames;

	private String host; // 主机
	private String token; // 令牌
	private String serviceEntry; 
	private String requestParam;
	private Map<String, Object> serviceResponseMap = new HashMap<String, Object>(); // 响应结果
	private ServiceResponse serviceResponse; // 响应结果 daoService的响应协议格式
	private String responseJson ; // 响应结果的json字符串
	private int timeOut = -1;
	private ModelCode modelCode;

	public DaoServiceClient() {
		this.modelCode = this.getClass().getAnnotation(ModelCode.class);
//		initRquestHostAndToken(initServiceCode());
		initRquestHostAndToken_2(initServiceCode());
		this.modelName =  initModelName();
		this.entityClass = MyBeanUtils.getSuperClassGenericType(this.getClass());
		this.entityClassName = this.entityClass.getSimpleName();
	}
	
	/**
	 * 通过注解指定serviceCode的方式可以被覆盖，自定义 
	 * @return
	 */
	protected String initServiceCode() {
		if (null == modelCode) {
			throw new RuntimeException("can not find serviceCode and modelName, please add Annotation ModelCode !!!");
		}
		return modelCode.serviceCode();
	}
	
	/**
	 * 通过注解指定modelName的方式可以被覆盖，自定义 
	 * @return
	 */
	protected String initModelName() {
		if (null == modelCode) {
			return "";
		}
		return modelCode.modelName();
	}

	/**
	 * 初始化请求的Host和Token
	 */
	protected void initRquestHostAndToken(String serviceTokenCode) {
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
		host = System.getenv(serviceAddressKey);
		if (null != host) {
			token = System.getenv(serviceTokenKey);
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

	/**
	 * 构建crud的ServiceEntry ---->/模块名/实体名/方法
	 */
	private void initServiceEntry(String methodName) {
		if (StringUtils.isBlank(methodName)) {
			throw new IllegalArgumentException("methodName cannot be null !!!");
		}
		this.serviceEntry = new StringBuilder(SEPARATOR).append(modelName ).append( SEPARATOR ).append( entityClassName ).append( SEPARATOR ).append(methodName).toString();
	}
	
	/**
	 * 构建crud的ServiceEntry ---->/模块名/实体名/方法
	 */
	private void initServiceEntry( RequestMethodName requestMethodName) {
		initServiceEntry(requestMethodName.getMethodName());
	}
	
	/**
	 * headers支持 json格式
	 * @param responseType 返回结果类型
	 * @param requsetURL 请求URL
	 * @param jsonParam json请求参数字符串
	 * @return
	 */
	public <E> E request(Class<E> responseType,String requsetURL,String jsonParam) {
		
		E response = null;
		log.info(new StringBuilder("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^lion ").append(requsetURL).toString());
		log.info(new StringBuilder("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^lion ").append(jsonParam).toString());
		try {
			MultiValueMap<String, Object> headers = new LinkedMultiValueMap();
			headers.add("Accept", "application/json;charset=utf-8");
			headers.add("Content-Type", "application/json;charset=utf-8");
			HttpEntity httpEntity = new HttpEntity(jsonParam, headers);
			response = restTemplate.postForObject(requsetURL, httpEntity,responseType , new Object[0]);
			activateTimeOut();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	protected Object requestForResult() {
		try {
			serviceResponse = request(ServiceResponse.class, buildRequestURL(), this.requestParam==null?"{}":this.requestParam);
			if (serviceResponse == null) {
				serviceResponse = new ServiceResponse(ResponseStatus.ERROR);
				return null;
			}
			return serviceResponse.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 返回请求结果的JSON字符穿
	 * 
	 * @return
	 */
	public String getJSONResponse() {
		responseJson = request(String.class, buildRequestURL(), this.requestParam==null?"{}":this.requestParam);
		return responseJson;
	}
	
	/**
	 * 返回请求结果的Map
	 * 
	 * @return
	 */
	public Map<String,Object> getMapResponse() {
		serviceResponseMap = request(Map.class, buildRequestURL(), this.requestParam==null?"{}":this.requestParam);
		return serviceResponseMap;
	}
	
	/**
	 * serviceAdderss[ip:port] + serviceEntry[/模块名/实体名/方法] + ? +serviceToken=[token]
	 * @return
	 */
	private  String buildRequestURL() {
		return new StringBuilder(StringUtils.stripEnd(getServiceAddress(), "/"))
				.append( StringUtils.stripEnd(getServiceEntry(), "/"))
				.append("?token=" + getServiceToken())
				.toString();
	}
	
	public Map<String, Object> findEntityCollection() {
		return findEntityCollection(null);
	}

	public Map<String, Object> findEntityCollection(Map<String, Object> query) {
		
		initServiceEntry(RequestMethodName.FIND_COLLECTION);
		Map<String, Object> map = null;
		if (MapUtils.isEmpty(query)) {
			map = getCollection();
		} else {
			map = getCollection(query);
		}
		map.put(COLLECTION, _mapToEntity(entityClass,(Collection<Map<String, Object>>)map.get(COLLECTION), ormPackageNames));
		return map;
	}

	public List<T> findEntityList(Map<String, Object> query, Map<String, Object> sort, Map<String, Object> pagination) {
		
		initServiceEntry(RequestMethodName.FIND_LIST);
		setServiceRequestQuery(query, sort, pagination);
		return _mapToEntity(entityClass, (Collection<Map<String, Object>>) requestForResult(), ormPackageNames);
	}

	public T findEntity(Map<String, Object> request) {
		
		initServiceEntry(RequestMethodName.FIND_ONE);
		setRequestParam(Json.toJson(request));
		return _mapToEntity(entityClass, (Map<String, Object>) requestForResult(), ormPackageNames);
	}

	public T findEntityById(PK id) {
		if (null == id) {
			return null;
		}
		initServiceEntry(RequestMethodName.FIND_BY_ID);
		setRequestParam(id.toString());
		return _mapToEntity(entityClass, (Map<String, Object>) requestForResult(), ormPackageNames);
	}

	public boolean createEntity(T entity) {
		return createEntity(Json.toJson(entity));
	}
	
	public boolean createEntity(Map<String,Object> properties) {
		return createEntity(Json.toJson(properties));
	}
	
	public boolean createEntity(String jsonParam) {
		initServiceEntry(RequestMethodName.SAVE);
		setRequestParam(jsonParam);
		getMapResponse();
		return checkSuccess();
	}

	public boolean updateEntity(T entity) {
		return updateEntity(Json.toJson(entity));
	}
	
	public boolean updateEntity(Map<String,Object> properties) {
		return updateEntity(Json.toJson(properties));
	}
	
	public boolean updateEntity(String jsonParam) {
		initServiceEntry(RequestMethodName.UPDATE);
		setRequestParam(jsonParam);
		getMapResponse();
		return checkSuccess();
	}
	
	public boolean saveOrUpdateEntity(T entity) {
		return updateEntity(Json.toJson(entity));
	}
	
	public boolean saveOrUpdateEntity(Map<String,Object> properties) {
		return updateEntity(Json.toJson(properties));
	}
	
	public boolean saveOrUpdateEntity(String jsonParam) {
		initServiceEntry(RequestMethodName.SAVE_OR_UPDATE);
		setRequestParam(jsonParam);
		getMapResponse();
		return checkSuccess();
	}
	
	public boolean delete(Map<String,Object> query) {
		initServiceEntry(RequestMethodName.DELETE);
		setRequestParam(Json.toJson(query));
		getMapResponse();
		return checkSuccess();
	}

	public boolean deleteEntityById(PK id) {
		initServiceEntry(RequestMethodName.DELETE_BY_ID);
		setRequestParam(id.toString());
		getMapResponse();
		return checkSuccess();
	}

	public int countsEntity(Map<String, Object> query) {
		initServiceEntry(RequestMethodName.COUNTS);
		setRequestParam(Json.toJson(query));
		return Integer.parseInt(requestForResult().toString());
	}

	public Map<String, Object> getCollection(Boolean excludeCount) {
		return getCollection(null, excludeCount);
	}

	public Map<String, Object> getCollection() {
		return getCollection(Boolean.valueOf(false));
	}

	public Map<String, Object> getCollection(Map<String, Object> query) {
		return getCollection(query, Boolean.valueOf(false));
	}

	public Map<String, Object> getCollection(Map<String, Object> query, Boolean excludeCount) {
		_getCollectionRequest(query, excludeCount);
		return (Map<String, Object>) requestForResult();
	}

	/**
	 * 获取每个用户中Session中的查询条件
	 * @param query
	 * @param excludeCount
	 */
	private void _getCollectionRequest(Map<String, Object> query, Boolean excludeCount) {
		setRequestParam(query);
	}

	private void activateTimeOut() {
		if (timeOut > 0) {
			Object factory = restTemplate.getRequestFactory();
			if ((factory instanceof SimpleClientHttpRequestFactory)) {
				System.out.println("HttpUrlConnection is used");
				((SimpleClientHttpRequestFactory) factory).setConnectTimeout(timeOut);
				((SimpleClientHttpRequestFactory) factory).setReadTimeout(timeOut);
			} else if ((factory instanceof HttpComponentsClientHttpRequestFactory)) {
				System.out.println("HttpClient is used");
				((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout(timeOut);
				((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout(timeOut);
			}
		}
	}


	public String getServiceAddress() {
		return host;
	}

	public void setServiceAddress(String serviceAddress) {
		this.host = serviceAddress;
	}

	public String getServiceEntry() {
		return serviceEntry;
	}

	public void setServiceEntry(String serviceEntry) {
		this.serviceEntry = serviceEntry;
	}

	public String getRequestParam() {
		return requestParam;
	}

	public void setRequestParam(String requestParam) {
		this.requestParam = requestParam;
	}
	
	public void setRequestParam(Map<String,Object> mapParam) {
		setRequestParam(Json.toJson(mapParam));
	}

	public Map<String, Object> getServiceResult() {
		return serviceResponseMap;
	}

	public void setServiceResult(Map<String, Object> serviceResult) {
		this.serviceResponseMap = serviceResult;
	}

	public String getServiceToken() {
		return token;
	}

	public void setServiceToken(String serviceToken) {
		this.token = serviceToken;
	}

	public void setServiceRequestId(Object id) {
		setRequestParam(id.toString());
	}

	public void setServiceRequestQuery(Object query, Object sort, Object pagination) {
		setServiceRequestQuery(query, sort, pagination, Boolean.valueOf(true));
	}

	/**
	 * 将map中的查询条件转换为json字符串，作为请求的参数
	 * @param query
	 * @param sort
	 * @param pagination
	 * @param excludeCount
	 */
	public void setServiceRequestQuery(Object query, Object sort, Object pagination, Boolean excludeCount) {
		HashMap<String, Object> request = new HashMap();
			request.put("query", query);
			if (null != query) {
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

		setRequestParam(Json.toJson(request));
	}

	public void setServiceRequestQueryGroup(Object query, Object group) {
		HashMap<String, Object> request = new HashMap();
		if (null != query) {
			request.put("query", query);
		}

		request.put("group", group);

		setRequestParam(Json.toJson(request));
	}

	public String setRequestParamBatchUpdate(Object object) {
		if ((object instanceof String)) {
			setRequestParam((String) object);
		} else if ((object instanceof List)) {
			Map<String, Object> request = new HashMap();
			request.put("updates", object);
			setRequestParam(Json.toJson(request));
		} else {
			HashMap<String, Object> request = new HashMap();
			Map<String, Object> updates = new HashMap();
			if ((object instanceof Map)) {
				updates = (Map) object;
			} else {
				updates = (Map) Json.fromJson(Json.toJson(object), Map.class);
			}
			request.put("ids", updates.get("ids"));
			request.put("updates", updates);
			setRequestParam(Json.toJson(request));
		}

		return getRequestParam();
	}

	public String setServiceRequestUpdate(Object object) {
		if ((object instanceof String)) {
			setRequestParam((String) object);
		} else {
			HashMap<String, Object> request = new HashMap();
			Map<String, Object> updates = new HashMap();
			if ((object instanceof Map)) {
				updates = (Map) object;
			} else {
				updates = (Map) Json.fromJson(Json.toJson(object), Map.class);
			}
			request.put("id", updates.get("id"));
			request.put("updates", updates);
			setRequestParam(Json.toJson(request));
		}

		return getRequestParam();
	}

	public String setServiceRequestCreateBatch(Object object) {
		if ((object instanceof String)) {
			setRequestParam((String) object);
		} else {
			setRequestParam(Json.toJson(object));
		}

		return getRequestParam();
	}

	public String setServiceRequestCreate(Object object) {
		Map<String, Object> request = new HashMap();
		if ((object instanceof Map)) {
			request = (Map) object;
		} else {
			request = (Map) Json.fromJson(Json.toJson(object), Map.class);
		}
		setRequestParam(Json.toJson(request));

		return getRequestParam();
	}

	public Boolean checkSuccess() {
		if ((null != serviceResponseMap) && (null != serviceResponseMap.get("code"))
				&& ((ResponseStatus.SUCCESS.getCode().equals(serviceResponseMap.get("code"))) 
						|| ((ResponseStatus.ERROR.equals(serviceResponseMap.get("code")))
								&& (null != serviceResponseMap.get("result"))))) {
			return Boolean.valueOf(true);
		}

		return Boolean.valueOf(false);
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
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
	protected T _mapToEntity(Class<T> clazz, Map<String, Object> properties, String... ormPackageNames) {
		return MyBeanUtils._mapToEntity(clazz, properties, ormPackageNames);
	}
	
	protected List<T> _mapToEntity(Class<T> clazz, Collection<Map<String, Object>> propertiesCol, String... ormPackageNames) {
		return MyBeanUtils._mapToEntity(clazz, propertiesCol, ormPackageNames);
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
