package client;

/*     */ 
/*     */ /*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;

/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.springframework.http.HttpEntity;
/*     */ import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
/*     */ import org.springframework.http.client.SimpleClientHttpRequestFactory;
/*     */ import org.springframework.util.LinkedMultiValueMap;
/*     */ import org.springframework.util.MultiValueMap;
/*     */ import org.springframework.web.client.RestTemplate;

import service.ResponseStatus;
import eyihcn.utils.Json;
import eyihcn.utils.ServiceQueryHelper;
import eyihcn.utils.ServiceSorterHelper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class RestBasicService
/*     */ {
/*     */   private String serviceAddress;
/*     */   private String serviceEntry;
/*     */   private String serviceRequest;
/*  39 */   private Map<String, Object> serviceResult = new HashMap();
/*     */   private String serviceToken;
/*  41 */   private RestTemplate restTemplate = new RestTemplate();
/*  42 */   private static Map<String, Map<String, String>> serviceConfigs = new HashMap();
/*  43 */   private int timeOut = -1;
/*     */   
/*     */   protected void init(String code) {
/*  46 */     Map<String, String> serviceConfig = (Map)serviceConfigs.get(code);
/*     */     
/*  48 */     if (null == serviceConfig) {
/*  49 */       String serviceAddressKey = "JTOMTOPERP_" + code + "_SERVICE_ADDRESS";
/*  50 */       String serviceTokenKey = "JTOMTOPERP_" + code + "_SERVICE_TOKEN";
/*  51 */       serviceAddress = System.getenv(serviceAddressKey);
/*  52 */       if (null == serviceAddress)
/*     */       {
/*  54 */         ServerSettingService sss = new ServerSettingService();
/*  55 */         ServerPortSetting sp = sss.fetchServerPortSettingByCode(code);
/*  56 */         if (null == sp) {
/*  57 */           System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^error: no server config!");
/*     */         } else {
/*  59 */           serviceAddress = sp.getAddress();
/*  60 */           serviceToken = sp.getToken();
/*  61 */           serviceConfig = new HashMap();
/*  62 */           serviceConfig.put("ADDRESS", serviceAddress);
/*  63 */           serviceConfig.put("TOKEN", serviceToken);
/*  64 */           serviceConfigs.put(code, serviceConfig);
/*     */         }
/*     */       } else {
/*  67 */         serviceToken = System.getenv(serviceTokenKey);
/*     */       }
/*     */     } else {
/*  70 */       serviceAddress = ((String)serviceConfig.get("ADDRESS"));
/*  71 */       serviceToken = ((String)serviceConfig.get("TOKEN"));
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<String, Object> getCollection(Boolean excludeCount) {
/*  76 */     return getCollection(null, excludeCount);
/*     */   }
/*     */   
/*     */   public Map<String, Object> getCollection() {
/*  80 */     return getCollection(Boolean.valueOf(false));
/*     */   }
/*     */   
/*     */ 
/*  84 */   public Map<String, Object> getCollection(Map<String, Object> query) { return getCollection(query, Boolean.valueOf(false)); }
/*     */   
/*     */   public Long getCollectionCount(Map<String, Object> query) {
/*  87 */     _getCollectionRequest(query, Boolean.valueOf(false));
/*     */     
/*  89 */     Object result = request();
/*  90 */     if (null != result) {
/*  91 */       return new Long(result.toString());
/*     */     }
/*     */     
/*  94 */     return Long.valueOf(0L);
/*     */   }
/*     */   
/*     */   public Map<String, Object> getCollection(Map<String, Object> query, Boolean excludeCount) {
/*  98 */     _getCollectionRequest(query, excludeCount);
/*     */     
/* 100 */     return requestCollectionList();
/*     */   }
/*     */   
/*     */   private void _getCollectionRequest(Map<String, Object> query, Boolean excludeCount) {
/* 104 */     SorterSession sorterSession = new SorterSession(null);
/* 105 */     FilterSession filterSession = new FilterSession();
/* 106 */     Filter filter = filterSession.getFilter();
/* 107 */     PagerSession pagerSession = new PagerSession();
/* 108 */     if (null == query) {
/* 109 */       query = new LinkedHashMap();
/*     */     }
/*     */     
/* 112 */     Object sort = null;
/* 113 */     if (null != sorterSession.getSorter()) {
/* 114 */       String sorterKey = sorterSession.getSorter().getKey();
/* 115 */       Integer braceIndex = Integer.valueOf(sorterKey.indexOf("["));
/* 116 */       if (braceIndex.intValue() > 0) {
/* 117 */         sorterKey = sorterKey.substring(0, braceIndex.intValue());
/*     */       }
/* 119 */       sort = ServiceSorterHelper.build(sorterKey, sorterSession.getSorter().getDirection());
/*     */     }
/*     */     
/* 122 */     setServiceRequestQuery(
/* 123 */       ServiceQueryHelper.and(query, filter.getQuery()), sort, 
/*     */       
/* 125 */       ServicePaginationHelper.build(Integer.valueOf(pagerSession.getPager().getPageLimit()), Integer.valueOf(pagerSession.getPager().getCurrentPage())), excludeCount);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> requestCollectionList()
/*     */   {
/* 131 */     Object result = _request();
/* 132 */     if (null != result) {
/* 133 */       return (Map)result;
/*     */     }
/* 135 */     return null;
/*     */   }
/*     */   
/*     */   public List<Map<String, Object>> requestList() {
/* 139 */     Object result = _request();
/* 140 */     if (null != result) {
/* 141 */       return (List)result;
/*     */     }
/*     */     
/* 144 */     return null;
/*     */   }
/*     */   
/*     */   private Object _request() {
/*     */     try {
/* 149 */       String requestUrl = StringUtils.stripEnd(getServiceAddress(), "/") + StringUtils.stripEnd(getServiceEntry(), "/") + "?token=" + getServiceToken();
/* 150 */       if (null == getServiceRequest()) {
/* 151 */         setServiceRequest("{}");
/*     */       }
/* 153 */       System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^lion " + StringUtils.stripEnd(getServiceAddress(), "/") + StringUtils.stripEnd(getServiceEntry(), "/"));
/* 154 */       System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^lion " + getServiceRequest());
/*     */       
/* 156 */       MultiValueMap<String, Object> headers = new LinkedMultiValueMap();
/* 157 */       headers.add("Accept", "application/json;charset=utf-8");
/* 158 */       headers.add("Content-Type", "application/json;charset=utf-8");
/* 159 */       String requestBody = getServiceRequest();
/* 160 */       HttpEntity httpEntity = new HttpEntity(requestBody, headers);
/*     */       
/* 162 */       serviceResult = ((Map)restTemplate.postForObject(requestUrl, httpEntity, LinkedHashMap.class, new Object[0]));
/*     */       
/* 164 */       activateTimeOut();
/*     */       
/* 166 */       if (!serviceResult.containsKey("code")) {
/* 167 */         serviceResult.put("code", ServiceResponseCode.SERVER_ERROR);
/* 168 */         return null;
/*     */       }
/*     */       
/* 171 */       if (null != serviceResult.get("result")) {
/* 172 */         return serviceResult.get("result");
/*     */       }
/*     */     } catch (Exception e) {
/* 175 */       serviceResult.put("code", ServiceResponseCode.SERVER_ERROR);
/* 176 */       e.printStackTrace();
/*     */     }
/*     */     
/* 179 */     return null;
/*     */   }
/*     */   
/*     */   private void activateTimeOut() {
/* 183 */     if (timeOut > 0) {
/* 184 */       Object factory = restTemplate.getRequestFactory();
/* 185 */       if ((factory instanceof SimpleClientHttpRequestFactory)) {
/* 186 */         System.out.println("HttpUrlConnection is used");
/* 187 */         ((SimpleClientHttpRequestFactory)factory).setConnectTimeout(timeOut);
/* 188 */         ((SimpleClientHttpRequestFactory)factory).setReadTimeout(timeOut);
/* 189 */       } else if ((factory instanceof HttpComponentsClientHttpRequestFactory)) {
/* 190 */         System.out.println("HttpClient is used");
/* 191 */         ((HttpComponentsClientHttpRequestFactory)factory).setReadTimeout(timeOut);
/* 192 */         ((HttpComponentsClientHttpRequestFactory)factory).setConnectTimeout(timeOut);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Object request() {
/* 198 */     return _request();
/*     */   }
/*     */   
/*     */   public String getServiceAddress() {
/* 202 */     return serviceAddress;
/*     */   }
/*     */   
/*     */   public void setServiceAddress(String serviceAddress) {
/* 206 */     this.serviceAddress = serviceAddress;
/*     */   }
/*     */   
/*     */   public String getServiceEntry() {
/* 210 */     return serviceEntry;
/*     */   }
/*     */   
/*     */   public void setServiceEntry(String serviceEntry) {
/* 214 */     this.serviceEntry = serviceEntry;
/*     */   }
/*     */   
/*     */   public String getServiceRequest() {
/* 218 */     return serviceRequest;
/*     */   }
/*     */   
/*     */   public void setServiceRequest(HashMap<String, Object> request) {
/* 222 */     setServiceRequest(Json.toJson(request));
/*     */   }
/*     */   
/*     */   public void setServiceRequest(String serviceRequest) {
/* 226 */     this.serviceRequest = serviceRequest;
/*     */   }
/*     */   
/*     */   public Map<String, Object> getServiceResult() {
/* 230 */     return serviceResult;
/*     */   }
/*     */   
/*     */   public void setServiceResult(Map<String, Object> serviceResult) {
/* 234 */     this.serviceResult = serviceResult;
/*     */   }
/*     */   
/*     */   public String getServiceToken() {
/* 238 */     return serviceToken;
/*     */   }
/*     */   
/*     */   public void setServiceToken(String serviceToken) {
/* 242 */     this.serviceToken = serviceToken;
/*     */   }
/*     */   
/*     */   public void setServiceRequestId(Object id) {
/* 246 */     setServiceRequest(id.toString());
/*     */   }
/*     */   
/*     */   public void setServiceRequestQuery(Object query, Object sort, Object pagination) {
/* 250 */     setServiceRequestQuery(query, sort, pagination, Boolean.valueOf(true));
/*     */   }
/*     */   
/*     */   public void setServiceRequestQuery(Object query, Object sort, Object pagination, Boolean excludeCount) {
/* 254 */     HashMap<String, Object> request = new HashMap();
/* 255 */     if (null != query) {
/* 256 */       request.put("query", query);
/*     */     }
/* 258 */     if (null != sort) {
/* 259 */       request.put("sort", sort);
/*     */     }
/* 261 */     if (null != pagination) {
/* 262 */       request.put("pagination", pagination);
/*     */     }
/* 264 */     if (excludeCount.booleanValue()) {
/* 265 */       request.put("excludeCount", "1");
/*     */     }
/*     */     
/* 268 */     setServiceRequest(Json.toJson(request));
/*     */   }
/*     */   
/*     */   public void setServiceRequestQueryGroup(Object query, Object group) {
/* 272 */     HashMap<String, Object> request = new HashMap();
/* 273 */     if (null != query) {
/* 274 */       request.put("query", query);
/*     */     }
/*     */     
/* 277 */     request.put("group", group);
/*     */     
/* 279 */     setServiceRequest(Json.toJson(request));
/*     */   }
/*     */   
/*     */   public String setServiceRequestBatchUpdate(Object object) {
/* 283 */     if ((object instanceof String)) {
/* 284 */       setServiceRequest((String)object);
/* 285 */     } else if ((object instanceof List)) {
/* 286 */       Map<String, Object> request = new HashMap();
/* 287 */       request.put("updates", object);
/* 288 */       setServiceRequest(Json.toJson(request));
/*     */     } else {
/* 290 */       HashMap<String, Object> request = new HashMap();
/* 291 */       Map<String, Object> updates = new HashMap();
/* 292 */       if ((object instanceof Map)) {
/* 293 */         updates = (Map)object;
/*     */       } else {
/* 295 */         updates = (Map)Json.fromJson(Json.toJson(object), Map.class);
/*     */       }
/* 297 */       request.put("ids", updates.get("ids"));
/* 298 */       request.put("updates", updates);
/* 299 */       setServiceRequest(Json.toJson(request));
/*     */     }
/*     */     
/* 302 */     return getServiceRequest();
/*     */   }
/*     */   
/*     */   public String setServiceRequestUpdate(Object object) {
/* 306 */     if ((object instanceof String)) {
/* 307 */       setServiceRequest((String)object);
/*     */     } else {
/* 309 */       HashMap<String, Object> request = new HashMap();
/* 310 */       Map<String, Object> updates = new HashMap();
/* 311 */       if ((object instanceof Map)) {
/* 312 */         updates = (Map)object;
/*     */       } else {
/* 314 */         updates = (Map)Json.fromJson(Json.toJson(object), Map.class);
/*     */       }
/* 316 */       request.put("id", updates.get("id"));
/* 317 */       request.put("updates", updates);
/* 318 */       setServiceRequest(Json.toJson(request));
/*     */     }
/*     */     
/* 321 */     return getServiceRequest();
/*     */   }
/*     */   
/*     */   public String setServiceRequestCreateBatch(Object object) {
/* 325 */     if ((object instanceof String)) {
/* 326 */       setServiceRequest((String)object);
/*     */     } else {
/* 328 */       setServiceRequest(Json.toJson(object));
/*     */     }
/*     */     
/* 331 */     return getServiceRequest();
/*     */   }
/*     */   
/*     */   public String setServiceRequestCreate(Object object) {
/* 335 */     Map<String, Object> request = new HashMap();
/* 336 */     if ((object instanceof Map)) {
/* 337 */       request = (Map)object;
/*     */     } else {
/* 339 */       request = (Map)Json.fromJson(Json.toJson(object), Map.class);
/*     */     }
/* 341 */     setServiceRequest(Json.toJson(request));
/*     */     
/* 343 */     return getServiceRequest();
/*     */   }
/*     */   
/*     */   public Boolean checkSuccess() {
/* 347 */     if ((null != serviceResult) && (null != serviceResult.get("code")) && (
/* 348 */       (ResponseStatus.SUCCESS.getCode().equals(serviceResult.get("code"))) || ((ServiceResponseCode.ERROR.equals(serviceResult.get("code"))) && (null != serviceResult.get("result"))))) {
/* 349 */       return Boolean.valueOf(true);
/*     */     }
/*     */     
/* 352 */     return Boolean.valueOf(false);
/*     */   }
/*     */   
/*     */   public int getTimeOut() {
/* 356 */     return timeOut;
/*     */   }
/*     */   
/*     */   public void setTimeOut(int timeOut) {
/* 360 */     this.timeOut = timeOut;
/*     */   }
/*     */ }

/* Location:           D:\maven_repo\com\tomtop\jtomtoperp-system\1.1.4\jtomtoperp-system-1.1.4.jar
 * Qualified Name:     com.tomtop.system.service.RestBasicService
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */