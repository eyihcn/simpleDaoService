package service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import dao.MongoDao;

@SuppressWarnings("unchecked")
public abstract class CRUDService<T> extends BaseService<T> {

	@Autowired
	private MongoDao<T> commonDao;

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
	public boolean daoDeleteById(Integer id) {
		return commonDao.deleteById(id);
	}

}
