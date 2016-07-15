package dao;

import java.io.Serializable;
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
	
	Map<Integer,Boolean> batchSaveOrUpdate(List<Map<String, Object>> allSaveOrUpdates); 

	Map<Integer,Boolean>  batchInsert(List<Map<String, Object>> batchToSave); 
	
	Map<Integer,Boolean>  insert(List<T> batchToSave);
	
	boolean deleteById(PK id);

	boolean delete(Map<String, Object> request);
	
	boolean checkExists(Map<String, Object> request);
	
	T findOne(Map<String, Object> request);
	
	T findById(PK id);
	
	List<T> findCollection(Map<String, Object> request);

	Long findCollectionCount(Map<String, Object> request);
	
	List<PK> findIds(Map<String, Object> request);

	/**
	 *  根据主键的生产方式和偏移量，产生一个的主键
	 * @param offset 偏移量
	 */
	PK generatePrimaryKeyByOffset(Integer offset) ;

}
