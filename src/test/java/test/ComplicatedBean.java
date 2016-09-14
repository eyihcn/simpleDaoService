package test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComplicatedBean {
	
	private List<Map<String,Integer>> nums; // test
	private Set<Long> lnums; // test
	private Map<String, String> map1; // test
	private Map<Integer, String> map2; // test
	private Map<String, List<Map<String, Long>>> comPmap;// test
	private Set<User> users; // test
	private Map<String,User> nameToUser; //test
	// nice   >·< 
	private Map<String,Set<User>> goupToUsers; // great
	private Map<String, List<Map<String, User>>> goupToUsers2;// great
	
	private List rm ; // 泛型擦除
	//通配类型，不考虑
	public List getRm() {
		return rm;
	}

	public void setRm(List rm) {
		this.rm = rm;
	}

	public Map<String, List<Map<String, User>>> getGoupToUsers2() {
		return goupToUsers2;
	}

	public void setGoupToUsers2(Map<String, List<Map<String, User>>> goupToUsers2) {
		this.goupToUsers2 = goupToUsers2;
	}

	public Map<String, Set<User>> getGoupToUsers() {
		return goupToUsers;
	}

	public void setGoupToUsers(Map<String, Set<User>> goupToUsers) {
		this.goupToUsers = goupToUsers;
	}

	public Map<String, User> getNameToUser() {
		return nameToUser;
	}

	public void setNameToUser(Map<String, User> nameToUser) {
		this.nameToUser = nameToUser;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<User> getUsers() {
		return users;
	}

	public Set<Long> getLnums() {
		return lnums;
	}

	public void setLnums(Set<Long> lnums) {
		this.lnums = lnums;
	}

	public Map<String, String> getMap1() {
		return map1;
	}

	public void setMap1(Map<String, String> map1) {
		this.map1 = map1;
	}

	public Map<Integer, String> getMap2() {
		return map2;
	}

	public void setMap2(Map<Integer, String> map2) {
		this.map2 = map2;
	}

	public Map<String, List<Map<String, Long>>> getComPmap() {
		return comPmap;
	}

	public void setComPmap(Map<String, List<Map<String, Long>>> comPmap) {
		this.comPmap = comPmap;
	}

	public List<Map<String, Integer>> getNums() {
		return nums;
	}

	public void setNums(List<Map<String, Integer>> nums) {
		this.nums = nums;
	}

}
