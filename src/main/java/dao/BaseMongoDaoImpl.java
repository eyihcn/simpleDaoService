package dao;

import java.io.Serializable;

/**
 * @author chenyi
 *
 * @param <T>
 * @param <Id>
 */
public abstract class BaseMongoDaoImpl<T, Id extends Serializable> implements BaseDao<T> {

	private Id id;
	private Class<T> clazz;
	private String entityClassName;


}
