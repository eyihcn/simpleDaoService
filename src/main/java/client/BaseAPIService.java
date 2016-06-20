package client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import utils.Json;
import entity.ServerPortSetting;

public class BaseAPIService {

	private static Map<String, Map<String, String>> serviceRouterConfigs = new ConcurrentHashMap();
	private RestTemplate restTemplate ;
	private String host; // 主机
	private String token; // 令牌
	private int timeOut = -1;
	
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
		log.info(new StringBuilder("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^lion ").append(requsetURL).toString());
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
}
