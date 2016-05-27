package service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Criteria;

import dao.BaseMongoDao;
import entity.BaseEntity;

/**
 * 基础的mongodao实现CRUD
 * 
 * @author chenyi
 *
 * @param <T>
 *            实体类型
 * @param <PK>
 *            实体主键类型
 */
public abstract class CRUDService<T extends BaseEntity<PK>, PK extends Serializable> extends BaseService<T, PK> {

	protected BaseMongoDao<T, PK> commonDao;

	public void setCommonDao(BaseMongoDao<T, PK> commonDao) {
		this.commonDao = commonDao;
	}

	@Override
	public boolean daoSave(Map<String, Object> request) {
		return commonDao.save(request);
	}

	@Override
	public boolean daoUpdate(Map<String, Object> request) {
		return commonDao.update(request);
	}
	
	@Override
	public boolean daoSaveOrUpdate(Map<String, Object> request) {
		return commonDao.saveOrUpdate(request);
	}

	@Override
	public T daoFindOne(Map<String, Object> request) {
		return  commonDao.fetchRow(request);
	}
	
	@Override
	public T daoFindById(Long id) {
		if (null == id) {
			return null;
		}
		return commonDao.findOne(Criteria.where("id").is(id));
	}

	@Override
	public List<T> daoFindCollection(Map<String, Object> request) {
		return commonDao.fetchCollection(request);
	}

	@Override
	public Long daoFindCollectionCount(Map<String, Object> request) {
		return commonDao.fetchCollectionCount(request);
	}

	@Override
	public boolean daoDeleteById(Long id) {
		if (id == null) {
			return false;
		}
		Map<String,Object> query = new HashMap<String,Object>();
		query.put("id", id);
		return commonDao.delete(query);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean daoDelete(Map<String, Object> request) {
		Map<String,Object> query = (Map<String, Object>) request.get("query");
		if (query == null) {
			return false;
		}
		return commonDao.delete(query);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Long counts(Map<String, Object> request) {
		Map<String,Object> query = (Map<String, Object>) request.get("query");
		if (query == null) {
			return commonDao.count();
		}
		return commonDao.count(query);
	}

}
