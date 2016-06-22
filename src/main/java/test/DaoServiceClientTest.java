package test;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import client.ProductServiceClient;
import entity.Product;

public class DaoServiceClientTest {

	ApplicationContext applicationContext;
	String configLocation = "classpath:/testDaoServiceClient.xml";
	ProductServiceClient productServiceClient = null;
	
	@Before
	public void setUp() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(configLocation);
		productServiceClient = applicationContext.getBean(ProductServiceClient.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindCollectionMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindList() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOneMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOneMapOfStringObjectMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindById() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateT() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateString() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateT() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrUpdateT() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrUpdateMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveOrUpdateString() {
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
	public void testBatchInsertListOfMapOfStringObject() {
	}

	@Test
	public void testBatchInsertCollectionOfT() {
		long start = System.currentTimeMillis();
		Product product = null;
		List<Product> batchToSave = new ArrayList<Product>();
		int batchSize = 2000;
		for (int i=0;i<50000;i++) {
			product =new Product();
			product.setName("pro-1-b-"+i);
			product.setUnitPrice(1314+i);
			batchToSave.add(product);
			if (i !=0 && (i%batchSize)==0) {
				System.out.println(i-31557);
				System.out.println( productServiceClient.batchInsert(batchToSave));
				batchToSave.clear();
			}
		}
		System.out.println(System.currentTimeMillis()-start);
		
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteById() {
		fail("Not yet implemented");
	}

	@Test
	public void testCountsMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testCounts() {
		fail("Not yet implemented");
	}

}
