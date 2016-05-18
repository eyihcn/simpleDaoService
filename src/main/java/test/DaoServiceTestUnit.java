package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.query.Criteria;

import dao.ProductDao;
import entity.Product;

public class DaoServiceTestUnit {

	String address = "";
	ApplicationContext applicationContext;
	String configLocation = "classpath:/testMongoTemplate/mongo.xml";
	ProductDao productDao ;
	
	
	@Before
	public void setUp() {
		System.out.println("setup...");
		
		applicationContext = new ClassPathXmlApplicationContext(configLocation);
		productDao = applicationContext.getBean(ProductDao.class);
		
		System.out.println("setup over");
	}
	
	
	@After
	public void tearDown() {
		((ClassPathXmlApplicationContext)applicationContext).close();
		System.out.println("tearDown ...");
	}
	
	
	
	
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
		System.out.println(productDao.fetchRow(requestArgs));
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
//		p.setName("test-ProB");
		p.setUnitPrice(12);
		System.out.println(productDao.saveOrUpdate(p));
	}
	
}
