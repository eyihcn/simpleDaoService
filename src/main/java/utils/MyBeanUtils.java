package utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.springframework.util.Assert;

import test.ComplicatedBean;
import test.User;
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class MyBeanUtils {
	
	
	/**
	 * 字符串过长换行显示
	 * @param str
	 * @param lineLength 多长换一次行
	 * @return
	 */
	public  String addBR(String str,int lineLength) {
//		if (StringUtils.isBlank(comment)) {
//			return "";
//		}
//		int block = 30;
		int len = str.length();
		if (len <= lineLength) {
			return str;
		}
		StringBuilder comm = new StringBuilder();
		int endIndex = 0;
		int fromIndex = 0;
		int count = 0;
		while ( true ) {
			fromIndex = (lineLength*count);
			if (fromIndex > len) {
				break;
			}
			endIndex = fromIndex+lineLength;
			if (endIndex >= len) {
				comm.append(str.substring(fromIndex, len)).append("<br/>");
				break;
			}
			comm.append(str.substring(fromIndex, endIndex)).append("<br/>");
			count++;
		}
		return comm.toString();
	}
	
	@Test
	public void test6() {
		readValue("dao_service_router.properties", "JTOMTOPERP_SERVER_PORT_SETTING_SERVICE_ADDRESS");
	}
	
	//根据key读取value
	 public  String readValue(String filePath,String key) {
	  Properties props = new Properties();
	        try {
	         InputStream in = new BufferedInputStream (this.getClass().getClassLoader().getResourceAsStream(filePath));
	         props.load(in);
	         String value = props.getProperty (key);
	            System.out.println(key+"="+value);
	            return value;
	        } catch (Exception e) {
	         e.printStackTrace();
	         return null;
	        }
	 }
	

	/**
	 * 
	 * @param clazz
	 *            需要映射的实体Class类型
	 * @param data
	 *            实体的数据
	 * @param args
	 * @return
	 */
	public static <T> T _mapToEntity(Class<T> clazz, Map<String, Object> properties, String... ormPackageNames) {
		
		if(MapUtils.isEmpty(properties)) {
			return null;
		}
		T entity = null;
		try{
			entity =  clazz.newInstance();
			Class<?> classType = clazz;
			Field[] fs = classType.getDeclaredFields(); // 得到所有的fields
			for (Field f : fs) {
				f.setAccessible(true);
				String fieldName = f.getName();
				Object fieldValue = properties.get(fieldName);
				if (null == fieldValue) {
					continue;
				}
				Class fieldClazz = f.getType(); // 得到field的class
				// 【1】 //判断是否为基本类型 或者 lang包中的类型 或者 日期类型
				if (fieldClazz.isPrimitive() || fieldClazz.getName().startsWith("java.lang")
						||java.util.Date.class.isAssignableFrom(fieldClazz) 
						|| java.sql.Date.class.isAssignableFrom(fieldClazz)) {
					BeanUtils.setProperty(entity, fieldName, fieldValue);
					continue;
				}
				// 【2】若为自定义的orm // com.tomtop.application.orm
				if (isSelfDesignOrm(fieldClazz,ormPackageNames)) {
					Object obj = _mapToEntity(fieldClazz, (Map<String, Object>) fieldValue, ormPackageNames);
					if (null != obj) {
						BeanUtils.setProperty(entity, fieldName, obj);
					}
					continue;
				}
				// 【3】 如果是Collection类型，得到其Generic的类型
				if (Collection.class.isAssignableFrom(fieldClazz)) {
					Collection col = handleCollection(f.getGenericType(),(Collection) fieldValue,ormPackageNames);
					if (null != col) {
						BeanUtils.setProperty(entity, fieldName, col);
					}
					continue;
				}
				// 【4】 如果是Map类型，得到其Generic的类型
				if(Map.class.isAssignableFrom(fieldClazz)) {
					Map map = handleMap(f.getGenericType(),(Map) fieldValue,ormPackageNames);
					if (null != map) {
						BeanUtils.setProperty(entity, fieldName, map);
					}
					continue;
				}
				// 其他,好自为之~
				BeanUtils.setProperty(entity, fieldName, fieldValue);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}
	
	private static <T> Collection handleCollection(Type fGenericType , Collection colProperties, String... ormPackageNames) throws Exception {
		
		if (colProperties == null) {
			return null;
		}
		if (fGenericType == null) {
			return null;
		}
		Collection col = null;
		if (fGenericType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) fGenericType;
			Type acType = pt.getActualTypeArguments()[0];
			Type rowTpye = pt.getRawType();
			if (rowTpye  instanceof Class) {
				Class rtClass = (Class) rowTpye;
				col = newCollectionByType(rtClass);
				if (acType instanceof ParameterizedType) {
					ParameterizedType acParameterizedType = (ParameterizedType) acType;
					Type acRowTpye = acParameterizedType.getRawType();
					if (acRowTpye instanceof Class) {
						Class	acRowTpyeClass = (Class) acRowTpye;
						// 若List的元素为自定义的orm，应该做递归处理
						if (isSelfDesignOrm(acRowTpyeClass,ormPackageNames)) {
							Collection<Map<String, Object>> listMap = (Collection<Map<String, Object>>) colProperties;
							for (Map entityMap : listMap) {
								col.add(_mapToEntity(acRowTpyeClass, entityMap, ormPackageNames));
							}
						}else if (Collection.class.isAssignableFrom(acRowTpyeClass)) {
							Collection colResult = null;
							for (Collection coll : (Collection<Collection>)colProperties) {
								colResult = handleCollection(acParameterizedType, coll, ormPackageNames);
								if (null != colResult ) {
									col.add(colResult);
								}
							}
						}else if (Map.class.isAssignableFrom(acRowTpyeClass)) {
							Map mapResult = null;
							for (Map map: (Collection<Map>)colProperties) {
								mapResult = handleMap(acParameterizedType, map, ormPackageNames);
								if (null != mapResult) {
									col.add(mapResult);
								}
							}
						} // 其他，不造怎么办老~~
					}
				}else if(acType instanceof Class){// 到达最内层的泛型参数
					Class acTpyeClass = (Class) acType;
					if (!isSelfDesignOrm(acTpyeClass,ormPackageNames)) {
						// 基本类型直接可以用BeanUtils.setProperty,
						// 但json字符串bean转换为Map的过程中,javaBean中List 和 Set 都会用ArrayList封装
						// 所以要自己转换一下，否则报错tpye mismatch，妈蛋~~
						col.addAll(colProperties);
					}else {
						// 若List的元素为自定义的orm，应该做递归处理
						Collection<Map<String, Object>> listMap = (Collection<Map<String, Object>>) colProperties;
						for (Map entityMap : listMap) {
							col.add(_mapToEntity(acTpyeClass, entityMap, ormPackageNames));
						}
					}
				}
				
			}
		}else {
			System.out.println("fGenericType is removed ！！！");
			return colProperties;
		}
		return col;
	}

	private static Collection newCollectionByType(Class rtClass) {
		Collection col = null;
		if (List.class.isAssignableFrom(rtClass)){
			if (ArrayList.class.isAssignableFrom(rtClass)){
				col= new ArrayList();
			}else if(LinkedList.class.isAssignableFrom(rtClass)) {
				col= new LinkedList();
			}else {
				col= new ArrayList();
			}
		}else if(Set.class.isAssignableFrom(rtClass)){
			if (HashSet.class.isAssignableFrom(rtClass)) {
				col = new HashSet();
			}else if (LinkedHashSet.class.isAssignableFrom(rtClass)) {
				col = new LinkedHashSet();
			}else if(TreeSet.class.isAssignableFrom(rtClass)) {
				col = new TreeSet();
			}else {
				col = new HashSet();
			}
		}
		return col;
	}
	
	private static <T> Map handleMap(Type fGenericType ,Map mapProperties, String... ormPackageNames) throws Exception {
		
		
		if (mapProperties == null) {
			return null;
		}
		if (fGenericType == null) {
			return null;
		}
		Map map = null;
		if (fGenericType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) fGenericType;
			Class rowTpyeClass = (Class)pt.getRawType();
			Type acTypeVal  = pt.getActualTypeArguments()[1];
			map = newMapByType(rowTpyeClass);
			// map的key应该不会级联泛型吧！~ ！暂时只考虑
			if ( acTypeVal instanceof ParameterizedType) {
				ParameterizedType acTpyeValPT = (ParameterizedType) acTypeVal;
				Type  acTpyeValRowType = acTpyeValPT.getRawType();
				if (acTpyeValRowType instanceof Class) {
					Class	acRowTpyeClass = (Class) acTpyeValRowType;
					// 若为自定义的orm，应该做递归处理
					if (isSelfDesignOrm(acRowTpyeClass,ormPackageNames)) {
						Map value = null;
						Object valOrm = null;
						for (Object key :mapProperties.keySet()) {
							if (null == key) {
								return null;
							}
							value = (Map) mapProperties.get(key);
							if (null == value) {
								return null;
							}
							valOrm =  _mapToEntity(acRowTpyeClass, value, ormPackageNames);
							if (null != valOrm) {
								map.put(key,valOrm);
							}
						}
					}else if (Collection.class.isAssignableFrom(acRowTpyeClass)) {
						Collection value = null;
						Collection valCol= null;
						for (Object key : mapProperties.keySet()) {
							if (null == key) {
								return null;
							}
							value = (Collection) mapProperties.get(key);
							if (null == value){
								return null;
							}
							valCol =  handleCollection(acTpyeValPT,  value, ormPackageNames);
							if (null != valCol) {
								map.put(key,valCol);
							}
						}
					}else if (Map.class.isAssignableFrom(acRowTpyeClass)) {
						Map value = null;
						Map valMap= null;
						for (Object key:mapProperties.keySet()) {
							if (null == key) {
								return null;
							}
							value = (Map) mapProperties.get(key);
							if (null == value) {
								return null;
							}
							valMap =  handleMap(acTpyeValPT, value, ormPackageNames);
							if (null != valMap) {
								map.put(key,valMap);
							}
						}
					} // 其他，不造怎么办老~~
				}
			}else { // Class 到达最内层的泛型参数
//				Class keyClass = (Class) acTypeKey;
				Class valClass = (Class) acTypeVal;
				// 若Map的value为自定义的orm，应该做递归处理
				if (isSelfDesignOrm(valClass,ormPackageNames)) {
					Map value = null;
					Object valOrm = null;
					for (Object key:mapProperties.keySet()) {
						if (null == key) {
							return null;
						}
						value = (Map) mapProperties.get(key);
						if (null == value) {
							return null;
						}
						valOrm =  _mapToEntity(valClass, value, ormPackageNames);
						if (null != valOrm) {
							map.put(key,valOrm);
						}
					}
				}else if(Collection.class.isAssignableFrom(valClass)) {
					System.out.println(valClass);
				}else if(Map.class.isAssignableFrom(valClass)) {
					System.out.println(valClass);
				}else {
					for (Object key: mapProperties.keySet()) {
						map.put(key, mapProperties.get(key));
					}
				}
			}
		}else {
			System.out.println("fGenericType is removed ！！！");
			return mapProperties;
		}
		return map;
	}

	private static Map newMapByType(Class rowTpyeClass) {
		Map map;
		if (HashMap.class.isAssignableFrom(rowTpyeClass)) {
			map  = new HashMap();
		}else if (LinkedHashMap.class.isAssignableFrom(rowTpyeClass)) {
			map  = new LinkedHashMap();
		}else if (TreeMap.class.isAssignableFrom(rowTpyeClass)){
			map  = new TreeMap();
		}else {
			map  = new HashMap();
		}
		return map;
	}
	
	
	/**
	 * 先判断包名是否以系统自定义的orm包的前缀，若是直接返回true， 否则在判断是否包含在自己提供的orm包名内
	 * 
	 * @param clazz
	 * @param ormPackageNames
	 *            自定义orm的包名
	 * @return true:是自定义orm false:不是自定义orm
	 */
	public static boolean isSelfDesignOrm(Class<?> clazz, String... ormPackageNames) {
		Assert.notNull(clazz);
		if (clazz.getPackage().getName().startsWith("com.tomtop.application.orm")) {
			return true;
		}
		if (null == ormPackageNames || ormPackageNames.length == 0) {
			return false;
		}
		return Arrays.asList(ormPackageNames).contains(clazz.getPackage().getName());
		
	}
	
	@Test
	public void testMapToBean() {
		ComplicatedBean complicateBean =new ComplicatedBean();
		
//		List <Map<String,Integer>> nums = new ArrayList<Map<String,Integer>>();
//		Map<String,Integer> m1 = new HashMap<String, Integer>();
//		m1.put("m1", 1);
//		nums.add(m1);
//		complicateBean.setNums(nums);
//		Set<Long> s1 = new HashSet<Long>();
//		s1.add(1L);
//		complicateBean.setLnums(s1);
//		Map<String,String> map1 =new HashMap<String, String>();
//		map1.put("map1", "map1");
//		complicateBean.setMap1(map1);
//	
//		Map<Integer,String> map2 =new HashMap<Integer, String>();
//		map2.put(2, "map2");
//		complicateBean.setMap2(map2);
//		
//		Map<String,Long> comin = new HashMap<String, Long>();
//		comin.put("comin1", 1L);
//		comin.put("comin2", 1L);
//		comin.put("comin3", 1L);
//		
//		Map<String,Long> comin2 = new HashMap<String, Long>();
//		comin2.put("comin1", 1L);
//		comin2.put("comin2", 1L);
//		comin2.put("comin3", 1L);
//		List<Map<String,Long>> comList = new ArrayList<Map<String,Long>>();
//		comList.add(comin);
//		comList.add(comin2);
//		Map<String, List<Map<String, Long>>> compMap = new HashMap<String, List<Map<String,Long>>>();
//		compMap.put("compMap", comList);
//		complicateBean.setComPmap(compMap);
		Set<User> users = new HashSet<User>();
//		complicateBean.setUsers(users);
		users.add(new User("eyihcn",new Date()));
		users.add(new User("chenyi",new Date()));
		users.add(new User("cnheyi",new Date()));
		Map<String,Object> ben = new HashMap<String, Object>();
//		ben.put("users", users);
		Map<String,User> nameToUser =new HashMap<String, User>();
		nameToUser.put("me", new User("eyihcn",new Date()));
		nameToUser.put("i", new User("cnheyi",new Date()));
		nameToUser.put("my", new User("iyehcn",new Date()));
//		ben.put("nameToUser", nameToUser);
		
		Map<String,Set<User>> goupToUsers = new HashMap<String, Set<User>>();
		goupToUsers.put("G1", users);
		goupToUsers.put("G2", users);
		Map<String, List<Map<String, User>>> goupToUsers2 = new HashMap<String, List<Map<String,User>>>();
		List<Map<String, User>> ll = new ArrayList<Map<String,User>>();
		ll.add(nameToUser);
		goupToUsers2.put("goupToUsers2", ll);
		
//		ben.put("goupToUsers", goupToUsers);
//		ben.put("goupToUsers2", goupToUsers2);
//		String json = Json.toJson(complicateBean);
		List<String> strList = new ArrayList<String>();
		strList.add("eyihcn");
		ben.put("rm", strList);
		
		String json = Json.toJson(ben);
		System.out.println("json =====>"+json);
		Map<String,Object> properties = Json.fromJson(json, Map.class);
//		System.out.println("properties===>"+properties);
//		ComplicateBean bean = MyBeanUtils._mapToEntity(ComplicateBean.class, properties, "test");
		ComplicatedBean bean = MyBeanUtils._mapToEntity(ComplicatedBean.class, properties, "test");
//		System.out.println(bean.getLnums());
//		System.out.println(bean.getMap1());
//		System.out.println(bean.getMap2());
//		System.out.println(bean.getComPmap());
		System.out.println(bean.getUsers());
		System.out.println(bean.getNameToUser());
		System.out.println(bean.getGoupToUsers());
		System.out.println(bean.getGoupToUsers2());
		System.out.println(bean.getRm());
		
	}
}
