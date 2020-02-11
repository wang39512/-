package com.offcn.common;

import com.qcloud.cos.COSClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class UploadUtils {
    private static String bucketName = "wangwei-1259207560";

    private static String domainName = "https://wangwei-1259207560.cos.ap-beijing.myqcloud.com/";

    public static String upload(COSClient cosClient, String dir, MultipartFile file){
        String fileName = UUID.randomUUID() + file.getOriginalFilename();
        String key = dir + "/" + fileName;
        try {
            cosClient.putObject(bucketName,key,file.getInputStream(),null);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return domainName + key;
    }
}
