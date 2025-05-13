package com.gp.utils;

import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Component
public class SftpClientUtils {

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private int port;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    private ChannelSftp setupJsch() throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();

        return (ChannelSftp) session.openChannel("sftp");
    }

    public String uploadFile(String baseDir, String fileName, InputStream inputStream) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch();
            channelSftp.connect();

            // 获取当前日期并生成目录
            String dateDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String remoteDir = baseDir + "/" + dateDir;

            // 检查并创建目录
            createDirectoryIfNotExists(channelSftp, remoteDir);

            // 切换到目标目录
            channelSftp.cd(remoteDir);

            // 上传文件
            channelSftp.put(inputStream, fileName);

            // 返回上传后的文件路径
            return remoteDir + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }
    }

    private void createDirectoryIfNotExists(ChannelSftp channelSftp, String remoteDir) throws SftpException {
        String[] folders = remoteDir.split("/");
        StringBuilder currentPath = new StringBuilder();
        for (String folder : folders) {
            if (folder.isEmpty()) continue; // 跳过空路径
            currentPath.append("/").append(folder);
            try {
                channelSftp.cd(currentPath.toString());
            } catch (SftpException e) {
                // 如果目录不存在，创建
                channelSftp.mkdir(currentPath.toString());
                channelSftp.cd(currentPath.toString());
            }
        }
    }



    public void downloadFile(String remoteFilePath, String localDir) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch();
            channelSftp.connect();

            String fileName = new File(remoteFilePath).getName();
            File localFile = new File(localDir + File.separator + fileName);
            channelSftp.get(remoteFilePath, new FileOutputStream(localFile));
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }
    }

    public void deleteFile(String remoteFilePath) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch();
            channelSftp.connect();
            channelSftp.rm(remoteFilePath);
        } catch (SftpException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        } catch (JSchException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
        }
    }
}
