package test;

import static org.junit.Assert.fail;

import java.util.HashMap;
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
		fail("Not yet implemented");
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
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateMulti() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
	}

	@Test
	public void testSaveT() {
		fail("Not yet implemented");
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
