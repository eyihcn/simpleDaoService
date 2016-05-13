package client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSettingService extends DaoServiceClient<ServerPortSetting, Integer> {
	// ServerPortSetting.class
	private final String CREATE_SERVER_PORT_SETTING_ENTRY = "/serverSetting/createServerPortSetting";
	private final String DELETE_SERVER_PORT_SETTING_ENTRY = "/serverSetting/deleteServerPortSetting";
	private final String UPDATE_SERVER_PORT_SETTING_ENTRY = "/serverSetting/updateServerPortSetting";
	private final String FETCH_SERVER_PORT_SETTING_ENTRY = "/serverSetting/fetchServerPortSettingById";
	private final String QUERY_SERVER_PORT_SETTING_ENTRY = "/serverSetting/queryServerPortSetting";
	private final String QUERY_SERVER_PORT_SETTINGS_ENTRY = "/serverSetting/queryServerPortSettings";
	private final String FETCH_SERVER_PORT_SETTING_COLLECTION_ENTRY = "/serverSetting/fetchServerPortSettingCollection";

	@Override
	protected String initServiceCode() {
		return "SERVER_PORT_SETTING";
	}

	/***
	 * ServerPortSetting.class
	 */
	public Boolean createServerPortSetting(Object object) {
		setServiceEntry(CREATE_SERVER_PORT_SETTING_ENTRY);
		setServiceRequestCreate(object);
		getMapResponse();
		return checkSuccess();
	}

	public Boolean deleteServerPortSetting(Object id) {
		setServiceEntry(DELETE_SERVER_PORT_SETTING_ENTRY);
		setServiceRequestId(id);
		getMapResponse();
		return checkSuccess();
	}

	public Boolean updateServerPortSetting(Object object) {
		setServiceEntry(UPDATE_SERVER_PORT_SETTING_ENTRY);
		setServiceRequestUpdate(object);
		getMapResponse();
		return checkSuccess();
	}

	public ServerPortSetting fetchServerPortSettingById(Integer id) {
		setServiceEntry(FETCH_SERVER_PORT_SETTING_ENTRY);
		setServiceRequestId(id);
		return (ServerPortSetting) requestForResult();
	}

	public ServerPortSetting queryServerPortSetting(Map<String, Object> query) {
		setServiceEntry(QUERY_SERVER_PORT_SETTING_ENTRY);
		setServiceRequestQuery(query, null, null);
		return (ServerPortSetting) requestForResult();
	}

	@SuppressWarnings("unchecked")
	public List<ServerPortSetting> queryServerPortSettings(Map<String, Object> query, Map<String, Object> sort, Map<String, Object> pagination) {
		setServiceEntry(QUERY_SERVER_PORT_SETTINGS_ENTRY);
		setServiceRequestQuery(query, sort, pagination);
		return (List<ServerPortSetting>) requestForResult();
	}

	public Map<String, Object> fetchServerPortSettingCollection(Map<String, Object> query) {

		setServiceEntry(FETCH_SERVER_PORT_SETTING_COLLECTION_ENTRY);
		return getCollection(query);
	}

	public ServerPortSetting fetchServerPortSettingByCode(String code) {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("code", code);
		setServiceEntry(QUERY_SERVER_PORT_SETTING_ENTRY);
		setServiceRequestQuery(query, null, null);
		return (ServerPortSetting) requestForResult();
	}

}
