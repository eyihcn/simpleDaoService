package test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

public class BaseTest {
	
	@Test
	public void test5() {
		System.out.println(this.getClass().getPackage().getName());
	}

	@Test
	public void test4() {
		Class<?> classType = User.class;
		Field[] fs = classType.getDeclaredFields(); // 得到所有的fields

		for (Field f : fs) {
			Class fieldClazz = f.getType(); // 得到field的class及类型全路径

			if (fieldClazz.isPrimitive())
				continue; // 【1】 //判断是否为基本类型

			if (fieldClazz.getName().startsWith("java.lang"))
				continue; // getName()返回field的类型全路径；

			if (fieldClazz.isAssignableFrom(List.class)) // 【2】
			{
				Type fc = f.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型

				if (fc == null)
					continue;

				if (fc instanceof ParameterizedType) // 【3】如果是泛型参数的类型
				{
					ParameterizedType pt = (ParameterizedType) fc;

					Class genericClazz = (Class) pt.getActualTypeArguments()[0]; // 【4】
																					// 得到泛型里的class类型对象。
					System.out.println(genericClazz.getName());
				}
			}
		}
	}

	@Test
	public void test2() {
		Map<String, Object> user = new HashMap<String, Object>();
		user.put("name", "user-AA");
		Map<String, Object> dept = new HashMap<String, Object>();
		dept.put("deptNo", 1);
		user.put("dept", dept);
		List<Map<String, Object>> accList = new ArrayList<Map<String, Object>>();
		dept.put("listacc", accList);
		Map<String, Object> acc = null;
		acc = new HashMap<String, Object>();
		acc.put("accounName", "acc-AA");
		acc.put("password", "acc-password");
		accList.add(acc);
		acc = new HashMap<String, Object>();
		acc.put("accounName", "acc-BB");
		acc.put("password", "acc-password");
		accList.add(acc);
		user.put("accountList", accList);
		Set<Map<String,Object>> deptSet = new HashSet<Map<String,Object>>();
		deptSet.add(dept);
		user.put("deptSet", deptSet);
		Set<Integer> lll = new HashSet<Integer>();
		lll.add(1);
		lll.add(2);
		user.put("listInt",lll);
		user.put("listMap", accList);
		User user1 = _mapToEntity(User.class, user, "test");
		System.out.println(user1.getDept().getListacc().get(1).getAccounName());
		System.out.println(user1.getAccountList().get(0).getAccounName());
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T _mapToEntity(Class<T> clazz, Map<String, Object> properties, String... ormPackageNames) {
		// 暂时处理Collection的实体集合转换
		T entity = null;
		try{
			entity =  clazz.newInstance();
			Class<?> classType = clazz;
			Field[] fs = classType.getDeclaredFields(); // 得到所有的fields
			for (Field f : fs) {
				String fieldName = f.getName();
				Object fieldValue = properties.get(fieldName);
				if (null == fieldValue) {
					continue;
				}
				Class fieldClazz = f.getType(); // 得到field的class及类型全路径
				// 【1】 //判断是否为基本类型 或者 lang包中的类型
				if (fieldClazz.isPrimitive() || fieldClazz.getName().startsWith("java.lang")) {
					BeanUtils.setProperty(entity, fieldName, properties.get(fieldName));
					continue;
				}
				// 【2】 关键的地方，如果是List类型，得到其Generic的类型
				if (Collection.class.isAssignableFrom(fieldClazz)) {
					Type fc = f.getGenericType(); 
					if (fc == null) {
						continue;
					}
					// 【3】如果是泛型参数的类型
					if (fc instanceof ParameterizedType) {
						ParameterizedType pt = (ParameterizedType) fc;
						// 暂时把多层嵌套的泛型，当作基本类型处理
						Type type = pt.getActualTypeArguments()[0];
						if (type instanceof ParameterizedType) {
							BeanUtils.setProperty(entity, fieldName, properties.get(fieldName));
							continue;
						}
						Collection col = null;
						if (List.class.isAssignableFrom(fieldClazz)){
							col= new ArrayList();
						}else {
							col = new HashSet();
						}
						Class genericClazz = (Class) pt.getActualTypeArguments()[0];
						if (!isSelfDesignOrm(genericClazz,ormPackageNames)) { 
							BeanUtils.setProperty(entity, fieldName, properties.get(fieldName));
							continue;
						}
						// 若List的元素为自定义的orm，应该做递归处理
						Collection<Map<String, Object>> listMap = (Collection<Map<String, Object>>) properties.get(fieldName);
						for (Map entityMap : listMap) {
							col.add(_mapToEntity(genericClazz, entityMap, ormPackageNames));
						}
						BeanUtils.setProperty(entity, fieldName, col);
					}
					continue;
				}
				// 若为自定义的orm // com.tomtop.application.orm
				if (isSelfDesignOrm(fieldClazz,ormPackageNames)) {
					BeanUtils.setProperty(entity, fieldName, _mapToEntity(fieldClazz, (Map<String, Object>) properties.get(fieldName), ormPackageNames));
					continue;
				}
				BeanUtils.setProperty(entity, fieldName, properties.get(fieldName));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}
	
	/**
	 * 先判断包名是否以系统自定义的orm包的前缀，若是直接返回true， 否则在判断是否包含在自己提供的orm包名内
	 * 
	 * @param clazz
	 * @param ormPackageNames
	 *            自定义orm的包名
	 * @return true:是自定义orm false:不是自定义orm
	 */
	private boolean isSelfDesignOrm(Class<?> clazz, String... ormPackageNames) {
		if (null == ormPackageNames || ormPackageNames.length == 0) {
			return clazz.getPackage().getName().startsWith("com.tomtop.application.orm");
		}
		if (clazz.getPackage().getName().startsWith("com.tomtop.application.orm")) {
			return true;
		}
		return Arrays.asList(ormPackageNames).contains(clazz.getPackage().getName());
	}
}
