package dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import entity.BaseEntity;

public interface CommonDaoInter<T extends BaseEntity<PK>, PK extends Serializable> {
	
	/**
	 * 忽略id字段，插入行的记录
	 * @param properties 
	 * 				Map的key为实体的字段名，value为对应的字段值
	 * @return true；插入成功，false:失败
	 */
	boolean save(Map<String, Object> properties);
	
	/**
	 * 忽略id字段，插入行的记录
	 * @param entity 实体对象
	 * @return true:成功，false:失败
	 */
	boolean save(T entity);

	/**
	 * 传入id 和 需要更新的字段，执行更新操作
	 * @param properties 
	 * 				必须包含id 和 被更新的字段（key为字段名，value为对应的值）
	 * @return true:成功，false:失败
	 */
	boolean update(Map<String, Object> request);
	
	/**
	 * 根据传入的对象执行更新操作
	 * @param entity
	 * @return true:成功，false:失败
	 */
	boolean update(T entity);

	/**
	  * 若properties有id且id在数据库对应的有记录，根据id则会覆盖之前的记录<br/>
	  * 若properties没有id 或者 id没有对应的记录，则会插入新列(没有Id会生成Id，有则直接插入)
	  * @param properties key为字段名，value为对应的值
	  * @return true:成功，false:失败
	  */
	boolean saveByUpsert(Map<String, Object> request);
	
	/**
	  * 若properties有id且id在数据库对应的有记录，根据id则会覆盖之前的记录<br/>
	  * 若properties没有id 或者 id在数据库中没有对应的记录，则会插入新列(没有Id会生成Id，有则直接插入)
	  * @param properties key为字段名，value为对应的值
	  * @return true:成功，false:失败
	  */
	boolean saveByUpsert(T entity);
	
	/**
	 * 批量更新，对Ids集合中的id在数据库中对应记录，执行相同更新操作
	 * @param ids 需要更新的实体Id集合
	 * @param updates 需要更新的属性(key为字段名，value为对应的值)
	 * @return true:成功，false:失败
	 */
	boolean batchUpdateByIds(List<PK> ids, Map<String, Object> updates);
	
	/**
	 * 批量更新,每个单独的更新操作和 单独的update(Map<String, Object> properties)方法相同
	 * @param allUpdates 所有的update的集合
	 * @return 返回Map的key为id,value为更新结果(true:成功，false:失败)
	 */
	Map<Integer,Boolean> batchUpdate(List<Map<String, Object>> allUpdates); 

	/**
	 * 批量的SaveByUpsert操作。单独的保存逻辑和saveByUpsert(Map<String, Object> properties)相同
	 * @param allSaveByUpserts
	 * @return 返回的Map的key为List集合的序列号，value为对应的结果(true:成功，false:失败)
	 */
	Map<Integer,Boolean> batchSaveByUpsert(List<Map<String, Object>> allSaveByUpserts); 
	
	/**
	 * 批量插入(若实体id存在，忽略实体Id，自增生成新Id)
	 * @param batchToSave 需要插入保存的实体的Map集合
	 * @return 返回的Map的key为插入集合的序列号，value为插入结果(true:成功，false:失败)
	 */
	Map<Integer,Boolean> batchInsert(List<Map<String, Object>> batchToSave); 
	
	/**
	 * 批量插入(若实体id存在，忽略实体Id，自增生成新Id)
	 * @param batchToSave 需要插入保存的实体集合
	 * @return 返回的Map的key为插入集合的序列号，value为插入结果(true:成功，false:失败)
	 */
	Map<Integer,Boolean> insert(List<T> batchToSave);
	
	boolean deleteById(PK id);

	boolean delete(Map<String, Object> request);
	
	boolean checkExists(Map<String, Object> request);
	
	T findOne(Map<String, Object> request);
	
	T findById(PK id);
	
	List<T> findCollection(Map<String, Object> request);

	Long findCollectionCount(Map<String, Object> request);
	
	long count(Map<String, Object> query);
	
	/**
	 * 根据查询条件，返回查询到的所有实体的id集合
	 * @param request 查询条件
	 * @return 所有实体的id集合
	 */
	List<PK> findIds(Map<String, Object> request);
	
	/**
	 *  根据主键的生产方式和偏移量，产生一个的主键
	 * @param offset 偏移量
	 */
	PK generatePrimaryKeyByOffset(int offset) ;

}
