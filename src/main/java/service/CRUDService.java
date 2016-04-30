package service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
public abstract class CRUDService<T extends BaseEntity, PK extends Serializable> extends BaseService<T, PK> {

	private BaseMongoDao<T, PK> commonDao;

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
	public T daoFindOne(Map<String, Object> request) {
		return (T) commonDao.fetchRow(request);
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
	public boolean daoDeleteById(PK id) {
		return commonDao.deleteById(id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Long counts(Map<String, Object> request) {
		Map<String,Object> query = (Map<String, Object>) request.get("query");
		if (query == null) {
			return null;
		}
		return commonDao.count(query);
	}

}
