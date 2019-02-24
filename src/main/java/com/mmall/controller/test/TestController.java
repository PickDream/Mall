package com.mmall.controller.test;

import com.google.common.collect.Maps;
import com.mmall.service.IFileService;
import com.mmall.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Maoxin
 * @ClassName TestController
 * @date 2/15/2019
 */
@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    IFileService iFileService;

    @ResponseBody
    @RequestMapping("richTextImgUpload.do")
    public Map upload(HttpSession session, @RequestParam("upload_file") MultipartFile file, HttpServletResponse response){
        String path = session.getServletContext().getRealPath("upload");
        Map resultMap = Maps.newHashMap();
        String imgServerPath = PropertiesUtil.getProperty("ftp.server.http.perfix","http://47.93.241.68/")+iFileService.upload(file,path);
        resultMap.put("success",true);
        resultMap.put("msg","上传成功");
        resultMap.put("file_path",imgServerPath);
        return resultMap;
    }
}
