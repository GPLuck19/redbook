package com.gp.controller.assets;


import cn.dev33.satoken.annotation.SaIgnore;
import com.gp.exception.ServiceException;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.utils.MinioUtil;
import com.gp.utils.OSSUtils;
import com.gp.utils.SftpClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


/**
 * 文件上传 控制层
 */
@RestController
@RequiredArgsConstructor
@SaIgnore
public class SysOssController {


    private final OSSUtils aliOSSUtils;

    private final SftpClientUtils sftpClientUtils;

    private final MinioUtil minioUtil;


    @PostMapping("/api/assets-service/uploadOss")
    public Result<String> upload(MultipartFile file) {
        try {
            String uploadedFile = aliOSSUtils.uploadFile(file);
            return Results.success(uploadedFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/api/assets-service/uploadByFtp")
    public Result<String> uploadByFtp(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        // 获取文件输入流和文件名
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String fileName = file.getOriginalFilename();
        // 定义上传到 SFTP 的目标目录 (可根据需求动态定义)
        String remoteDir = "/data/images";
        // 调用 SFTP 工具类上传文件
        String remoteFilePath = sftpClientUtils.uploadFile(remoteDir, fileName, inputStream);
        // 返回成功结果
        return Results.success(remoteFilePath);

    }

    @PostMapping("/api/assets-service/upload")
    public Result<String> uploadByMinio(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        String remoteFilePath = minioUtil.uploadFile(inputStream, fileName, contentType);
        // 返回成功结果
        return Results.success(remoteFilePath);

    }


}
