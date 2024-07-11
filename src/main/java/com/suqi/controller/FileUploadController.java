package com.suqi.controller;

import com.suqi.pojo.Result;
import com.suqi.utils.AliOssUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.util.UUID;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        //把文件的内容存到本地磁盘上
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        //file.transferTo(new File("E:\\赛题\\02_资料\\files\\"+filename));
        String url = AliOssUtil.upLoadFile(filename,file.getInputStream());
        return Result.success(url);
    }
}
