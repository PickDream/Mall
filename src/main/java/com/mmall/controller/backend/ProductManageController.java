package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mysql.cj.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

/**
 * @author Maoxin
 * @date 2/10/2019
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IProductService iProductService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IFileService iFileService;

    @ResponseBody
    @RequestMapping(value = "save.do")
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.updateOrSaveProduct(product);
        }else {
            return ServerResponse.createByError("无权限访问");
        }
    }

    //设置商品上下架状态
    @ResponseBody
    @RequestMapping(value = "set_sale_status.do")
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.changeProductStatus(productId,status);
        }else {
            return ServerResponse.createByError("无权限访问");
        }
    }

    @ResponseBody
    @RequestMapping(value = "detail.do")
    public ServerResponse getDetail(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductDetail(productId);
        }else {
            return ServerResponse.createByError("无权限访问");
        }
    }
    //分页获取
    @ResponseBody
    @RequestMapping(value = "list.do")
    public ServerResponse getList(HttpSession session
            , @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum
            ,@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductList(pageNum,pageSize);
        }else {
            return ServerResponse.createByError("无权限访问");
        }
    }
    @ResponseBody
    @RequestMapping(value = "search.do")
    //分页获取搜索列表
    public ServerResponse search(HttpSession session,String productName,Integer productId
            , @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum
            ,@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        }else {
            return ServerResponse.createByError("无权限访问");
        }
    }


    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.perfix","http://47.93.241.68/")+targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else {
            return ServerResponse.createByError("无权限访问");
        }
    }
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richTextImgupload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpSession session){
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.perfix","http://47.93.241.68/")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("file_path",url);
            return resultMap;
        }else {
            resultMap.put("success",false);
            resultMap.put("msg","无权操作");
            return resultMap;
        }
    }
}
