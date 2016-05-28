package utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
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
	public static boolean isSelfDesignOrm(Class<?> clazz, String... ormPackageNames) {
		if (null == ormPackageNames || ormPackageNames.length == 0) {
			return clazz.getPackage().getName().startsWith("com.tomtop.application.orm");
		}
		if (clazz.getPackage().getName().startsWith("com.tomtop.application.orm")) {
			return true;
		}
		return Arrays.asList(ormPackageNames).contains(clazz.getPackage().getName());
	}
}