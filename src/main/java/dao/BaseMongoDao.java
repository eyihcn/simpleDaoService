package dao;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
import utils.Json;
import utils.MyBeanUtil;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import entity.BaseEntity;

/**
 * @author tomtop2016
 * @param <T>实体类型
 * @param <PK>主键类型
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseMongoDao<T extends BaseEntity<PK>, PK extends Serializable> implements CommonDaoInter<T, PK> {

	@Autowired
	private MongoTemplate mongoTemplate;
	private Class<T> entityClass; // 实体的运行是类
	private Class<PK> pkClass; // 实体的运行是类
	private String collectionName;// 创建的数据表的名称是类名的首字母小写

	public BaseMongoDao() {
		this.entityClass = MyBeanUtil.getSuperClassGenericType(this.getClass());
		this.pkClass = MyBeanUtil.getSuperClassGenericType(this.getClass(), 1);
		this.collectionName = _getCollectionName();
	}

	public BaseMongoDao(Class<T> entityClass, Class<PK> pkClass) {
		this.entityClass = entityClass;
		this.pkClass = pkClass;
		collectionName = _getCollectionName();
	}

	public BaseMongoDao(Class<T> entityClass, Class<PK> pkClass, String collectionName) {
		this.entityClass = entityClass;
		this.pkClass = pkClass;
		this.collectionName = collectionName;
	}

	public long count() {
		return count(new HashMap<String, Object>());
	}

	public long count(Map<String, Object> query) {
		return count(getRequestRestriction(query));
	}

	public long count(Criteria criteria) {
		return mongoTemplate.count(new Query(criteria), collectionName);
	}

	public Object group(Criteria criteria, GroupBy groupBy) {
		if (null == criteria) {
			return mongoTemplate.group(collectionName, groupBy, entityClass);
		}
		return mongoTemplate.group(criteria, collectionName, groupBy, entityClass);
	}

	public List<T> find(Criteria criteria) {
		Query query = new Query(criteria);
		return mongoTemplate.find(query, entityClass, collectionName);
	}

	public List<T> find(Criteria criteria, Integer pageSize) {
		Query query = new Query(criteria).limit(pageSize.intValue());
		return mongoTemplate.find(query, entityClass, collectionName);
	}

	public List<T> find(Criteria criteria, Integer pageSize, Integer pageNumber) {
		Query query = new Query(criteria).skip((pageNumber.intValue() - 1) * pageSize.intValue()).limit(pageSize.intValue());
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

	public T findOne(Criteria criteria) {
		return mongoTemplate.findOne(new Query(criteria), entityClass, collectionName);
	}

	public T findOne(Criteria criteria, Integer skip) {
		Query query = new Query(criteria).skip(skip.intValue());
		return mongoTemplate.findOne(query, entityClass, collectionName);
	}

	public T findOne(Integer skip) {
		Query query = new Query().skip(skip.intValue());
		return mongoTemplate.findOne(query, entityClass, collectionName);
	}

	public List<T> findCollection(Map<String, Object> requestArgs) {
		return mongoTemplate.find(getQueryFromQueryParam(requestArgs), entityClass, collectionName);
	}

	public T findOne(Map<String, Object> requestArgs) {
		return mongoTemplate.findOne(getQueryFromQueryParam(requestArgs), entityClass, collectionName);
	}

	public boolean checkExists(Criteria criteria) {
		Query query = new Query(criteria).limit(1);
		return null != mongoTemplate.findOne(query, entityClass, collectionName);
	}

	public boolean checkExists(Map<String, Object> requestArgs) {
		return mongoTemplate.exists(getQueryFromQueryParam(requestArgs), collectionName);
	}

	public Boolean delete(T object) {
		try {
			if (object.getId() == null) {
				return false;
			}
			delete(Criteria.where("id").is(object.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}

	public boolean delete(Criteria criteria) {
		try {
			WriteResult writeResult = mongoTemplate.remove(new Query(criteria), this.entityClass, collectionName);
			return writeResult.getN() > 0 ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(Map<String, Object> requestArgs) {
		return delete(getRequestRestriction((Map<String, Object>) requestArgs.get("query")));
	}

	public boolean deleteById(PK id) {
		T object = findById(id);
		if (null == object) {
			return Boolean.valueOf(false);
		}
		return delete(object);
	}

	public WriteResult updateMulti(Criteria criteria, Update update) {
		return mongoTemplate.updateMulti(new Query(criteria), update, collectionName);
	}

	public boolean saveByUpsert(Map<String, Object> properties) {
		try {
			T entity = MyBeanUtil.mapToEntity(entityClass, properties);
			saveByUpsert(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(true);
	}

	public boolean saveByUpsert(T entity) {
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

	public Map<Integer, Boolean> batchSaveByUpsert(List<Map<String, Object>> allSaveOrUpdates) {
		if (CollectionUtils.isEmpty(allSaveOrUpdates)) {
			return Collections.EMPTY_MAP;
		}
		Map<Integer, Boolean> result = new HashMap<Integer, Boolean>();
		for (int i = 1, len = allSaveOrUpdates.size(); i <= len; i++) {
			try {
				result.put(Integer.valueOf(i), Boolean.valueOf(saveByUpsert(allSaveOrUpdates.get(i - 1))));
			} catch (Exception e) {
				e.printStackTrace();
				result.put(Integer.valueOf(i), Boolean.valueOf(false));
			}
		}
		return result;
	}

	/**
	 * 若id为空，更新失败<br/>
	 * updateParam参数包含id 和 需要更新的字段<br/>
	 * 更新的字段参数可以直接放在updateParam中，也可以另为封装一个Map(key名为updates，value 为封装的map)
	 */
	public boolean update(Map<String, Object> updateParam) {
		Assert.notEmpty(updateParam, "updateParam can not be empty");
		Object id = updateParam.get("id");
		if (null == id) {
			return false;
		}
		try {
			Update update = new Update();
			Map<String, Object> updates = (Map<String, Object>) updateParam.get("updates");
			if (null != updates) {
				updates.remove("id");
				updates.remove("class");
				for (String key : updates.keySet()) {
					update.set(key, updates.get(key));
				}
			} else {
				updateParam.remove("id");
				updateParam.remove("class");
				for (String key : updateParam.keySet()) {
					update.set(key, updateParam.get(key));
				}
			}
			findAndModify(Criteria.where("id").is(id), update);
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	public boolean save(Map<String, Object> properties) {
		try {
			properties.remove("id");
			properties.remove("_id");
			T entity = MyBeanUtil.mapToEntity(entityClass, properties);
			saveByUpsert(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean save(T entity) {
		entity.setId(null);
		return saveByUpsert(entity);
	}

	public boolean update(T entity) {
		return update((Map) Json.fromJson(Json.toJson(entity), Map.class));
	}

	public Map<Integer, Boolean> batchInsert(List<Map<String, Object>> batchToSave) {
		if (CollectionUtils.isEmpty(batchToSave)) {
			return Collections.EMPTY_MAP;
		}
		return insert(MyBeanUtil.mapToEntity(entityClass, batchToSave));
	}

	public Map<Integer, Boolean> insert(List<T> batchToSave) {
		if (CollectionUtils.isEmpty(batchToSave)) {
			return Collections.EMPTY_MAP;
		}
		Map<Integer, Boolean> result = new HashMap<Integer, Boolean>();
		for (int i = 1, len = batchToSave.size(); i <= len; i++) {
			try {
				result.put(Integer.valueOf(i), Boolean.valueOf(save(batchToSave.get(i - 1))));
			} catch (Exception e) {
				e.printStackTrace();
				result.put(Integer.valueOf(i), Boolean.valueOf(false));
			}
		}
		return result;
	}

	public PK generatePrimaryKeyByOffset(int offset) {
		if (offset < 1) {
			throw new IllegalArgumentException();
		}
		try {
			return pkClass.getConstructor(String.class).newInstance(getIdByOffset(offset));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String getIdByOffset(int offset) {
		return _getIdByOffset(getCollectionName(), offset);
	}

	private String _getIdByOffset(String seq_name, int offset) {
		if (offset < 1) {
			throw new IllegalArgumentException();
		}
		String sequence_collection = "seq";
		String sequence_field = "seq";

		DBCollection seq = mongoTemplate.getCollection(sequence_collection);

		DBObject query = new BasicDBObject();
		query.put("_id", seq_name);

		DBObject change = new BasicDBObject(sequence_field, Integer.valueOf(offset));
		DBObject update = new BasicDBObject("$inc", change);

		DBObject res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
		return res.get(sequence_field).toString();
	}

	public String getNextId() {
		return _getIdByOffset(getCollectionName(), 1);
	}

	// public String getNextId(String seq_name) {
	// String sequence_collection = "seq";
	// String sequence_field = "seq";
	//
	// DBCollection seq = mongoTemplate.getCollection(sequence_collection);
	//
	// DBObject query = new BasicDBObject();
	// query.put("_id", seq_name);
	//
	// DBObject change = new BasicDBObject(sequence_field, Integer.valueOf(1));
	// DBObject update = new BasicDBObject("$inc", change);
	//
	// DBObject res = seq.findAndModify(query, new BasicDBObject(), new
	// BasicDBObject(), false, update, true, true);
	// return res.get(sequence_field).toString();
	// }

	public void _sort(Query query, String orderAscField, String orderDescField) {
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

	/** 根据类名获取集合的名称 :将类名首字母小写，转换为mongodb的集合名称 */
	private String _getCollectionName() {
		return StringUtils.uncapitalize(entityClass.getSimpleName());
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
				} else if ("$elemMatch".equals(compare)) {
					Criteria childCriteria = getRequestRestriction((Map<String, Object>) _compareValue);
					criterias.add(Criteria.where(key).elemMatch(childCriteria));
				}
			}
		} else {
			criterias.add(Criteria.where(key).is(value));
		}

		return criterias;
	}

	/** 将Map查询参数转换为Criteria */
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

	/** 获取查询、排序、分页的条件 */
	public Query getQueryFromQueryParam(Map<String, Object> queryParam) {
		// 无参 ，默认是{}
		Criteria criteria = getRequestRestriction((Map) queryParam.get("query"));
		// 无参 ，默认是id
		String sortField = CommonDaoHelper.getRequestSortField(queryParam);
		// 无参 ，默认是-1
		String sortDirection = CommonDaoHelper.getRequestSortDirection(queryParam);
		// 无参 ，默认是10
		Integer pageSize = CommonDaoHelper.getRequestPageSize(queryParam);
		// 无参 ，默认是1
		Integer pageNumber = CommonDaoHelper.getRequestPageNumber(queryParam);
		Query query = new Query(criteria).skip((pageNumber.intValue() - 1) * pageSize.intValue()).limit(pageSize.intValue());
		if ("-1".equals(sortDirection)) {
			_sort(query, null, sortField);
		} else {
			_sort(query, sortField, null);
		}
		return query;
	}

	public Long findCollectionCount(Map<String, Object> requestArgs) {
		Criteria criteria = getRequestRestriction((HashMap<String, Object>) requestArgs.get("query"));
		return Long.valueOf(count(criteria));
	}

	/** 根据ID批量更新 */
	public boolean batchUpdateByIds(List<Integer> ids, Map<String, Object> updates) {
		try {
			if (CollectionUtils.isEmpty(ids) || MapUtils.isEmpty(updates)) {
				return false;
			}
			Update update = new Update();
			updates.remove("id");
			updates.remove("ids");
			updates.remove("class");
			for (Object key : updates.keySet()) {
				update.set(key.toString(), updates.get(key));
			}
			updateMulti(Criteria.where("id").in((List) ids), update);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Map<Integer, Boolean> batchUpdate(List<Map<String, Object>> allUpdates) {

		if (CollectionUtils.isEmpty(allUpdates)) {
			return MapUtils.EMPTY_MAP;
		}
		Map<Integer, Boolean> result = new HashMap();
		Integer id = null;
		for (Map<String, Object> perUpdates : allUpdates) {
			id = Integer.valueOf(perUpdates.get("id").toString());
			result.put(id, Boolean.valueOf(update(perUpdates)));
		}
		return result;
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

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<PK> getPkClass() {
		return pkClass;
	}

	public void setPkClass(Class<PK> pkClass) {
		this.pkClass = pkClass;
	}

}
