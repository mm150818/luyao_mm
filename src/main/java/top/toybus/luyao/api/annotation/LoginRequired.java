package top.toybus.luyao.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要登录注解
 * 
 * @author sunxg
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
public @interface LoginRequired {
	/** 登录用户 */
	String LOGIN_USER = "LOGIN_USER";

	/**
	 * 需要登录，默认true
	 */
	boolean value() default true;

}
