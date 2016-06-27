package client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import utils.Json;
import entity.ServerPortSetting;

/**
 * 获取路由和发送请求
 * @author eyihcn
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class BaseServiceClient {

	
	final Logger log = LoggerFactory.getLogger(DaoServiceClient.class);

	protected static Map<String, Map<String, String>> serviceRouterConfigs = new ConcurrentHashMap();
	@Autowired
	protected RestTemplate restTemplate ; //once constructed ,is thread safe
	protected String host; // 主机
	protected String token; // 令牌
	protected int timeOut = -1;
	
	public static final String SEPARATOR = "/";
	
	public static MultiValueMap<String, Object> headers ;
	static {
		headers = new LinkedMultiValueMap();
		headers.add("Accept", "application/json;charset=utf-8");
		headers.add("Content-Type", "application/json;charset=utf-8");
	}
	
	/**
	 * 初始化请求的Host和Token
	 */
	protected void initServiceAddressAndToken(String serviceTokenCode) {
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
	 * 
	 * @param responseType 返回结果类型
	 * @param requsetURL 请求URL
	 * @param requestParam 请求参数
	 * @param headers 请求头
	 * @return
	 */
	public <E> E request(Class<E> responseType,String requsetURL,Object requestParam,MultiValueMap<String, Object> headers ) {
		
		E response = null;
		if (!(requestParam instanceof String)) {
			requestParam = requestParam==null?"{}":Json.toJson(requestParam);
		}
		String tempURL = null;
		int index = requsetURL.indexOf("?");
		if (index > -1) {
			tempURL = requsetURL.substring(0, index);
		}else {
			tempURL = requsetURL;
		}
		log.info(new StringBuilder("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^lion ").append(tempURL).toString());
		log.info(new StringBuilder("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^lion ").append(requestParam).toString());
		try {
			HttpEntity httpEntity = new HttpEntity(requestParam, headers);
			response = restTemplate.postForObject(requsetURL, httpEntity,responseType , new Object[0]);
			_activateTimeOut();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	/**
	 * serviceAdderss[ip:port] + serviceEntry[/XX/XX] + ? +token=[serviceToken]
	 * @return
	 */
	public  String buildRequestURL(String ServiceAdress,String ServiceEntry,String serviceToken) {
		
		StringBuilder url = new StringBuilder(StringUtils.stripEnd(ServiceAdress, "/")).append( StringUtils.stripEnd(ServiceEntry,"/"));
		if (StringUtils.isNotBlank(serviceToken)) {
			url.append("?token=" + serviceToken).toString();
		}
		return url.toString();
	}
	
	/**
	 * API 和 内部DAO的ServiceEntry的组织方式不同
	 * @param ServiceEntry
	 * @param requestParam
	 * @return
	 */
	public Map<String,Object> getMapResponse(String ServiceEntry, Object requestParam) {
		return  request(Map.class, buildRequestURL(host,ServiceEntry,token), requestParam, headers);
	}
	
	protected void _activateTimeOut() {
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
	
	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		log.info("Autowired... " + restTemplate);
		this.restTemplate = restTemplate;
	}	
}
