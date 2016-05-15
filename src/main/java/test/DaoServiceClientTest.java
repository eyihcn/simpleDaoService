package test;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import client.ProductServiceClient;
import client.ServerSettingService;

public class DaoServiceClientTest {

	private ApplicationContext applicationContext;

	@Before
	public void setup() {
		System.out.println("before");
		this.applicationContext = new ClassPathXmlApplicationContext("classpath:/testMongoTemplate/mongo.xml");
	}
	
	@Test
	public void test2() {
		ProductServiceClient client=  applicationContext.getBean(ProductServiceClient.class);
		System.out.println(client.countsEntity(new HashMap<String, Object>()));
	}
	
	
	@Test
	public void test1() {
		ServerSettingService sss=  applicationContext.getBean(ServerSettingService.class);
		System.out.println(sss);
		System.out.println(sss.fetchServerPortSettingById(3));
	}
	
}
