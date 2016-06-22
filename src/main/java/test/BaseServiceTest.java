package test;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import utils.Json;
import entity.Product;

public class BaseServiceTest {
	
	RestTemplate restTemplate = null;
	String url = "http://localhost:8080/testdaoservice/";
	MultiValueMap<String, Object> headers = null;

	@Before
	public void setUp() throws Exception {
		restTemplate = new RestTemplate();
		headers = new LinkedMultiValueMap<String, Object>();
		headers.add("Accept", "application/json;charset=utf-8");
		headers.add("Content-Type", "application/json;charset=utf-8");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testSave() {
		long start = System.currentTimeMillis();
		String requestUrl = url+"sale/Product/save";
		String jsonParam = "{}";
		Product product = null;
		HttpEntity httpEntity = null;
		for (int i=21557;i<31557;i++) {
			product =new Product();
			product.setName("pro-1-b-"+i);
			product.setUnitPrice(1314+i);
			jsonParam = Json.toJson(product);
//			System.out.println(jsonParam);
			httpEntity = new HttpEntity(jsonParam, headers);
			String json  = restTemplate.postForObject(requestUrl , httpEntity, String.class, new Object[0]);
			System.out.println(json);
		}
		System.out.println(System.currentTimeMillis()-start);
		
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testBatchUpdateByIds() {
		fail("Not yet implemented");
	}

	@Test
	public void testBatchUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testBatchInsert() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOne() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindList() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteById() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testCounts() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckExists() {
		fail("Not yet implemented");
	}

}
