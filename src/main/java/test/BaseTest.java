package test;

import java.lang.reflect.Method;

import org.junit.Test;

import eyihcn.utils.GenericsUtils;

public class BaseTest {

	
	@Test
	public void test1() {
		BaseTest t = getEntity();
	}
	
	public <T> T getEntity() {
		Method method = null;
		try {
			method = this.getClass().getDeclaredMethod("getEntity", null);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Class type = GenericsUtils.getMethodGenericReturnType(method);
		System.out.println(type.getName());
		return null;
		
	}
}
