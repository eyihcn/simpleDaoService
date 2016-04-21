package entity;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract Long getId();

	public abstract void setId(Long id);
}
