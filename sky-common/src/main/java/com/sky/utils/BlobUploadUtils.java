package com.sky.utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
public class BlobUploadUtils {
    private final BlobServiceClient blobServiceClient;

    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String containerName;

    private BlobContainerClient containerClient;

	@Autowired
	public BlobUploadUtils(BlobServiceClient blobServiceClient) {
		this.blobServiceClient = blobServiceClient;
	}

	/**
     * 初始化时确保 Azure 容器存在。
     */
    @PostConstruct
    public void init() {
        containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) {
            containerClient.create();
            log.info("Azure Blob 容器 '{}' 已创建。", containerName);
        } else {
            log.info("已连接到现有的 Azure Blob 容器 '{}'。", containerName);
        }
    }

    /**
     * 核心方法：上传文件并返回其公共 URL。
     * @param file Spring 的 MultipartFile 对象
     * @return 上传后文件的可访问 URL
     * @throws IOException 如果文件流操作失败
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 1. 生成唯一的文件名以避免冲突
	    LocalDate today = LocalDate.now();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM");
		String datePath = today.format(formatter);
        String newFileName = datePath + "/" + UUID.randomUUID() + getFileExtension(file.getOriginalFilename());

        // 2. 获取一个指向新文件名的 BlobClient
        BlobClient blobClient = containerClient.getBlobClient(newFileName);

        // 3. 执行上传
        blobClient.upload(file.getInputStream(), file.getSize(), true);

	    // 4. 返回文件的公共访问 URL
        return blobClient.getBlobUrl();
    }

    /**
     * 内部辅助方法，用于提取文件扩展名。
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}