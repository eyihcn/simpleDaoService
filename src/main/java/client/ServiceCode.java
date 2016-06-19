package client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description  指定：1.模块环境变量Token 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Documented
public @interface ServiceCode {

	/**
	 * default for wish platform 
	 * 模块环境变量Token 例如："WISH_DAO_SERVICE"
	 * 
	 * @return
	 */
	String value() default "WISH_DAO_SERVICE";

}