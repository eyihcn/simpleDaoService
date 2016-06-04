package test;

import java.util.Date;

public class User {
	
	private String name;
	private Date birth;
	
	public User() {
		super();
	}
	public User(String name, Date birth) {
		super();
		this.name = name;
		this.birth = birth;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	
	

//	private Set<Account> lnums;
//	private Map<String, Account> nameToAcc;
//
//	public Set<Account> getLnums() {
//		return lnums;
//	}
//
//	public void setLnums(Set<Account> lnums) {
//		this.lnums = lnums;
//	}
//
//	public Map<String, Account> getNameToAcc() {
//		return nameToAcc;
//	}
//
//	public void setNameToAcc(Map<String, Account> nameToAcc) {
//		this.nameToAcc = nameToAcc;
//	}

}
