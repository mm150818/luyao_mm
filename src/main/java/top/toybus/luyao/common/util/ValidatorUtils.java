package top.toybus.luyao.common.util;

import java.util.regex.Pattern;

/**
 * 校验工具类
 * 
 * @author sunxg
 */
public class ValidatorUtils {
	// 手机号11-16位数字，首数字必须为1
	private static final Pattern PATTERN_MOBILE = Pattern.compile("^1\\d{10,15}$");
	// 密码6-16位字母或数字
	// ^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$
	private static final Pattern PATTERN_PASSWORD = Pattern.compile("^[0-9A-Za-z]{6,16}$");
	// 验证码为4位数字
	private static final Pattern PATTERN_VERIFY_CODE = Pattern.compile("^\\d{4}$");
	// 车次号保留1000内的数字。也就是说从1001开始所有带4的数字不要。E1001(etc的) 普通的就K1002
	private static final Pattern PATTERN_VEHICLE_NO = Pattern.compile("^[EK](?!0000|1000)[0-35-9]{4}$");
	// 昵称，简体中文、英文，数字，下划线，最多20个字符
	private static final Pattern PATTERN_NICKNAME = Pattern.compile("^[\u4E00-\u9FA5A-Za-z0-9_]{1,20}$");
	// 金额，小于100000000(一亿)，必须两位小数，无符号
	private static final Pattern PATTERN_MONEY = Pattern.compile("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$");

	public static void main(String[] args) {
		System.out.println(isNotVehicleNo("E99990"));
		System.out.println(isNotMoney("10.01"));
	}

	/**
	 * 正则校验，不是车次号
	 */
	public static boolean isNotVehicleNo(String rideNo) {
		return !PATTERN_VEHICLE_NO.matcher(rideNo).matches();
	}

	/**
	 * 正则校验，不是手机号
	 * 
	 * @param mobile
	 */
	public static boolean isNotMobile(String mobile) {
		return !PATTERN_MOBILE.matcher(mobile).matches();
	}

	/**
	 * 正则校验，不是密码
	 * 
	 * @param password
	 * @return
	 */
	public static boolean isNotPassword(String password) {
		return !PATTERN_PASSWORD.matcher(password).matches();
	}

	/**
	 * 正则校验，不是校验码
	 * 
	 * @param verifyCode
	 * @return
	 */
	public static boolean isNotVerifyCode(String verifyCode) {
		return !PATTERN_VERIFY_CODE.matcher(verifyCode).matches();
	}

	/**
	 * 正则校验，不是昵称
	 * 
	 * @param nickname
	 * @return
	 */
	public static boolean isNotNickname(String nickname) {
		return !PATTERN_NICKNAME.matcher(nickname).matches();
	}

	/**
	 * 正则校验
	 */
	public static boolean isNotMoney(String money) {
		return !PATTERN_MONEY.matcher(money).matches();
	}

}
