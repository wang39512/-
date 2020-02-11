package com.offcn.controller;

import com.offcn.common.UploadUtils;
import com.qcloud.cos.COSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/up")
public class UploadTest {

    @Autowired
    private COSClient cosClient;

    private String dir = "img";

    @RequestMapping("/uploadd")
    public String upload(MultipartFile file){
        return UploadUtils.upload(cosClient, dir, file);
    }

}
