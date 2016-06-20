package dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import entity.BaseEntity;

public interface CommonDaoInter<T extends BaseEntity<PK>, PK extends Serializable> {

	boolean save(Map<String, Object> request);
	
	boolean save(T entity);

	boolean update(Map<String, Object> request);

	boolean update(T entity);

	boolean saveOrUpdate(Map<String, Object> request);
	
	boolean saveOrUpdate(T entity);
	
	boolean batchUpdateByIds(List<Integer> ids, Map<String, Object> updates);
	
	Map<Integer,Boolean> batchUpdate(List<Map<String, Object>> allUpdates); 

	boolean batchInsert(List<Map<String, Object>> batchToSave); 
	
	boolean insert(Collection<T> batchToSave);
	
	boolean deleteById(PK id);

	boolean delete(Map<String, Object> request);
	
	boolean checkExists(Map<String, Object> request);
	
	T findOne(Map<String, Object> request);
	
	T findById(PK id);
	
	List<T> findCollection(Map<String, Object> request);

	Long findCollectionCount(Map<String, Object> request);

}
