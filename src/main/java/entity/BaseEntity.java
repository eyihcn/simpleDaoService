package entity;

import java.io.Serializable;

/**
 * 
 * @author chenyi
 * @version Apr 30, 20166:30:09 PM
 * @description 抽象基类，便于操作公共ID
 */
public abstract class BaseEntity<PK extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract PK getId();

	public abstract void setId(PK id);
}
