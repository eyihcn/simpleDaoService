package test;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import client.ProductServiceClient;
import entity.Product;
@ContextConfiguration(locations = { "classpath:/testDaoServiceClient.xml" })
public class DaoServiceClientTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	ProductServiceClient productServiceClient ;
	
	@Before
	public void setUp() throws Exception {
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
		System.out.println(productServiceClient.findById(80000L));
	}

	@Test
	public void testCheckExistsMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckExistsMapOfStringObjectMapOfStringObject() {
		fail("Not yet implemented");
	}

	// 10000*5 ---- 67067ms * 5 = 335335ms
	@Test
	public void testCreateT() {
		long start = System.currentTimeMillis();
		Product product = null;
		int counts = 1000;
		for (int i=0; i<counts; i++) {
			product =new Product();
			product.setName("pro-"+i);
			product.setUnitPrice(i);
			System.out.println(productServiceClient.create(product));
		}
		System.out.println(" ==================== "+(System.currentTimeMillis()-start));
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
		Map<String, Object> update = new HashMap<String, Object>();
		update.put("id", 80000);
		update.put("unitPrice", 10000);
		System.out.println(productServiceClient.update(update));
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
	public void testBatchUpdateListOfMapOfStringObjectInt() {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 81756);
		map.put("name", "81756");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("id", 81755);
		map.put("name", "81755");
		list.add(map);
		System.out.println(productServiceClient.batchUpdate(list).keySet().iterator().next().getClass());
	}

	@Test
	public void testBatchUpdateListOfMapOfStringObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testBatchInsertListOfMapOfStringObject() {
		fail("Not yet implemented");
	}

	// 10000 * 5 -- 5000 --- 18073ms*5
	// 10000  ---5000 --------17165ms
	// 10000  ---5000 --------17302ms
	// 10000 ----10000 -------18528MS
	// 10000 ----7500 ---------17342MS
	// 20000 ----5000 ---------10425ms
	@Test
	public void testBatchInsertListOfTInt() {
		int insertSize = 200;
		Product product = null;
		List<Product> batchToSave = new ArrayList<Product>(insertSize);
		int batchSize = 50;
//		int batchSize = 5000;
//		int batchSize = 7500;
//		int batchSize = 10000;
		for (int i=0;i<insertSize;i++) {
			product =new Product();
			product.setName("pro-1-b-"+i);
			product.setUnitPrice(i);
			batchToSave.add(product);
		}
		System.out.println(productServiceClient.batchInsert(batchToSave, batchSize));
		
	}
	//[main] INFO client.DaoServiceClient - batchInsert by single thread end :
	//batchToSave.size() = 20000, totalTaskCounts = totalTaskCounts ,costTime : 5716ms
	
	// [main] INFO client.DaoServiceClient - batchInsertByMultiThread end 
	// : batchToSave.size() = 20000, totalTaskCounts = 0, costTime : 17520ms
	//
	@Test
	public void testbatchInsertByMultiThread() {
		
		Product product = null;
		List<Product> batchToSave = new ArrayList<Product>(20000);
		int batchSize = 5000;
//		int batchSize = 7500;
//		int batchSize = 10000;
		for (int i=0;i<20000;i++) {
			product =new Product();
			product.setName("pro-1-b-"+i);
			product.setUnitPrice(i);
			batchToSave.add(product);
		}
//		System.out.println(productServiceClient.batchInsertByMultiThread(batchToSave, batchSize));
		
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
	
	@Test
	public void testBatchSaveOrUpdate() {
		List<Long> keys =  productServiceClient.generatePrimaryKeys(5) ;
		List<Product> batchToSave = new ArrayList<Product>(keys.size());
		Product product = null;
		for (int i=0;i<keys.size();i++) {
			product =new Product();
			product.setId(Long.valueOf(keys.get(i)));
			product.setName("pro-1-b-"+i);
			product.setUnitPrice(i);
			batchToSave.add(product);	
		}
//		System.out.println(productServiceClient.batchSaveOrUpdate(batchToSave));
	}
	
	@Test
	public void testSaveOrUpdate2() {
	
		Product product = null;
		product =new Product();
		product.setId(81806L);
		product.setName("pro-1-b-");
//		product.setUnitPrice(99);
//		System.out.println(productServiceClient.saveOrUpdate(product));
	}
	
	@Test
	public void testUpdate2() {
	
		Product product = null;
		product =new Product();
		product.setId(81806L);
		product.setName("pro-1-bbbb-");
//		product.setUnitPrice(99);
//		System.out.println(productServiceClient.update(product));
		
		Map<String,Object> udpate = new HashMap<String, Object>();
		udpate.put("id", 81806L);
		udpate.put("name", "bbbb");
		System.out.println(productServiceClient.update(udpate));
		
	}
	
	@Test
	public void testGeneratePrimaryKeys() {
		List<Long> keys =  productServiceClient.generatePrimaryKeys(5) ;
		System.out.println(keys.size());
	}
	

	// 50001 -- 5000 -- 94547ms 
	// 50002 -- 10000 -- 91705ms
	// 10000	 -- 5000	-- 18726ms
	// 10000	 -- 5000	-- 17403ms
	// 10000	 -- 10000	-- 17563ms
	// 10000	 -- 7500	-- 17445ms
	@Test
	public void testBatchInsertCollectionOfT() {
		long start = System.currentTimeMillis();
		Product product = null;
//		int batchSize = 5000;
		int batchSize = 7500;
//		int batchSize = 10000;
		List<Product> batchToSave = new ArrayList<Product>(batchSize);
		for (int i=0;i<10000;i++) {
			product =new Product();
			product.setName("pro-1-b-"+i);
			product.setUnitPrice(i);
			batchToSave.add(product);
			if (i !=0 && (i%batchSize)==0) {
				System.out.println(i);
				System.out.println( productServiceClient.batchInsert(batchToSave));
				batchToSave.clear();
			}
		}
		if (batchToSave.size()>0) {
			System.out.println( productServiceClient.batchInsert(batchToSave));
		}
		System.out.println(System.currentTimeMillis()-start);
		
	}
	
	
	@Test
	public void testThreadSafe () {
		//  查询出Id 为 80000的Product
		// 10个线程 每个线程对unitPrice 循环更新100次， 每次自增1；
		for (int i=0; i<10; i++) {
			new Thread(new Runnable() {
				
				public void run() {
					ProductServiceClient  productServiceClient = applicationContext.getBean(ProductServiceClient.class);
					Long id = Long.valueOf(80000);
					Product product =null;
					Map<String,Object> update= new HashMap<String, Object>();
					update.put("id", id);
					for (int index=0; index<100; index++) {
						product = productServiceClient.findById(id);
						update.put("unitPrice", product.getUnitPrice()+1);
						System.out.println(product);
						System.out.println(productServiceClient.update(update));
					}
				}
			}).start();
		}
		
		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFindIds() {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("unitPrice", -1);
		System.out.println(productServiceClient.findIds(query ));
	}
	
//	@Test
//	public void testThreadSafe_1 () {
//		
//		int totalPageSize = Long.valueOf(productServiceClient.counts()).intValue();
//		String [] titles = new String[2];
//		titles[0] = "name";
//		titles[1] = "unitPrice";
//		String exportFileName = "D:"+File.separator+"testProduct.xls";
//		 new MultiThreadExportService<Product, Object>(totalPageSize,titles,
//				 
//			 new DataProvider<Product>() {
//
//			public List<Product> providerOnePageDage(int pageSize, int pageNumber) {
//				return productServiceClient.findList(null, null, ServicePaginationHelper.build(pageSize, pageNumber));
//			}
//		 }
//		 ,new RowDataHandler<Product>() {
//
//			public List<String> handler(Product entity) {
//				List<String> rows = new ArrayList<String>(2);
//				rows.add(Thread.currentThread().getName()+"---"+entity.getName());
//				rows.add(entity.getUnitPrice()+"");
//				return rows;
//			}
//		}).export(exportFileName);
//		
//	}
	
}
