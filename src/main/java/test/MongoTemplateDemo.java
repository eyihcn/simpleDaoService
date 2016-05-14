package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import entity.Product;

public class MongoTemplateDemo {

	private final static String url = "http://localhost:8080/testdaoservice/";

	private ApplicationContext applicationContext;
	private MongoTemplate mongoDao;

	@Before
	public void setup() {
		System.out.println("before");
		this.applicationContext = new ClassPathXmlApplicationContext("classpath:/testMongoTemplate/mongo.xml");
		this.mongoDao = this.applicationContext.getBean(MongoTemplate.class);
	}

	@After
	public void teardown() {
		System.out.println("after");

	}

	@Test
	public void testGet() {
		Product p = new Product();
		p.setId(1l);
		p.setName("pro-aa");
		p.setPrice(2);
		this.mongoDao.insert(p);
	}

	@Test
	public void testQuery() {
//		Query query = new Query();
		
		System.out.println(this.getClass().getSimpleName());
//		mongoDao.
	}

	/**
	 * findOne 根据查询条件取第一个
	 */
	@Test
	public void testFind3() {
		Class<Product> clazz = Product.class;
		List<Product> findAll = mongoDao.findAll(Product.class);
		System.out.println(findAll.size());
		//update()命令
		// db.collection.update( criteria, objNew, upsert, multi )
		// criteria : update的查询条件，类似sql update查询内where后面的
		// objNew : update的对象和一些更新的操作符（如$,$inc...）等，也可以理解为sql update查询内set后面的
		// upsert : 这个参数的意思是，如果不存在update的记录，是否插入objNew,true为插入，默认是false，不插入。
		// multi : mongodb默认是false,只更新找到的第一条记录，如果这个参数为true,就把按条件查出来多条记录全部更新。

		// 用法：{ $inc : { field : value } }
		// 意思对一个数字字段field增加value
		// 用法：{ $set : { field : value } }
		// 就是相当于sql的set field = value，全部数据类型都支持$set
		// { $unset : { field : 1} }
		// 顾名思义，就是删除字段了
		// 用法：{ $push : { field : value } }
		// 把value追加到field里面去，field一定要是数组类型才行，如果field不存在，会新增一个数组类型加进去
		// 用法：{ $addToSet : { field : value } }
		// 增加一个值到数组内，而且只有当这个值不在数组内才增加
		// $pop

		// 删除数组内的一个值
		// 删除最后一个值：{ $pop : { field : 1 } }
		// 删除第一个值：{ $pop : { field : -1 } }
		// 注意，只能删除一个值，也就是说只能用1或-1，而不能用2或-2来删除两条。mongodb 1.1及以后的版本才可以用
		// 用法：$pull : { field : value } }
		// 从数组field内删除一个等于value值
		Query query = new Query();
		query.addCriteria(Criteria.where("price").is(20));
		Product findOne = mongoDao.findOne(query, Product.class);
		System.out.println(findOne.getId());
		Update update = new Update().inc("price", 10);
		// mongoDao.findAndModify(query, update, clazz);
		// 返回满足查询条件的第一条记录（更新之前的entity），且更新第一条满足条件的记录
		// Product findAndModify = mongoDao.findAndModify(query, update, clazz);
		FindAndModifyOptions options = FindAndModifyOptions.options();
		options.returnNew(true); // 是否返回更新后的实体
		// options.upsert(true);
		Product findAndModify = mongoDao.findAndModify(query, update, options, clazz);
		System.out.println(findAndModify);
	}

	/**
	 * findOne 根据查询条件取第一个
	 */
	@Test
	public void testFind2() {
		List<Product> findAll = mongoDao.findAll(Product.class);
		System.out.println(findAll.size());
		Query query = new Query();
		query.addCriteria(Criteria.where("price").is(0));
		Product findOne = mongoDao.findOne(query, Product.class);
		System.out.println(findOne.getId());
	}

	/**
	 * 测试查询
	 */
	@Test
	public void testFind() {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(1));
		List<Product> products = mongoDao.find(query, Product.class);
		System.out.println(products.size());
		System.out.println(products.get(0).getName());

	}

	/**
	 * 更新使用save
	 */
	@Test
	public void testSave() {
		Product p = new Product();
		p.setId(4l);
		p.setName("pro-zz");
		p.setPrice(3);
		p.setNum(4);
		// 1. 若将引用字段设置为null，则文档中对应的字段将被删除
		// 2. save 是否可以插入新列,可以
		mongoDao.save(p);
	}

	/**
	 * 插入使用insert
	 */
	@Test
	public void testInsert() {
		
		Product p = new Product();
		p.setId(3l);
		p.setName("pro-ccc");
		//若insert时， 
		// 某些字段不设值，若字段是引用类型null，则文档中不会产生该字段 
		// 某些字段是基本类型，会保存默认值如int保存0, 
		// p.setPrice(2);
		// 若该对象的id已经存在了，再次保存会发生啥呢?啥都不会发生~~ 
		// 当object不存在是执行insert，也就是id是否已经存在了。

		this.mongoDao.insert(p);
		Product p1 = new Product();
		p1.setId(6l);
		p1.setName("pro-bb");
		p1.setPrice(2); List<Product> list = new ArrayList<Product>();
		list.add(p1);
		list.add(p);
		// 插入到mongodb的顺序是按照list集合中元素添加的顺序
		// this.mongoDao.insertAll(list);
		// mongoDao.insert(list, Product.class);
		 
		// 若插入的实体没有Id，会发生神马？
		/*
		 * 保存失败 org.springframework.dao.InvalidDataAccessApiUsageException:
		 * Cannot autogenerate id of type java.lang.Long for entity of type
		 * entity.Product!
		 */
		Product p2 = new Product();
		p2.setIsbn("ssss");
		mongoDao.insert(p2);
	}

}
