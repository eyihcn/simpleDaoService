package client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import entity.ServerPortSetting;
@ModelName("serverSetting")
@ServiceCode("SERVER_PORT_SETTING")
@Component
public class ServerSettingService extends DaoServiceClient<ServerPortSetting, Integer> {

	
	public ServerPortSetting fetchServerPortSettingByCode(String code) {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("code", code);
		return findOne(query);
	}
}
