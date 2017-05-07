package top.toybus.luyao.api.exception;

/**
 * 未登录异常
 * 
 * @author sunxg
 */
@SuppressWarnings("serial")
public class NotLoginException extends Exception {

	public NotLoginException() {
		super();
	}

	public NotLoginException(String message) {
		super(message);
	}

}
