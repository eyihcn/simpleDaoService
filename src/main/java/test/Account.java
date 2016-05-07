package test;

import entity.BaseEntity;

public class Account extends BaseEntity<Long> {

	private Long id;
	private String accounName;
	private String password;

	public String getAccounName() {
		return accounName;
	}

	public void setAccounName(String accounName) {
		this.accounName = accounName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


}
