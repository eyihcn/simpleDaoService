/**
 * 
 */
package client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenyi
 * @version May 5, 201611:13:05 PM
 * @description  指定：1.模块环境变量Token 2.模块名称 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Documented
public @interface ModelName {

	/**
	 * 模块名称
	 * 
	 * @return
	 */
	 String value() default "";
}
