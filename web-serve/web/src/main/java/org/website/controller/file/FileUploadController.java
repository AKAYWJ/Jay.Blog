package org.website.controller.file;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.website.common.result.Result;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
    // Mac 本地存储根目录
    private static final String SAVE_BASE_PATH = "/Users/yewenjie/项目资源/MyBlog/fileupload/cover/";
    // 浏览器访问前缀
    private static final String WEB_PREFIX = "/upload/cover/";

    /**
     * 上传文章封面
     */
    @PostMapping("/cover")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            // 根文件夹不存在自动创建
            File baseDir = new File(SAVE_BASE_PATH);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
            // 提取后缀
            String originalName = file.getOriginalFilename();
            String suffix = originalName.substring(originalName.lastIndexOf("."));
            // 按日期分文件夹存储
            String dateFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

            File datePath = new File(SAVE_BASE_PATH + dateFolder + "/");
            if (!datePath.exists()) {
                datePath.mkdirs();
            }
            File target = new File(datePath, fileName);
            file.transferTo(target);

            // 返回前端可访问url
            String accessUrl = WEB_PREFIX + dateFolder + "/" + fileName;
            return Result.success(accessUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(400,"图片上传失败");
        }
    }

    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("ok");
    }
}