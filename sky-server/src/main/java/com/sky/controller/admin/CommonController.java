package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliyunOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/admin/common")
public class CommonController {

    private final AliyunOSSUtil aliyunOSSUtil;
    @Autowired
    public CommonController(AliyunOSSUtil aliyunOSSUtil) {
        this.aliyunOSSUtil = aliyunOSSUtil;
    }

    @PostMapping("/upload")
    public Result<String> uploadFile(MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            String url = aliyunOSSUtil.upload(file, file.getOriginalFilename());
            return Result.success(url);
        }
        return Result.error("upload error");
    }
}
