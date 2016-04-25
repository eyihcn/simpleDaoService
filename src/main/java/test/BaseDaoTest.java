package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.ProductDao;
import entity.Product;

public class BaseDaoTest {

	private final static String url = "http://localhost:8080/testdaoservice/";

	private ApplicationContext applicationContext;
	private ProductDao productDao = null;

	@Before
	public void setup() {
		System.out.println("before");
		this.applicationContext = new ClassPathXmlApplicationContext("classpath:/testMongoTemplate/mongo.xml");
		this.productDao = this.applicationContext.getBean(ProductDao.class);
	}

	@After
	public void teardown() {
		System.out.println("after");

	}

	@Test
	public void testFind() {
		System.out.println(productDao.findById(1l));
	}

	@Test
	public void testSave() {
		Product product = new Product();
		product.setIsbn("aaa");
		product.setName("pro-aaa");
		product.setPrice(2.22);
		product.setNum(222);
		System.out.println(productDao.saveOrUpdate(product));
	}

}
