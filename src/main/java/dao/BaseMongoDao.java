package dao;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import utils.CommonDaoHelper;
import utils.GenericsUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import entity.BaseEntity;

/**
 * 
 * @author chenyi
 *
 * @param <T>实体类型
 * @param <PK>主键类型
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class BaseMongoDao<T extends BaseEntity<PK>, PK extends Serializable> {

	@Autowired
	private MongoTemplate mongoTemplate;
	private Class<T> entityClass; // 实体的运行是类
	private Class<PK> pkClass; // 实体的运行是类
	private String collectionName;// MongoTemplate 创建的数据表的名称是类名的首字母小写
	private String orderAscField;
	private String orderDescField;

	public BaseMongoDao() {
		this.entityClass = GenericsUtils.getSuperClassGenericType(this.getClass());
		this.pkClass = 	GenericsUtils.getSuperClassGenericType(this.getClass(), 1);
		this.collectionName = _getCollectionName();
	}

	public BaseMongoDao(Class<T> entityClass) {
		this.entityClass = entityClass;
		collectionName = _getCollectionName();
	}

	public BaseMongoDao(Class<T> entityClass, String collectionName) {
		this.entityClass = entityClass;

		this.collectionName = collectionName;
	}

	public long count() {
		return mongoTemplate.count(new Query(), collectionName);
	}

	public long count(Criteria criteria) {
		return mongoTemplate.count(new Query(criteria), collectionName);
	}
	
	public long count(Map<String,Object> query) {
		Criteria criteria = getRequestRestriction(query);
		return mongoTemplate.count(new Query(criteria), collectionName);
	}

	public List<T> find(Criteria criteria) {
		Query query = new Query(criteria);
		_sort(query);

		return mongoTemplate.find(query, entityClass, collectionName);
	}

	public Object group(Criteria criteria, GroupBy groupBy) {
		if (null == criteria) {
			return mongoTemplate.group(collectionName, groupBy, entityClass);
		}

		return mongoTemplate.group(criteria, collectionName, groupBy, entityClass);
	}

	public List<T> find(Criteria criteria, Integer pageSize) {
		Query query = new Query(criteria).limit(pageSize.intValue());
		_sort(query);

		return mongoTemplate.find(query, entityClass, collectionName);
	}

	public List<T> find(Criteria criteria, Integer pageSize, Integer pageNumber) {
		Query query = new Query(criteria).skip((pageNumber.intValue() - 1) * pageSize.intValue()).limit(pageSize.intValue());
		_sort(query);
		return mongoTemplate.find(query, entityClass, collectionName);
	}

	public T findAndModify(Criteria criteria, Update update) {
		return mongoTemplate.findAndModify(new Query(criteria), update, entityClass, collectionName);
	}

	public T findAndRemove(Criteria criteria) {
		return mongoTemplate.findAndRemove(new Query(criteria), entityClass, collectionName);
	}

	public List<T> findAll() {
		return mongoTemplate.findAll(entityClass, collectionName);
	}

	public T findById(PK id) {
		return mongoTemplate.findById(id, entityClass, collectionName);
	}

	public Boolean checkExists(Criteria criteria) {
		Query query = new Query(criteria).limit(1);
		_sort(query);

		return Boolean.valueOf(null != mongoTemplate.findOne(query, entityClass, collectionName));
	}

	public T findOne(Criteria criteria) {
		Query query = new Query(criteria).limit(1);
		_sort(query);

		return mongoTemplate.findOne(query, entityClass, collectionName);
	}

	public T findOne(Criteria criteria, Integer skip) {
		Query query = new Query(criteria).skip(skip.intValue()).limit(1);
		_sort(query);

		return mongoTemplate.findOne(query, entityClass, collectionName);
	}

	public T findOne(Integer skip) {
		Query query = new Query().skip(skip.intValue()).limit(1);
		_sort(query);

		return mongoTemplate.findOne(query, entityClass, collectionName);
	}

	public Boolean remove(T object) {
		try {
			if (object.getId() == null) {
				return false;
			}
			remove(Criteria.where("_id").is(object.getId()));
		} catch (Exception e) {
			e.printStackTrace();

			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public Boolean remove(Criteria criteria) {
		try {
			mongoTemplate.remove(new Query(criteria), this.entityClass, collectionName);
		} catch (Exception e) {
			e.printStackTrace();

			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public WriteResult updateMulti(Criteria criteria, Update update) {
		return mongoTemplate.updateMulti(new Query(criteria), update, collectionName);
	}

	/**
	 * 若entity有id，根据id执行更新操作
	 * 若entity没有id，生成id，执行保存
	 * @param entity
	 * @return
	 */
	public Boolean saveOrUpdate(T entity) {
		try {
			if (null == entity.getId()) {
				entity.setId(pkClass.getConstructor(String.class).newInstance(getNextId()));
			}
			mongoTemplate.save(entity, collectionName);
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public Boolean insert(Collection<T> batchToSave) {
		try {
			mongoTemplate.insert(batchToSave, collectionName);
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public MongoOperations getMongoOperation() {
		return mongoTemplate;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public String getNextId() {
		return getNextId(getCollectionName());
	}

	public String getNextId(String seq_name) {
		String sequence_collection = "seq";
		String sequence_field = "seq";

		DBCollection seq = mongoTemplate.getCollection(sequence_collection);

		DBObject query = new BasicDBObject();
		query.put("_id", seq_name);

		DBObject change = new BasicDBObject(sequence_field, Integer.valueOf(1));
		DBObject update = new BasicDBObject("$inc", change);

		DBObject res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
		return res.get(sequence_field).toString();
	}

	private void _sort(Query query) {
		if (null != orderAscField) {
			String[] fields = orderAscField.split(",");
			for (String field : fields) {
				if ("id".equals(field)) {
					field = "_id";
				}
				query.with(new Sort(Sort.Direction.ASC, new String[] { field }));
			}
		} else if (null != orderDescField) {
			String[] fields = orderDescField.split(",");
			for (String field : fields) {
				if ("id".equals(field)) {
					field = "_id";
				}
				query.with(new Sort(Sort.Direction.DESC, new String[] { field }));
			}
		}
	}

	/**
	 * 根据类名获取集合的名称 :将类名首字母小写，转换为mongodb的集合名称
	 * 
	 * @return
	 */
	private String _getCollectionName() {
		return StringUtils.uncapitalize(entityClass.getSimpleName());
	}

	public String getOrderAscField() {
		return orderAscField;
	}

	public void setOrderAscField(String orderAscField) {
		this.orderAscField = orderAscField;
	}

	public String getOrderDescField() {
		return orderDescField;
	}

	public void setOrderDescField(String orderDescField) {
		this.orderDescField = orderDescField;
	}

	private Criteria _parseRequestRestrictionOr(Map<String, Object> query) {
		Criteria allOrCriteria = new Criteria();
		List<Criteria> criterias = new ArrayList<Criteria>();
		if (null != query) {
			for (String key : query.keySet()) {
				Object value = query.get(key);
				if (StringUtils.startsWith(key, "$and")) {
					criterias.add(getRequestRestriction((Map) value));
				} else {
					criterias.addAll(_parseCriteria(key, value));
				}
			}
		}
		if (!criterias.isEmpty()) {
			allOrCriteria.orOperator((Criteria[]) criterias.toArray(new Criteria[criterias.size()]));
		}

		return allOrCriteria;
	}

	/**
	 * 根据操作符转换为Criteria查询条件
	 * @param key
	 * @param value
	 * @return
	 */
	private List<Criteria> _parseCriteria(String key, Object value) {
		if ("id".equals(key)) {
			key = "_id";
		}
		List<Criteria> criterias = new ArrayList<Criteria>();
		Map<String, Object> compareValue;
		if ((value instanceof Map)) {
			compareValue = (Map) value;
			for (String compare : compareValue.keySet()) {
				Object _compareValue = compareValue.get(compare);
				if ("$ge".equals(compare)) {
					criterias.add(Criteria.where(key).gte(_compareValue));
				} else if ("$le".equals(compare)) {
					criterias.add(Criteria.where(key).lte(_compareValue));
				} else if ("$gt".equals(compare)) {
					criterias.add(Criteria.where(key).gt(_compareValue));
				} else if ("$lt".equals(compare)) {
					criterias.add(Criteria.where(key).lt(_compareValue));
				} else if ("$in".equals(compare)) {
					criterias.add(Criteria.where(key).in((Collection) _compareValue));
				} else if ("$like".equals(compare)) {
					criterias.add(Criteria.where(key).regex(Pattern.compile(Pattern.quote((String) _compareValue), 2)));
				} else if ("$left_like".equals(compare)) {
					criterias.add(Criteria.where(key).regex(Pattern.compile(Pattern.quote((String) _compareValue + "$"), 2)));
				} else if ("$right_like".equals(compare)) {
					criterias.add(Criteria.where(key).regex(Pattern.compile(Pattern.quote("^" + (String) _compareValue), 2)));
				} else if ("$not_like".equals(compare)) {
					criterias.add(Criteria.where(key).not().regex((String) _compareValue));
				} else if ("$left_like".equals(compare)) {
					criterias.add(Criteria.where(key).not().regex(Pattern.compile(Pattern.quote((String) _compareValue + "$"), 2)));
				} else if ("$not_right_like".equals(compare)) {
					criterias.add(Criteria.where(key).not().regex(Pattern.compile(Pattern.quote("^" + (String) _compareValue), 2)));
				} else if ("$ne".equals(compare)) {
					criterias.add(Criteria.where(key).ne(_compareValue));
				} else if ("$null".equals(compare)) {
					criterias.add(Criteria.where(key).is(null));
				} else if ("$not_null".equals(compare)) {
					criterias.add(Criteria.where(key).not().is(null));
				} else if ("$not_in".equals(compare)) {
					criterias.add(Criteria.where(key).not().in((Collection) _compareValue));
				} else if ("$where".equals(compare)) {
					criterias.add(Criteria.where("$where").is(_compareValue));
				}
			}
		} else {
			criterias.add(Criteria.where(key).is(value));
		}

		return criterias;
	}

	/**
	 * 将Map查询参数转换为Criteria
	 * @param query
	 * @return
	 */
	public Criteria getRequestRestriction(Map<String, Object> query) {
		Criteria allCriteria = new Criteria();
		List<Criteria> criterias = new ArrayList<Criteria>();
		if (null != query) {
			for (String key : query.keySet()) {
				if ("$or".equals(key)) {
					Map<String, Object> orValues = (Map) query.get(key);
					criterias.add(_parseRequestRestrictionOr(orValues));
				} else {
					Object value = query.get(key);
					criterias.addAll(_parseCriteria(key, value));
				}
			}
		}
		if (!criterias.isEmpty()) {
			allCriteria.andOperator((Criteria[]) criterias.toArray(new Criteria[criterias.size()]));
		}

		return allCriteria;
	}

	public List<T> fetchCollection(Map<String, Object> requestArgs) {
		Criteria criteria = getRequestRestriction((HashMap) requestArgs.get("query"));
		String sortField = CommonDaoHelper.getRequestSortField(requestArgs);
		String sortDirection = CommonDaoHelper.getRequestSortDirection(requestArgs);
		Integer pageSize = CommonDaoHelper.getRequestPageSize(requestArgs);
		Integer pageNumber = CommonDaoHelper.getRequestPageNumber(requestArgs);

		if ("-1".equals(sortDirection)) {
			setOrderDescField(sortField);
			setOrderAscField(null);
		} else {
			setOrderAscField(sortField);
			setOrderDescField(null);
		}

		return find(criteria, pageSize, pageNumber);
	}

	public Long fetchCollectionCount(Map<String, Object> requestArgs) {
		Criteria criteria = getRequestRestriction((HashMap<String, Object>) requestArgs.get("query"));

		return Long.valueOf(count(criteria));
	}

	public T load(PK id) {
		return findById(id);
	}

	public T fetchRow(Map<String, Object> requestArgs) {
		Criteria criteria = getRequestRestriction((Map<String, Object>) requestArgs.get("query"));

		return findOne(criteria);
	}

	public Boolean batchUpdate(Map<String, Object> requestArgs) {
		try {
			Object ids = requestArgs.get("ids");
			if (null != ids) {
				Update update = new Update();
				Map updates = (Map) requestArgs.get("updates");
				updates.remove("id");
				updates.remove("ids");
				updates.remove("class");
				for (Object key : updates.keySet()) {
					update.set(key.toString(), updates.get(key));
				}
				updateMulti(Criteria.where("_id").in((List) ids), update);
			} else {
				List<Map<String, Object>> allUpdates = (List) requestArgs.get("updates");
				for (Object perUpdates : allUpdates) {
					Object id = ((Map) perUpdates).get("id");
					if (null != id) {
						Update update = new Update();
						((Map) perUpdates).remove("id");
						((Map) perUpdates).remove("class");
						for (Object key : ((Map) perUpdates).keySet()) {
							update.set(key.toString(), ((Map) perUpdates).get(key));
						}
						findAndModify(Criteria.where("id").is(id), update);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	/**
	 * 若id为空，更新失败
	 * updateParam参数包含id 和 需要更新的字段
	 * 更新的字段参数可以直接放在updateParam中，也可以另为封装一个Map(key名为updates，value 为封装的map)
	 * @param updateParam
	 * @return
	 */
	public Boolean update(Map<String, Object> updateParam) {
		Assert.notEmpty(updateParam, "updateParam can not be empty");
		Object id = updateParam.get("id");
		if (null == id) {
			return Boolean.valueOf(false);
		}
		try {
			Update update = new Update();
			Map<String, Object> updates = (Map<String,Object>) updateParam.get("updates");
			if (null != updates) {
				updates.remove("id");
				updates.remove("class");
				for (String key : updates.keySet()) {
					update.set(key, updates.get(key));
				}
			}else {
				updateParam.remove("id");
				updateParam.remove("class");
				for (String key : updateParam.keySet()) {
					update.set(key, updateParam.get(key));
				}
			}
			findAndModify(Criteria.where("id").is(id), update);
		} catch (Exception e) {
			e.printStackTrace();

			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public Boolean save(Map<String, Object> requestArgs) {
		try {
			T object = getEntityClass().newInstance();
			BeanUtils.populate(object, requestArgs);
			saveOrUpdate(object);
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public Boolean deleteById(PK id) {
		T object = load(id);
		if (null == object) {
			return Boolean.valueOf(false);
		}
		return remove(object);
	}

	public BasicDBList group(Map<String, Object> requestArgs) throws Exception {
		Criteria criteria = getRequestRestriction((HashMap<String, Object>) requestArgs.get("query"));
		HashMap<String, String> groupConditions = (HashMap) requestArgs.get("group");
		if (null == groupConditions) {
			return null;
		}
		String groupKey = (String) groupConditions.get("key");
		String groupInitialDocument = (String) groupConditions.get("initialDocument");
		String groupReduceFunction = (String) groupConditions.get("reduceFunction");
		if ((null == groupKey) || (null == groupInitialDocument) || (null == groupReduceFunction)) {
			return null;
		}
		groupInitialDocument = URLDecoder.decode(groupInitialDocument, "UTF-8").replace("TOMTOP___PLUS____REPLACE___TOKEN", "+");
		groupReduceFunction = URLDecoder.decode(groupReduceFunction, "UTF-8").replace("TOMTOP___PLUS____REPLACE___TOKEN", "+");

		GroupBy groupBy = GroupBy.key(groupKey.split(",")).initialDocument(groupInitialDocument).reduceFunction(groupReduceFunction);
		GroupByResults<Object> result = (GroupByResults) group(criteria, groupBy);

		BasicDBList o = (BasicDBList) result.getRawResults().get("retval");

		return o;
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}
