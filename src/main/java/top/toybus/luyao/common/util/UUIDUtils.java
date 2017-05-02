package top.toybus.luyao.common.util;

import java.util.UUID;

/**
 * 唯一ID生成工具类
 * 
 * @author sunxg
 */
public class UUIDUtils {
    /**
     * 产生32位小写UUID字符串
     */
    public static String randUUID() {
	return UUID.randomUUID().toString().replace("-", "");
    }
}
