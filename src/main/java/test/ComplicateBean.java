package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Json;
import utils.MyBeanUtils;

public class ComplicateBean {

	public static void main(String[] args) {
		ComplicateBean complicateBean =new ComplicateBean();
		
		List <Map<String,Integer>> nums = new ArrayList<Map<String,Integer>>();
		Map<String,Integer> m1 = new HashMap<String, Integer>();
		m1.put("m1", 1);
		nums.add(m1);
//		complicateBean.setNums(nums);
		Set<Long> s1 = new HashSet<Long>();
		s1.add(1L);
		complicateBean.setLnums(s1);
//		complicateBean.setName("eyihcn");
//		complicateBean.setName("eyihcn");
		String json = Json.toJson(complicateBean);
		System.out.println("json =====>"+json);
		Map<String,Object> properties = Json.fromJson(json, Map.class);
		System.out.println("properties===>"+properties);
		System.out.println("properties===>"+properties.get("lnums").getClass());
		ComplicateBean bean = MyBeanUtils._mapToEntity(ComplicateBean.class, properties, "test");
	}
	
	private List<Map<String,Integer>> nums;
	private Set<Long> lnums;
	private Map<String, String> map1;
	private Map<Integer, String> map2;
	private Map<String, Long> map3;
	private Map<String, List<Map<String, Long>>> comPmap;

	private List<User> users;

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

	public Map<String, Long> getMap3() {
		return map3;
	}

	public void setMap3(Map<String, Long> map3) {
		this.map3 = map3;
	}

	public Map<String, List<Map<String, Long>>> getComPmap() {
		return comPmap;
	}

	public void setComPmap(Map<String, List<Map<String, Long>>> comPmap) {
		this.comPmap = comPmap;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Map<String, Integer>> getNums() {
		return nums;
	}

	public void setNums(List<Map<String, Integer>> nums) {
		this.nums = nums;
	}

}

class User {

	private List<Account> nums;
	private Set<Account> lnums;
	private Map<String, Account> nameToAcc;

	public List<Account> getNums() {
		return nums;
	}

	public void setNums(List<Account> nums) {
		this.nums = nums;
	}

	public Set<Account> getLnums() {
		return lnums;
	}

	public void setLnums(Set<Account> lnums) {
		this.lnums = lnums;
	}

	public Map<String, Account> getNameToAcc() {
		return nameToAcc;
	}

	public void setNameToAcc(Map<String, Account> nameToAcc) {
		this.nameToAcc = nameToAcc;
	}

}

class Account {

	private String[] fingers;
	private Map<String, List<ComplicateBean>> comMap;

	public String[] getFingers() {
		return fingers;
	}

	public void setFingers(String[] fingers) {
		this.fingers = fingers;
	}

	public Map<String, List<ComplicateBean>> getComMap() {
		return comMap;
	}

	public void setComMap(Map<String, List<ComplicateBean>> comMap) {
		this.comMap = comMap;
	}

}