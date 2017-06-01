package top.toybus.luyao.common.util;

import java.time.LocalDateTime;
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

    /**
     * 获得订单号
     */
    public static Long getOrderNo() {
        // yyMMddHHmmss[15]uuid.hashCode[10]
//        String datetime = DateTimeFormatter.ofPattern("yyMMddHHmmss").format(LocalDateTime.now());
//        LocalDateTime.now().getNano();
//        return datetime + Integer.toString(LocalDateTime.now().getNano());
//        Math.abs(UUID.randomUUID().hashCode());
        return Long.valueOf(Math.abs(UUID.randomUUID().hashCode()));
    }

    public static void main(String[] args) {
        String orderNo = UUIDUtils.getOrderNo().toString();
        System.out.println(orderNo);
        System.out.println(UUID.randomUUID().toString().hashCode());
        System.out.println(String.format("%05d", 2444444));

        System.out.println(orderNo.length());
        System.out.println(System.currentTimeMillis());
        System.out.println(System.nanoTime());
        System.out.println(LocalDateTime.now().getNano());
        System.out.println(Long.MAX_VALUE);
    }
}
