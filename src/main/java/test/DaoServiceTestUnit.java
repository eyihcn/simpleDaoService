package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import service.ServiceResponse;
import utils.Json;
import utils.MyBeanUtil;
import client.ProductServiceClient;
import dao.ProductDao;
import entity.Product;

public class DaoServiceTestUnit {

	String address = "";
	ApplicationContext applicationContext;
	String configLocation = "classpath:/testMongoTemplate/mongo.xml";
	ProductDao productDao ;
	RestTemplate restTemplate = null;
	String url = "http://localhost:8080/testdaoservice/";
	MultiValueMap<String, Object> headers = null;
	ProductServiceClient client = null;
	
	@Before
	public void setUp() {
		System.out.println("setup...");
		
		applicationContext = new ClassPathXmlApplicationContext(configLocation);
		productDao = applicationContext.getBean(ProductDao.class);
		
		restTemplate = new RestTemplate();
		headers = new LinkedMultiValueMap<String, Object>();
		headers.add("Accept", "application/json;charset=utf-8");
		headers.add("Content-Type", "application/json;charset=utf-8");
		
		client = applicationContext.getBean(ProductServiceClient.class);
		
		System.out.println("setup over");
	}
	
	
	@After
	public void tearDown() {
		((ClassPathXmlApplicationContext)applicationContext).close();
		System.out.println("tearDown ...");
	}
	
	@Test
	public void testServiceClient_DeleteById() {
		System.out.println(client.deleteEntityById(1L));
	}
	
	@Test
	public void testServiceClient_Save() {
		Product pro = new Product();
		pro.setName("pro-2-c");
		pro.setUnitPrice(134);
		System.out.println(client.updateEntity(pro));
		System.out.println(client.createEntity(pro));
	}
	
	
	@Test
	public void testServiceClient_Update() {
		Product pro = client.findEntityById(7L);
		System.out.println(pro);
		pro.setName("pro-1-d");
		client.updateEntity(pro);
		System.out.println(client.findEntityById(7L));
	}
	
	@Test
	public void testServiceClient_find() {
		System.out.println(client.findEntityById(7L));
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("unitPrice", 12);
		System.out.println(client.findEntityList(query, null, null));
	}
	
	
	
	// ==================BaseService======================
	
	@Test
	public void testCrudService_DeleteById() {
		String requestUrl = url+"sale/Product/deleteById";
		String jsonParam = "6";
		System.out.println(jsonParam);
		HttpEntity httpEntity = new HttpEntity(jsonParam, headers);
		ServiceResponse serviceResponse  = restTemplate.postForObject(requestUrl , httpEntity, ServiceResponse.class, new Object[0]);
		System.out.println(Json.toJson(serviceResponse));
	}
	
	@Test
	public void testCrudService_Update() {
		
		String requestUrl = url+"sale/Product/findById";
		String jsonParam = "2";
		System.out.println(jsonParam);

		HttpEntity httpEntity = new HttpEntity(jsonParam, headers);
		Map<String,Object> properties  = restTemplate.postForObject(requestUrl , httpEntity, Map.class, new Object[0]);
		Map<String,Object> result = (Map<String, Object>) properties.get("result");
		
//		ServiceResponse serviceResponse = MyBeanUtils._mapToEntity(ServiceResponse.class, properties, "entity");
//		Product pr = (Product) serviceResponse.getResult();
		Product pr = MyBeanUtil.mapToEntity(Product.class, result, "entity");
		pr.setName("pro-1-c");
		pr.setUnitPrice(7.7);
		requestUrl = url+"sale/Product/update";
		jsonParam = Json.toJson(pr);
		System.out.println(jsonParam);
		
		httpEntity = new HttpEntity(jsonParam, headers);
		ServiceResponse serResponse  = restTemplate.postForObject(requestUrl , httpEntity, ServiceResponse.class, new Object[0]);
		System.out.println(Json.toJson(serResponse));
	}
	
	@Test
	public void testCrudService_findOne() {
		
		String requestUrl = url+"sale/Product/findOne";
		String jsonParam = "2";
		System.out.println(jsonParam);
		HttpEntity httpEntity = new HttpEntity(jsonParam, headers);
		ServiceResponse serviceResponse  = restTemplate.postForObject(requestUrl , httpEntity, ServiceResponse.class, new Object[0]);
		System.out.println(Json.toJson(serviceResponse.getResult()));
		
	}
	
	@Test
	public void testCrudService_findById() {
		
		String requestUrl = url+"sale/Product/findById";
		String jsonParam = "1";
		System.out.println(jsonParam);
		HttpEntity httpEntity = new HttpEntity(jsonParam, headers);
		ServiceResponse serviceResponse  = restTemplate.postForObject(requestUrl , httpEntity, ServiceResponse.class, new Object[0]);
		System.out.println(Json.toJson(serviceResponse.getResult()));
		
	}
	
	
	@Test
	public void testCrudService_Save() {
		String requestUrl = url+"sale/Product/save";
		String jsonParam = "{}";
		Product product =new Product();
		product.setName("pro-1-b");
		product.setUnitPrice(1314);
		jsonParam = Json.toJson(product);
		
		System.out.println(jsonParam);
		HttpEntity httpEntity = new HttpEntity(jsonParam, headers);
		
		String json  = restTemplate.postForObject(requestUrl , httpEntity, String.class, new Object[0]);
		System.out.println(json);
	}
	
	// ===================BaseMongoDao-Test======================
	@Test
	public void testDelete() {
		Map<String,Object> query = new HashMap<String, Object>();
		query.put("name", "test-ProA");
		try {
			System.out.println(productDao.delete(query));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDeleteById() {
		System.out.println(productDao.deleteById(4L));
	}
	
	@Test
	public void testBaseDao_fetchCollectionCount() {
		System.out.println(productDao.findCollectionCount(new HashMap()));
	}
	
	@Test
	public void testBaseDao_counts() {
		System.out.println(productDao.count());
		System.out.println(productDao.count(Criteria.where("unitPrice").is(null)));
		Map<String,Object> query = new HashMap<String, Object>();
		query.put("unitPrice", 13);
//		query.put("id", 2);
		System.out.println(productDao.count(query));
		
	}
	
	
	@Test
	public void testBaseDao_find() {
		System.out.println(productDao.find(Criteria.where("unitPrice").is(13)));
		System.out.println(productDao.find(Criteria.where("unitPrice").is(null)));
		System.out.println(productDao.find(Criteria.where("name").is(null)));
		Map<String,Object> requestArgs = new HashMap<String, Object>();
		Map<String,Object> query = new HashMap<String, Object>();
		requestArgs.put("query", query);
		query.put("unitPrice", 13);
		query.put("id", 2);
		System.out.println(productDao.findOne(requestArgs));
	}
	
	
	@Test
	public void testBaseDao_findById() {
		System.out.println(productDao.findById(1L));
	}
	
	@Test
	public void testBaseDao_update() {
		Map<String,Object> updateParam = new HashMap<String,Object>();
		updateParam.put("id", 1);
		updateParam.put("unitPrice", 13);
		productDao.update(updateParam);
	}
	
	@Test
	public void testBaseDao_save() {
		Product  p = new Product();
		p.setName("test-ProB");
		p.setUnitPrice(12);
		System.out.println(productDao.saveOrUpdate(p));
	}
	
}
