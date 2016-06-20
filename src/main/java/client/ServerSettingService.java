package client;

import org.springframework.stereotype.Component;

import entity.ServerPortSetting;
@ModelName("serverSetting")
@ServiceCode("SERVER_PORT_SETTING")
@Component
public class ServerSettingService extends DaoServiceClient<ServerPortSetting, Integer> {

}
