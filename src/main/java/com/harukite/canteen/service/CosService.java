package com.harukite.canteen.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 腾讯云 COS 对象存储服务。
 * 负责文件的上传、删除等操作。
 */
@Service
@Slf4j // Lombok 注解，用于生成日志记录器
public class CosService
{

    @Value("${tencent.cos.secret-id}")
    private String secretId;

    @Value("${tencent.cos.secret-key}")
    private String secretKey;

    @Value("${tencent.cos.region}")
    private String regionName;

    @Value("${tencent.cos.bucket-name}")
    private String bucketName;

    @Value("${tencent.cos.base-url}")
    private String baseUrl; // COS 访问域名，用于拼接文件 URL

    private COSClient cosClient;

    /**
     * 服务初始化后，创建 COSClient 实例。
     */
    @PostConstruct
    public void init()
    {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(regionName));
        this.cosClient = new COSClient(cred, clientConfig);
        log.info("Tencent COS Client initialized for bucket: {}", bucketName);
    }

    /**
     * 上传文件到 COS。
     *
     * @param file       MultipartFile 文件对象
     * @param folderName COS 存储桶内的文件夹名称（可选，例如 "dishes/", "canteens/"）
     * @return 上传成功后的文件 URL
     * @throws IOException 如果文件处理失败
     */
    public String uploadFile(MultipartFile file, String folderName) throws IOException
    {
        if (file.isEmpty())
        {
            throw new IOException("Cannot upload empty file.");
        }

        // 生成唯一的文件名，保留原始文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains("."))
        {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String key = (folderName != null ? folderName : "") + UUID.randomUUID().toString() + fileExtension;

        try (InputStream inputStream = file.getInputStream())
        {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType()); // 设置文件类型

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            cosClient.putObject(putObjectRequest);

            // 返回文件的可访问 URL
            // COS 的 URL 格式通常是 https://<bucket-name>.cos.<region>.myqcloud.com/<key>
            return baseUrl + "/" + key;
        }
        catch (Exception e)
        {
            log.error("Failed to upload file to COS: {}", e.getMessage(), e);
            throw new IOException("Failed to upload file to COS: " + e.getMessage(), e);
        }
    }

    /**
     * 上传文本内容作为文件到 COS。
     *
     * @param content    文本内容
     * @param filename   文件的名称（例如 "report.txt", "log.csv"）
     * @param folderName COS 存储桶内的文件夹名称（可选）
     * @return 上传成功后的文件 URL
     * @throws IOException 如果文本内容处理或上传失败
     */
    public String uploadTextFile(String content, String filename, String folderName) throws IOException
    {
        if (content == null || content.isEmpty())
        {
            throw new IOException("Cannot upload empty content.");
        }

        // 生成唯一的文件名，保留原始文件扩展名，或直接使用传入的filename
        String fileExtension = "";
        if (filename != null && filename.contains("."))
        {
            fileExtension = filename.substring(filename.lastIndexOf("."));
        }
        String key = (folderName != null ? folderName : "") + UUID.randomUUID().toString() + fileExtension;

        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8); // 将字符串转换为字节数组
        try (InputStream inputStream = new ByteArrayInputStream(contentBytes))
        {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contentBytes.length);
            objectMetadata.setContentType(MediaType.TEXT_PLAIN_VALUE); // 设置为纯文本类型

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            cosClient.putObject(putObjectRequest);

            return baseUrl + "/" + key;
        }
        catch (Exception e)
        {
            log.error("Failed to upload text file to COS: {}", e.getMessage(), e);
            throw new IOException("Failed to upload text file to COS: " + e.getMessage(), e);
        }
    }


    /**
     * 根据文件 URL 删除 COS 中的文件。
     *
     * @param fileUrl 要删除的文件的完整 URL
     */
    public void deleteFile(String fileUrl)
    {
        if (fileUrl == null || fileUrl.isEmpty() || !fileUrl.startsWith(baseUrl))
        {
            log.warn("Invalid file URL for deletion: {}", fileUrl);
            return;
        }
        // 从 URL 中提取 key
        String key = fileUrl.substring(baseUrl.length() + 1); // +1 是为了去掉开头的斜杠

        try
        {
            cosClient.deleteObject(bucketName, key);
            log.info("File deleted from COS: {}", fileUrl);
        }
        catch (Exception e)
        {
            log.error("Failed to delete file from COS: {}", e.getMessage(), e);
            // 可以选择抛出异常或仅记录日志，取决于业务需求
        }
    }
}
