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

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import eyihcn.utils.CommonDaoHelper;
import eyihcn.utils.GenericsUtils;

/**
 * @author chenyi
 *
 * @param <T>
 * @param <PK>
 */
@SuppressWarnings("unchecked")
public abstract class BaseMongoDaoImpl<T, PK extends Serializable> implements BaseDao<T> {

	@Autowired(required = false)
	private MongoTemplate mongoTemplate;
	private Class<T> entityClass; // 实体的运行是类
	private String collectionName;// MongoTemplate 创建的数据表的名称是类名的首字母小写
	private String orderAscField;
	private String orderDescField;

	public BaseMongoDaoImpl() {
		this.entityClass = GenericsUtils.getSuperClassGenericType(this.getClass());
		this.collectionName = _getCollectionName();
	}

	public BaseMongoDaoImpl(Class<T> entityClass) {
		this.entityClass = entityClass;

		collectionName = _getCollectionName();
	}

	public BaseMongoDaoImpl(Class<T> entityClass, String collectionName) {
		this.entityClass = entityClass;

		this.collectionName = collectionName;
	}

	public long count() {
		return mongoTemplate.count(new Query(), collectionName);
	}

	public long count(Criteria criteria) {
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

	// public List<T> findIds(Criteria criteria) {
	// return mongoTemplate.find(new Query(criteria), Id.class, collectionName);
	// }

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
			mongoTemplate.remove(object);
		} catch (Exception e) {
			e.printStackTrace();

			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public Boolean remove(Criteria criteria) {
		try {
			mongoTemplate.remove(new Query(criteria), collectionName);
		} catch (Exception e) {
			e.printStackTrace();

			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public WriteResult updateMulti(Criteria criteria, Update update) {
		return mongoTemplate.updateMulti(new Query(criteria), update, collectionName);
	}

	public Boolean saveOrUpdate(Object object) {
		try {
			mongoTemplate.save(object, collectionName);
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public Boolean insert(Collection batchToSave) {
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
		Criteria criteria = getRequestRestriction((HashMap<String, Object>) requestArgs.get("query"));

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
			Map<String, Object> updates;
			e.printStackTrace();

			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public Boolean update(Map<String, Object> requestArgs) {
		Object id = requestArgs.get("id");
		if (null == id) {
			return Boolean.valueOf(false);
		}
		try {
			Update update = new Update();
			Map<String, Object> updates = (Map) requestArgs.get("updates");
			updates.remove("id");
			updates.remove("class");
			for (String key : updates.keySet()) {
				update.set(key, updates.get(key));
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
			if (null == requestArgs.get("id")) {
				requestArgs.put("id", getNextId());
			}
			BeanUtils.populate(object, requestArgs);
			saveOrUpdate(object);
		} catch (Exception e) {
			e.printStackTrace();

			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public void delete(T object) {
		remove(object);
	}

	public Boolean deleteById(PK id) {
		T object = load(id);
		if (null == object) {
			return Boolean.valueOf(false);
		}

		return remove(object);
	}

	public BasicDBList group(Map<String, Object> requestArgs) throws Exception {
		Criteria criteria = getRequestRestriction((HashMap) requestArgs.get("query"));
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

}
