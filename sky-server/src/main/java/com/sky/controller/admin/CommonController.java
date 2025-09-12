package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.BlobUploadUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 通用接口，提供文件上传等功能
 */
@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口")
@Slf4j
public class CommonController {

    private final BlobUploadUtils blobUploadUtils;

    @Autowired
    public CommonController(BlobUploadUtils blobUploadUtils) {
        this.blobUploadUtils = blobUploadUtils;
    }

    /**
     * 文件上传
     *
     * @param file 待上传的文件
     * @return 包含文件访问 URL 的 Result 对象
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("开始文件上传: {}", file.getOriginalFilename());

        // 校验文件是否为空
        if (file.isEmpty()) {
            return Result.error(MessageConstant.UPLOAD_FAILED + ": 文件为空");
        }

        try {
            // 调用工具类上传文件
            String fileUrl = blobUploadUtils.uploadFile(file);
            log.info("文件上传成功，访问 URL: {}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
