package top.toybus.luyao.common.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import top.toybus.luyao.common.properties.FileProperties;

/**
 * 校验工具类
 * 
 * @author sunxg
 */
@Component
public class ValidatorUtils {
    // 手机号11-16位数字，首数字必须为1
    private static final Pattern PATTERN_MOBILE = Pattern.compile("^1\\d{10,15}$");
    // 密码6-16位字母或数字
    // ^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$
    private static final Pattern PATTERN_PASSWORD = Pattern.compile("^.{6,16}$");
    // 验证码为4位数字
    private static final Pattern PATTERN_VERIFY_CODE = Pattern.compile("^\\d{4}$");
    // 行程号保留1000内的数字。也就是说从1001开始所有带4的数字不要。E1001(etc的) 普通的就K1002
    private static final Pattern PATTERN_VEHICLE_NO = Pattern.compile("^[EK](?!0000|1000)[0-35-9]{4}$");
    // 昵称，简体中文、英文，数字，下划线，最多20个字符
    private static final Pattern PATTERN_NICKNAME = Pattern.compile("^[\u4E00-\u9FA5A-Za-z0-9_]{1,20}$");
    // 金额，小于100000000(一亿)，必须两位小数，无符号
    private static final Pattern PATTERN_MONEY = Pattern.compile("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$");
    // 图片路径 /yyyy/MM/dd/md5.(jpg|jpeg|png|gif)
    private static final Pattern PATTERN_IMG_PATH = Pattern
            .compile("^img/\\d{4}/\\d{2}/\\d{2}/[a-zA-Z0-9]{32}\\.(jpg|jpeg|png|gif)$", Pattern.CASE_INSENSITIVE);

    private static FileProperties fileProperties;

    @Autowired
    public void setEnv(FileProperties fileProperties) {
        ValidatorUtils.fileProperties = fileProperties;
    }

    /**
     * 正则校验，不是行程号
     */
    public static boolean isNotVehicleNo(String vehicleNo) {
        return !PATTERN_VEHICLE_NO.matcher(vehicleNo).matches();
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
     * 正则校验，不是金钱
     */
    public static boolean isNotMoney(String money) {
        return !PATTERN_MONEY.matcher(money).matches();
    }

    /**
     * 正则校验，不是图片路径
     */
    public static boolean isNotImgPath(String imgPath) {
        return !PATTERN_IMG_PATH.matcher(imgPath).matches();
    }

    /**
     * 图片存在检测
     */
    public static boolean isNotExistsImg(String imgPath) {
        Path path = Paths.get(fileProperties.getBasePath(), imgPath);
        return Files.notExists(path);
    }

}
