package test;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class BaseServiceTest {

	private final static String url = "http://localhost:8080/testdaoservice/";

	private ApplicationContext applicationContext;
	private MongoTemplate mongoDao;

	@Before
	public void setup() {
//		System.out.println("before");
//		this.applicationContext = new ClassPathXmlApplicationContext("classpath:/testMongoTemplate/mongo.xml");
//		this.mongoDao = this.applicationContext.getBean(MongoTemplate.class);
	}

	@After
	public void teardown() {
		System.out.println("after");

	}

	@Test
	public void test1() {
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, Object> headers = new LinkedMultiValueMap();
		headers.add("Accept", "application/json;charset=utf-8");
		headers.add("Content-Type", "application/json;charset=utf-8");
		HashMap<String ,Object> map =  new HashMap<String, Object>();
		map.put("_id", 1L);
		String requestBody = map.toString();
		System.out.println(requestBody);
		HttpEntity httpEntity = new HttpEntity("{}", headers);

		LinkedHashMap linkedHashMap = restTemplate.postForObject(url + "sale/product/findList", httpEntity, LinkedHashMap.class, new Object[0]);
		System.out.println(linkedHashMap.toString());
	}


}
