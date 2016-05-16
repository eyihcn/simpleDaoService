package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
		p.setName("test-ProA");
		p.setUnitPrice(12);
		System.out.println(productDao.saveOrUpdate(p));
	}
	
}
