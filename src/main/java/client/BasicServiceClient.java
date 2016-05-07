package client;

 
 import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import service.ResponseStatus;
import entity.BaseEntity;
import eyihcn.utils.GenericsUtils;
import eyihcn.utils.Json;
import eyihcn.utils.ServiceQueryHelper;
 
 /**
  * daoService请求的client
  * 抽象基本的crud ,数据库项目按系统模块部署到不同的服务器
  * @author eyihcn 
  *
  */
@SuppressWarnings({"unchecked","rawtypes"})
 public abstract class BasicServiceClient<T extends BaseEntity<PK>,PK extends Serializable> {
	 
		private static final String SAVE = "save";
		private static final String UPDATE = "update";
		private static final String FIND_ONE = "findOne";
		private static final String DELETE_BY_ID = "deleteById";
		private static final String FIND_LIST = "findList";
		private static final String FIND_COLLECTION = "findCollection";
		private static final String COUNTS = "counts";
		protected static final String COLLECTION = "collection";
		protected static final String COLLECTION_COUNT = "collectionCount";
		protected static final String SEPARATOR = "/";
		
		private String serviceTokenCode;
		private String modelName;
		private Class<T> entityClass;
		private String entityClassName; // simpleName
		private String saveEntry;
		private String updateEntry;
		private String findOneEntry;
		private String deleteByIdEntry;
		private String findListEntry;
		private String countsEntry;
		private String findCollectionEntry;
		private String[] ormPackageNames;
	 
		private String serviceAddress;
		private String serviceEntry;
		private String serviceRequest;
		private Map<String, Object> serviceResult = new HashMap<String, Object>();
		private String serviceToken;
		private RestTemplate restTemplate = new RestTemplate();
		private static Map<String, Map<String, String>> serviceRouterConfigs = new HashMap<String, Map<String,String>>();
		private int timeOut = -1;
	   
		public BasicServiceClient() {
			ModelCode modelCode = this.getClass().getAnnotation(ModelCode.class);
			if (null == modelCode) {
				throw new RuntimeException("can not find serviceCode and modelName, please add Annotation ModelCode !!!");
			}
			String serviceCode = modelCode.serviceCode();
			String modelName = modelCode.modelName();
			if (StringUtils.isBlank(serviceCode) || StringUtils.isBlank(modelName)) {
				throw new RuntimeException("can not find serviceCode and modelName, please add Annotation ModelCode !!!");
			}
			this.serviceTokenCode = serviceCode;
			this.modelName = modelName;
			init(serviceTokenCode);
			this.entityClass = GenericsUtils.getSuperClassGenericType(this.getClass());
			this.entityClassName = this.entityClass.getSimpleName();
			initCrudServiceEntry();
		}

		/**
		 * 提供默认实现，也可以被覆盖，提高orm的所有包名
		 * 
		 * @return
		 */
		public String[] prepareOrmPackageNames() {
			String[] names = { "" };
			return names;
		}

		/**
		 * 构建crud的ServiceEntry ---->/模块名/实体名/方法
		 */
		private void initCrudServiceEntry() {
			this.saveEntry = SEPARATOR+modelName+SEPARATOR+entityClassName+SEPARATOR+SAVE;
			this.updateEntry = SEPARATOR+modelName+SEPARATOR+entityClassName+SEPARATOR+UPDATE;
			this.findOneEntry = SEPARATOR+modelName+SEPARATOR+entityClassName+SEPARATOR+FIND_ONE;
			this.deleteByIdEntry = SEPARATOR+modelName+SEPARATOR+entityClassName+SEPARATOR+DELETE_BY_ID;
			this.findListEntry = SEPARATOR+modelName+SEPARATOR+entityClassName+SEPARATOR+FIND_LIST;
			this.countsEntry = SEPARATOR+modelName+SEPARATOR+entityClassName+SEPARATOR+COUNTS;
			this.countsEntry = SEPARATOR+modelName+SEPARATOR+entityClassName+SEPARATOR+COUNTS;
			this.findCollectionEntry = SEPARATOR+modelName+SEPARATOR+entityClassName+SEPARATOR+FIND_COLLECTION;
		}
   
		public Map<String, Object> findEntityCollection() {
			return findEntityCollection(null);
		}

		public Map<String, Object> findEntityCollection(Map<String, Object> query) {
			setServiceEntry(findCollectionEntry);
			Map<String, Object> map = null;
	 		if (MapUtils.isEmpty(query)) {
	 			map = getCollection();
			}else {
				map = getCollection(query);
			}
			List<T> entityCol = new ArrayList<T>();
			if (null != map) {
				for (Map<String, Object> o : (List<Map<String, Object>>) map.get(COLLECTION)) {
					entityCol.add(_mapToEntity(entityClass, o, ormPackageNames));
				}
				map.put(COLLECTION, entityCol);
			}
			return map;
		}

		public List<T> findEntityList(Map<String, Object> query, Map<String, Object> sort,Map<String, Object> pagination) {
			setServiceEntry(findListEntry);
			setServiceRequestQuery(query, sort, pagination);
			List<Map<String, Object>> result = requestList();
			List<T> entityList = new LinkedList<T>();
			if (null != result) {
				for (Map<String, Object> data : result) {
					entityList.add(_mapToEntity(entityClass, data, ormPackageNames));
				}
			}
			return entityList;
		}

		public  T findEntity(Map<String, Object> request) {
			setServiceEntry(findOneEntry);
			setServiceRequestQuery(request, null, null);
			return _mapToEntity(entityClass, (Map<String, Object>) request(), ormPackageNames);
		}

		public T findEntityById(Integer id) {
			if (null == id) {
				return null;
			}
			Map<String, Object> request = new HashMap<String, Object>();
			ServiceQueryHelper.and(request, "_id", id);
			return findEntity(request);
		}

		public boolean createEntity(Object object) {
			setServiceEntry(saveEntry);
			setServiceRequestCreate(object);
			request();
			return checkSuccess();
		}

		public boolean updateEntity(Object object) {
			setServiceEntry(updateEntry);
			setServiceRequestUpdate(object);
			request();
			return checkSuccess();
		}

		public boolean deleteEntityById(Integer id) {
			setServiceEntry(deleteByIdEntry);
			setServiceRequestId(id);
			request();
			return checkSuccess();
		}

		public int countsEntity(Map<String, Object> query) {
			setServiceEntry(countsEntry);
			setServiceRequestQuery(query, null, null);
			return Integer.parseInt(request().toString());
		}
   
   protected void init(String code) {
     Map<String, String> serviceConfig = serviceRouterConfigs.get(code);
     
     if (null == serviceConfig) {
       String serviceAddressKey = "JTOMTOPERP_" + code + "_SERVICE_ADDRESS";
       String serviceTokenKey = "JTOMTOPERP_" + code + "_SERVICE_TOKEN";
       serviceAddress = System.getenv(serviceAddressKey);
       if (null == serviceAddress)
       {
         ServerSettingService sss = new ServerSettingService();
         ServerPortSetting sp = sss.fetchServerPortSettingByCode(code);
         if (null == sp) {
           System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^error: no server config!");
         } else {
           serviceAddress = sp.getAddress();
           serviceToken = sp.getToken();
           serviceConfig = new HashMap<String, String>();
           serviceConfig.put("ADDRESS", serviceAddress);
           serviceConfig.put("TOKEN", serviceToken);
           serviceRouterConfigs.put(code, serviceConfig);
         }
       } else {
         serviceToken = System.getenv(serviceTokenKey);
       }
     } else {
       serviceAddress = ((String)serviceConfig.get("ADDRESS"));
       serviceToken = ((String)serviceConfig.get("TOKEN"));
     }
   }
   
   public Map<String, Object> getCollection(Boolean excludeCount) {
     return getCollection(null, excludeCount);
   }
   
   public Map<String, Object> getCollection() {
     return getCollection(Boolean.valueOf(false));
   }
   
 
   public Map<String, Object> getCollection(Map<String, Object> query) { return getCollection(query, Boolean.valueOf(false)); }
   
   public Long getCollectionCount(Map<String, Object> query) {
     _getCollectionRequest(query, Boolean.valueOf(false));
     
     Object result = request();
     if (null != result) {
       return new Long(result.toString());
     }
     
     return Long.valueOf(0L);
   }
   
   public Map<String, Object> getCollection(Map<String, Object> query, Boolean excludeCount) {
     _getCollectionRequest(query, excludeCount);
     
     return requestCollectionList();
   }
   
   private void _getCollectionRequest(Map<String, Object> query, Boolean excludeCount) {
//     SorterSession sorterSession = new SorterSession(null);
//     FilterSession filterSession = new FilterSession();
//     Filter filter = filterSession.getFilter();
//     PagerSession pagerSession = new PagerSession();
//     if (null == query) {
//       query = new LinkedHashMap();
//     }
//     
//     Object sort = null;
//     if (null != sorterSession.getSorter()) {
//       String sorterKey = sorterSession.getSorter().getKey();
//       Integer braceIndex = Integer.valueOf(sorterKey.indexOf("["));
//       if (braceIndex.intValue() > 0) {
//         sorterKey = sorterKey.substring(0, braceIndex.intValue());
//       }
//       sort = ServiceSorterHelper.build(sorterKey, sorterSession.getSorter().getDirection());
//     }
//     
//     setServiceRequestQuery(
//       ServiceQueryHelper.and(query, filter.getQuery()), sort, 
//       
//       ServicePaginationHelper.build(Integer.valueOf(pagerSession.getPager().getPageLimit()), Integer.valueOf(pagerSession.getPager().getCurrentPage())), excludeCount);
   }
   
 
   public Map<String, Object> requestCollectionList()
   {
     Object result = _request();
     if (null != result) {
       return (Map)result;
     }
     return null;
   }
   
   public List<Map<String, Object>> requestList() {
     Object result = _request();
     if (null != result) {
       return (List)result;
     }
     
     return null;
   }
   
   private Object _request() {
     try {
       String requestUrl = StringUtils.stripEnd(getServiceAddress(), "/") + StringUtils.stripEnd(getServiceEntry(), "/") + "?token=" + getServiceToken();
       if (null == getServiceRequest()) {
         setServiceRequest("{}");
       }
       System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^lion " + StringUtils.stripEnd(getServiceAddress(), "/") + StringUtils.stripEnd(getServiceEntry(), "/"));
       System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^lion " + getServiceRequest());
       
       MultiValueMap<String, Object> headers = new LinkedMultiValueMap();
       headers.add("Accept", "application/json;charset=utf-8");
       headers.add("Content-Type", "application/json;charset=utf-8");
       String requestBody = getServiceRequest();
       HttpEntity httpEntity = new HttpEntity(requestBody, headers);
       
       serviceResult = ((Map)restTemplate.postForObject(requestUrl, httpEntity, LinkedHashMap.class, new Object[0]));
       
       activateTimeOut();
       
       if (!serviceResult.containsKey("code")) {
         serviceResult.put("code", ResponseStatus.ERROR.getCode());
         return null;
       }
       
       if (null != serviceResult.get("result")) {
         return serviceResult.get("result");
       }
     } catch (Exception e) {
    	 serviceResult.put("code", ResponseStatus.ERROR.getCode());
    	 e.printStackTrace();
     }
     
     return null;
   }
   
   private void activateTimeOut() {
     if (timeOut > 0) {
       Object factory = restTemplate.getRequestFactory();
       if ((factory instanceof SimpleClientHttpRequestFactory)) {
         System.out.println("HttpUrlConnection is used");
         ((SimpleClientHttpRequestFactory)factory).setConnectTimeout(timeOut);
         ((SimpleClientHttpRequestFactory)factory).setReadTimeout(timeOut);
       } else if ((factory instanceof HttpComponentsClientHttpRequestFactory)) {
         System.out.println("HttpClient is used");
         ((HttpComponentsClientHttpRequestFactory)factory).setReadTimeout(timeOut);
         ((HttpComponentsClientHttpRequestFactory)factory).setConnectTimeout(timeOut);
       }
     }
   }
   
   public Object request() {
     return _request();
   }
   
   public String getServiceAddress() {
     return serviceAddress;
   }
   
   public void setServiceAddress(String serviceAddress) {
     this.serviceAddress = serviceAddress;
   }
   
   public String getServiceEntry() {
     return serviceEntry;
   }
   
   public void setServiceEntry(String serviceEntry) {
     this.serviceEntry = serviceEntry;
   }
   
   public String getServiceRequest() {
     return serviceRequest;
   }
   
   public void setServiceRequest(HashMap<String, Object> request) {
     setServiceRequest(Json.toJson(request));
   }
   
   public void setServiceRequest(String serviceRequest) {
     this.serviceRequest = serviceRequest;
   }
   
   public Map<String, Object> getServiceResult() {
     return serviceResult;
   }
   
   public void setServiceResult(Map<String, Object> serviceResult) {
     this.serviceResult = serviceResult;
   }
   
   public String getServiceToken() {
     return serviceToken;
   }
   
   public void setServiceToken(String serviceToken) {
     this.serviceToken = serviceToken;
   }
   
   public void setServiceRequestId(Object id) {
     setServiceRequest(id.toString());
   }
   
   public void setServiceRequestQuery(Object query, Object sort, Object pagination) {
     setServiceRequestQuery(query, sort, pagination, Boolean.valueOf(true));
   }
   
   public void setServiceRequestQuery(Object query, Object sort, Object pagination, Boolean excludeCount) {
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
     
     setServiceRequest(Json.toJson(request));
   }
   
   public void setServiceRequestQueryGroup(Object query, Object group) {
     HashMap<String, Object> request = new HashMap();
     if (null != query) {
       request.put("query", query);
     }
     
     request.put("group", group);
     
     setServiceRequest(Json.toJson(request));
   }
   
   public String setServiceRequestBatchUpdate(Object object) {
     if ((object instanceof String)) {
       setServiceRequest((String)object);
     } else if ((object instanceof List)) {
       Map<String, Object> request = new HashMap();
       request.put("updates", object);
       setServiceRequest(Json.toJson(request));
     } else {
       HashMap<String, Object> request = new HashMap();
       Map<String, Object> updates = new HashMap();
       if ((object instanceof Map)) {
         updates = (Map)object;
       } else {
         updates = (Map)Json.fromJson(Json.toJson(object), Map.class);
       }
       request.put("ids", updates.get("ids"));
       request.put("updates", updates);
       setServiceRequest(Json.toJson(request));
     }
     
     return getServiceRequest();
   }
   
   public String setServiceRequestUpdate(Object object) {
     if ((object instanceof String)) {
       setServiceRequest((String)object);
     } else {
       HashMap<String, Object> request = new HashMap();
       Map<String, Object> updates = new HashMap();
       if ((object instanceof Map)) {
         updates = (Map)object;
       } else {
         updates = (Map)Json.fromJson(Json.toJson(object), Map.class);
       }
       request.put("id", updates.get("id"));
       request.put("updates", updates);
       setServiceRequest(Json.toJson(request));
     }
     
     return getServiceRequest();
   }
   
   public String setServiceRequestCreateBatch(Object object) {
     if ((object instanceof String)) {
       setServiceRequest((String)object);
     } else {
       setServiceRequest(Json.toJson(object));
     }
     
     return getServiceRequest();
   }
   
   public String setServiceRequestCreate(Object object) {
     Map<String, Object> request = new HashMap();
     if ((object instanceof Map)) {
       request = (Map)object;
     } else {
       request = (Map)Json.fromJson(Json.toJson(object), Map.class);
     }
     setServiceRequest(Json.toJson(request));
     
     return getServiceRequest();
   }
   
   public Boolean checkSuccess() {
     if ((null != serviceResult) && (null != serviceResult.get("code")) && (
       (ResponseStatus.SUCCESS.getCode().equals(serviceResult.get("code"))) || ((ResponseStatus.ERROR.equals(serviceResult.get("code"))) && (null != serviceResult.get("result"))))) {
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
	public T _mapToEntity(Class<T> clazz, Map<String, Object> properties, String... ormPackageNames) {
		T entity = null;
		try {
			entity = clazz.newInstance();
			Class<?> classType = clazz;
			Field[] fs = classType.getDeclaredFields(); // 得到所有的fields
			for (Field f : fs) {
				String fieldName = f.getName();
				Object fieldValue = properties.get(fieldName);
				if (null == fieldValue) {
					continue;
				}
				Class fieldClazz = f.getType(); // 得到field的class及类型全路径
				// 【1】 //判断是否为基本类型 或者 lang包中的类型
				if (fieldClazz.isPrimitive() || fieldClazz.getName().startsWith("java.lang")) {
					BeanUtils.setProperty(entity, fieldName, properties.get(fieldName));
					continue;
				}
				// 【2】 关键的地方，如果是Collection类型，得到其Generic的类型
				if (Collection.class.isAssignableFrom(fieldClazz)) {
					Type fc = f.getGenericType();
					if (fc == null) {
						continue;
					}
					// 【3】如果是泛型参数的类型
					if (fc instanceof ParameterizedType) {
						ParameterizedType pt = (ParameterizedType) fc;
						// 暂时把多层嵌套的泛型，当作基本类型处理
						Type type = pt.getActualTypeArguments()[0];
						if (type instanceof ParameterizedType) {
							BeanUtils.setProperty(entity, fieldName, properties.get(fieldName));
							continue;
						}
						Collection col = null;
						if (List.class.isAssignableFrom(fieldClazz)) {
							col = new ArrayList();
						} else {
							col = new HashSet();
						}
						Class genericClazz = (Class) pt.getActualTypeArguments()[0];
						if (!isSelfDesignOrm(genericClazz, ormPackageNames)) {
							BeanUtils.setProperty(entity, fieldName, properties.get(fieldName));
							continue;
						}
						// 若List的元素为自定义的orm，应该做递归处理
						Collection<Map<String, Object>> listMap = (Collection<Map<String, Object>>) properties.get(fieldName);
						for (Map entityMap : listMap) {
							col.add(_mapToEntity(genericClazz, entityMap, ormPackageNames));
						}
						BeanUtils.setProperty(entity, fieldName, col);
					}
					continue;
				}
				// 若为自定义的orm // com.tomtop.application.orm
				if (isSelfDesignOrm(fieldClazz, ormPackageNames)) {
					BeanUtils.setProperty(entity, fieldName,
							_mapToEntity(fieldClazz, (Map<String, Object>) properties.get(fieldName), ormPackageNames));
					continue;
				}
				BeanUtils.setProperty(entity, fieldName, properties.get(fieldName));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * 先判断包名是否以系统自定义的orm包的前缀，若是直接返回true， 否则在判断是否包含在自己提供的orm包名内
	 * 
	 * @param clazz
	 * @param ormPackageNames
	 *            自定义orm的包名
	 * @return true:是自定义orm false:不是自定义orm
	 */
	private boolean isSelfDesignOrm(Class<?> clazz, String... ormPackageNames) {
		if (null == ormPackageNames || ormPackageNames.length == 0) {
			return clazz.getPackage().getName().startsWith("com.tomtop.application.orm");
		}
		if (clazz.getPackage().getName().startsWith("com.tomtop.application.orm")) {
			return true;
		}
		return Arrays.asList(ormPackageNames).contains(clazz.getPackage().getName());
	}
 }

