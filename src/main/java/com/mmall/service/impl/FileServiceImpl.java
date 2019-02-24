package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FtpUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Maoxin
 * @ClassName FileServiceImpl
 * @date 2/14/2019
 */

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    //返回上传之后的名字,第二个参数是传递一个临时存放的位置
    @Override
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //获取扩展名,从后面的开始处理
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        //先从tomcat的临时文件夹搬运到本地规范的目录，之后将其上传后删除
        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdir();
        }

        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            FtpUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到服务器上
            targetFile.delete();
        } catch (IOException e) {
            //上传文件失败
            e.printStackTrace();
        }
        return targetFile.getName();
    }
}
