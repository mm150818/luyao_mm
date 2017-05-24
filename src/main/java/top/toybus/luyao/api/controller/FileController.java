package top.toybus.luyao.api.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j;
import top.toybus.luyao.api.annotation.LoginRequired;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.properties.FileProperties;
import top.toybus.luyao.common.util.UUIDUtils;

/**
 * 文件操作相关
 */
@RestController
@RequestMapping("/api/file")
@Log4j
public class FileController {
    @Autowired
    private FileProperties fileProperties;

    /**
     * 上传图片
     */
    @LoginRequired
    @RequestMapping("/upimg")
    public ResData upimg(@RequestPart("file") MultipartFile multipartFile) {
        ResData resData = ResData.newOne();
        try {
            if (multipartFile.isEmpty()) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("图片不能为空");
            }
            if (!StringUtils.equalsAny(multipartFile.getContentType(), fileProperties.getImgContentTypes())) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("图片类型错误");
            }

            String imgPath = fileProperties.getImgPath();
            Path target = Paths.get(fileProperties.getBasePath(), imgPath);
            if (Files.notExists(target)) {
                Files.createDirectories(target);
            }
            String filename = UUIDUtils.randUUID() + "."
                    + FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase();
            target = target.resolve(filename);
            multipartFile.transferTo(target.toFile());

            resData.put("path", imgPath + filename);
            resData.put("url", fileProperties.getBaseUrl() + imgPath + filename);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resData.setCode(-1).setMsg("文件处理异常"); // err-1
        }
        return resData;
    }

    /**
     * 删除图片
     */
    @LoginRequired
    @RequestMapping("/delimg")
    public ResData delimg(String url) {
        ResData resData = ResData.newOne();
        try {
            if (StringUtils.isBlank(url)) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("图片路径不能为空");
            }
            Path path = Paths.get(fileProperties.getBasePath(), url.replace(fileProperties.getBaseUrl(), ""));
            boolean delOk = Files.deleteIfExists(path);
            if (!delOk) {
                resData.setCode(1).setMsg("文件未成功删除"); // err1
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resData.setCode(-1).setMsg("文件删除异常"); // err-1
        }
        return resData;
    }

}
