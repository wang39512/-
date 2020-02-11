package com.offcn.controller;

import com.offcn.common.Result;
import com.offcn.common.UploadUtils;
import com.qcloud.cos.COSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class UploadFileController {
    @Autowired
    private COSClient cosClient;

    private String dir = "img";


    @RequestMapping("/upload")
    public String fileUpload(MultipartFile file) {
       return UploadUtils.upload(cosClient, dir, file);

    }
}
