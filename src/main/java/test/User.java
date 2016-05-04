package test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}
	
	public Dept getDept() {
		return dept;
	}

	public void setDept(Dept dept) {
		this.dept = dept;
	}
	
	public Set<Integer> getListInt() {
		return listInt;
	}

	public void setListInt(Set<Integer> listInt) {
		this.listInt = listInt;
	}

	public Set<Dept> getDeptSet() {
		return deptSet;
	}

	public void setDeptSet(Set<Dept> deptSet) {
		this.deptSet = deptSet;
	}

	public List<Map<String, String>> getListMap() {
		return listMap;
	}

	public void setListMap(List<Map<String, String>> listMap) {
		this.listMap = listMap;
	}

	private Set<Integer> listInt ;
	
	private Set<Dept> deptSet;
	
	private String name;
	
	private List<Account> accountList;
	
	private Dept dept;
	
	private List<Map<String,String>> listMap;

	@Override
	public String toString() {
		return "User [listInt=" + listInt + ", deptSet=" + deptSet + ", name=" + name + ", accountList=" + accountList + ", dept=" + dept + ", listMap=" + listMap + "]";
	}


}
