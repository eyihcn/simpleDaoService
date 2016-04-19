package test;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TestCRUDService {

	private final static String url = "http://localhost:8080/testdaoservice/";

	@Test
	public void test1() {
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, Object> headers = new LinkedMultiValueMap();
		headers.add("Accept", "application/json;charset=utf-8");
		headers.add("Content-Type", "application/json;charset=utf-8");
		String requestBody = "{}";
		HttpEntity httpEntity = new HttpEntity(requestBody, headers);

		restTemplate.postForObject(url + "sale/product/findAll", httpEntity, LinkedHashMap.class, new Object[0]);
		restTemplate.postForObject(url + "sale/product/list", httpEntity, LinkedHashMap.class, new Object[0]);
		restTemplate.postForObject(url + "sale/product/update", httpEntity, Map.class, new Object[0]);

	}

}
