package top.toybus.luyao.common.properties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("api.file")
@Data
public class FileProperties {
    /** 文件访问基路径 */
    public static String BASE_URL = "";

    // 文件存放路径
    private String basePath;

    // 文件访问路径
    private String baseUrl;

    // 图片路径
    private String imgPath;

    // 图片内容类型
    private String[] imgContentTypes;

    @PostConstruct
    private void init() {
        BASE_URL = this.baseUrl;
    }

    public String getImgPath() {
        return DateTimeFormatter.ofPattern(imgPath).format(LocalDate.now());
    }

}
