package com.bdilab.aiflow.common.utils;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author smile
 * @data 2020/9/15 10:40
 **/
public class MinioFileUtils {
    private MinioClient minioClient;

    private String url;
    private String accessKey;
    private String secretKey;

    public MinioFileUtils(String url,String accessKey,String secretKey,boolean isSecurity){
        try {
            minioClient = new MinioClient(url,accessKey,secretKey,isSecurity);
        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        } catch (InvalidPortException e) {
            e.printStackTrace();
        }
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }

    public void createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void uploadFile(String bucketName, MultipartFile multipartFile,String fileName){
        PutObjectOptions putObjectOptions = new PutObjectOptions(multipartFile.getSize(), PutObjectOptions.MIN_MULTIPART_SIZE);
        putObjectOptions.setContentType(multipartFile.getContentType());
        try {
            minioClient.putObject(bucketName, fileName, multipartFile.getInputStream(), putObjectOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void downLoadFile()

}