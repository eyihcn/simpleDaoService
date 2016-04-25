package service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import dao.BaseMongoDaoImpl;
import entity.BaseEntity;

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
public abstract class CRUDService<T extends BaseEntity, PK extends Serializable> extends BaseService<T, PK> {

	private BaseMongoDaoImpl<T, PK> commonDao;

	@Autowired()
	@Qualifier("productDao")
	public void setCommonDao(BaseMongoDaoImpl<T, PK> commonDao) {
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

}
