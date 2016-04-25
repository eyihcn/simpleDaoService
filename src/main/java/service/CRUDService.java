package service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dao.BaseMongoDaoImpl;

/**
 * 基础的mongodao实现CRUD; 这层算是Action和dao层的解耦
 * 
 * @author chenyi
 *
 * @param <T>
 *            实体类型
 * @param <PK>
 *            实体主键类型
 */
@Repository
public abstract class CRUDService<T, PK extends Serializable> extends BaseService<T, PK> {

	@Autowired
	private BaseMongoDaoImpl<T, PK> commonDao;

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

}
