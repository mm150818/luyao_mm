package top.toybus.luyao.common.util;

/**
 * 格式化工具类
 * 
 * @author SXG
 */
public class FormatUtils {
    /**
     * 金钱：分转元，保留两位小数
     */
    public static String moneyCent2Yuan(Long cent) {
        return String.format("%.2f", cent.longValue() / 100.0);
    }
}
