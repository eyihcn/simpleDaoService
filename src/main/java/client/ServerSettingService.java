package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.tomtop.application.orm.admin.ServerPortSetting;
import com.tomtop.system.service.RestBasicService;
import com.tomtop.system.service.ServerSettingBasicService;

public class ServerSettingService extends RestBasicService {
	//ServerPortSetting.class
	private final String CREATE_SERVER_PORT_SETTING_ENTRY						= "/serverSetting/createServerPortSetting";
    private final String DELETE_SERVER_PORT_SETTING_ENTRY                      	= "/serverSetting/deleteServerPortSetting";
    private final String UPDATE_SERVER_PORT_SETTING_ENTRY                       = "/serverSetting/updateServerPortSetting";
    private final String FETCH_SERVER_PORT_SETTING_ENTRY                        = "/serverSetting/fetchServerPortSettingById";
    private final String QUERY_SERVER_PORT_SETTING_ENTRY                        = "/serverSetting/queryServerPortSetting";
    private final String QUERY_SERVER_PORT_SETTINGS_ENTRY                       = "/serverSetting/queryServerPortSettings";
    private final String FETCH_SERVER_PORT_SETTING_COLLECTION_ENTRY             = "/serverSetting/fetchServerPortSettingCollection";
    
    public ServerSettingService() {
        init("SERVER_PORT_SETTING");
//    	setServiceToken("tomtop");
//		setServiceAddress("http://192.168.220.31:7215");
    }
    
    
    /***
     * ServerPortSetting.class
     */
    public Boolean createServerPortSetting(Object object) {
        setServiceEntry(CREATE_SERVER_PORT_SETTING_ENTRY);
        setServiceRequestCreate(object);
        request();

        return checkSuccess();
    }

    public Boolean deleteServerPortSetting(Object id) {
        setServiceEntry(DELETE_SERVER_PORT_SETTING_ENTRY);
        setServiceRequestId(id);
        request();

        return checkSuccess();
    }

    public Boolean updateServerPortSetting(Object object) {
        setServiceEntry(UPDATE_SERVER_PORT_SETTING_ENTRY);
        setServiceRequestUpdate(object);
        request();

        return checkSuccess();
    }

	public ServerPortSetting fetchServerPortSettingById(Integer id) {
        setServiceEntry(FETCH_SERVER_PORT_SETTING_ENTRY);
        setServiceRequestId(id);
        return _mapToServerPortSetting((Map<String, Object>)request());
    }

    public ServerPortSetting queryServerPortSetting(Map<String, Object> query) {
        setServiceEntry(QUERY_SERVER_PORT_SETTING_ENTRY);
        setServiceRequestQuery(query, null, null);

        return _mapToServerPortSetting((Map<String, Object>)request());
    }

    public List<ServerPortSetting> queryServerPortSettings(Map<String, Object> query, Map<String, Object> sort, Map<String, Object> pagination) {
        setServiceEntry(QUERY_SERVER_PORT_SETTINGS_ENTRY);
        setServiceRequestQuery(query, sort, pagination);
        List<Map<String, Object>> result = requestList();
        List<ServerPortSetting> serverPortSettings = new LinkedList<ServerPortSetting>();
        if (null != result) {
            for (Map<String, Object> data: result) {
                serverPortSettings.add(_mapToServerPortSetting(data));
            }
        }
        return serverPortSettings;
    }

    public Map<String, Object> fetchServerPortSettingCollection(Map<String, Object> query) {
        setServiceEntry(FETCH_SERVER_PORT_SETTING_COLLECTION_ENTRY);

        Map<String, Object> collection = getCollection(query);
        List<ServerPortSetting> serverPortSettings = new ArrayList<ServerPortSetting>();
        if (null != collection) {
            for (Object o: (List<Object>)collection.get("collection")) {
            	serverPortSettings.add(_mapToServerPortSetting((Map<String, Object>) o));
            }
            collection.put("collection", serverPortSettings);
            collection.put("collectionCount", collection.get("collectionCount"));
        }

        return collection;
    }
    
    public ServerPortSetting fetchServerPortSettingByCode(String code) {
    	 Map<String, Object> query = new HashMap<String, Object>();
    	 query.put("code", code);
    	 setServiceEntry(QUERY_SERVER_PORT_SETTING_ENTRY);
    	 setServiceRequestQuery(query, null, null);
    	 
    	 return _mapToServerPortSetting((Map<String, Object>) request());
    }

    public ServerPortSetting _mapToServerPortSetting(Map<String, Object> data) {
        try {
            if (null == data) {
                return null;
            }
            ServerPortSetting serverPortSetting = new ServerPortSetting();
            BeanUtils.populate(serverPortSetting, data);
            return serverPortSetting;
        } catch (Exception e) {
            e.printStackTrace();
	        return null;
        }
    }
}
