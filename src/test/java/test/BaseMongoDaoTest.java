package test;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.ProductDao;
import entity.Product;

public class BaseMongoDaoTest {

	
	ApplicationContext applicationContext;
	String configLocation = "classpath:/testDao.xml";
	ProductDao productDao ;
	
	@Before
	public void setUp() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(configLocation);
		productDao = applicationContext.getBean(ProductDao.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCount() {
		fail("Not yet implemented");
	}

	@Test
	public void testCountMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testCountCriteria() {
		fail("Not yet implemented");
	}

	@Test
	public void testGroupCriteriaGroupBy() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindCriteria() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindCriteriaInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindCriteriaIntegerInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAndModify() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAndRemove() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOneCriteria() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOneCriteriaInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOneInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindCollection() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOneMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckExistsCriteria() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckExistsMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindIds() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteT() {
		
	}

	@Test
	public void testDeleteCriteria() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteById() {
		System.out.println(productDao.deleteById(2L));
	}

	@Test
	public void testUpdateMulti() {
		
		List<Long> ids = Arrays.asList(3L);
		Map<String, Object> updates = new HashMap<String, Object>();
		updates.put("unitPrice", 8889D);
		boolean success = productDao.batchUpdateByIds(ids, updates);
		System.out.println(success);
	}

	@Test
	public void testSaveByUpsertMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testBatchSaveByUpsert() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveByUpsertT() {
		Product  p = new Product();
		p.setName("test-ProB");
		p.setUnitPrice(12);
		System.out.println(productDao.saveByUpsert(p));
	}
	
	@Test
	public void testSaveByUpsertT2() {
		Product product = productDao.findById(1L);
		product.setName("dasfd");
		System.out.println(productDao.saveByUpsert(product));
	}


	@Test
	public void testUpdateMapOfStringObject() {
		Map<String, Object> update = new HashMap<String, Object>();
		update.put("id", 1);
		update.put("name", null);
		System.out.println(productDao.update(update));
	}

	@Test
	public void testUpdateT() {
		Product product = productDao.findById(1L);
		System.out.println(product.getName());
		product.setName(null);
		System.out.println(productDao.update(product));
	}

	@Test
	public void testSaveMapOfStringObject() {
		Map<String,Object> updates = new HashMap<String, Object>();
		updates.put("id", 1L);
		updates.put("name", "EYIHCN-6");
		boolean success = productDao.update(updates);
		System.out.println(success);
	}

	@Test
	public void testSaveT() {
		Product p  = new Product();
		p.setName("eyihc");
		p.setUnitPrice(11111111111D);
		boolean save = productDao.save(p);
		System.out.println(save);
	}

	@Test
	public void testBatchInsert() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsert() {
		fail("Not yet implemented");
	}

	@Test
	public void testGeneratePrimaryKeyByOffset() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIdByOffset() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNextId() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRequestRestriction() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetQueryFromQueryParam() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindCollectionCount() {
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
	public void testGroupMapOfStringObject() {
		fail("Not yet implemented");
	}

}
