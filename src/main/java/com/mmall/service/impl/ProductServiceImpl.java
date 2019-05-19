package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Maoxin
 * @date 2/10/2019
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse updateOrSaveProduct(Product product) {
        if (Objects.isNull(product)){
            return ServerResponse.createByError("新增的产品信息参数不正确");
        }
        //处理上传的图片内容，使得对象符合要求
        if (StringUtils.isNotBlank((product.getSubImages()))){
            String[] imagesUrls = product.getSubImages().split(",");
            if (imagesUrls.length>0){
                product.setMainImage(imagesUrls[0]);
            }
        }
        //判断是什么类型的操作
        if (product.getId()!=null){
            int rowCount = productMapper.updateByPrimaryKey(product);
            if (rowCount>0){
                return ServerResponse.createBySuccess("更新产品成功");
            }
            return ServerResponse.createByError("更新产品信息失败");
        }else {
            int rowCount = productMapper.insert(product);
            if (rowCount>0){
                return ServerResponse.createBySuccess("新增产品成功");
            }
            return ServerResponse.createByError("更新产品失败");
        }
    }

    @Override
    public ServerResponse changeProductStatus(Integer productId, Integer status) {
        if (productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.IllEGAL_ARGUMENT.getCode()
                    ,ResponseCode.IllEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount>0){
            return ServerResponse.createBySuccess("修改商品状态成功");
        }
        return ServerResponse.createByError("修改商品状态发生错误！");
    }

    @Override
    public ServerResponse getProductDetail(Integer productId){
        if (Objects.isNull(productId)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.IllEGAL_ARGUMENT.getCode()
                    ,"参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (Objects.isNull(product)){
            return ServerResponse.createByError("商品下架或者已经删除");
        }
        ProductDetailVo productDetailVo  = assemableProductVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize){
        //startPage
        PageHelper.startPage(pageNum,pageSize);
        //填充SQL
        List<Product> products = productMapper.selectList();
        List<ProductListVo> voList = Lists.newArrayList();
        //pageHelper的收尾
        for (Product product:products){
            voList.add(assambleProductListVo(product));
        }
        PageInfo pageResult = new PageInfo(products);
        pageResult.setList(voList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productId,productName);
        List<ProductListVo> voList = Lists.newArrayList();
        for (Product product:productList){
            voList.add(assambleProductListVo(product));
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(voList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 获取商品列表通过关键词以及品类Id
     * @param keyword 关键字
     * @param categoryId 品类Id
     * @param pageNum 当前页面位置
     * @param pageSize 页面的大小
     * @param orderBy 排序方式，接受
     * */
    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        // keyword以及categoryId都为空的失衡报错
        if (StringUtils.isBlank(keyword)&&keyword==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.IllEGAL_ARGUMENT.getCode(),
                    ResponseCode.IllEGAL_ARGUMENT.getDesc());
        }
        //
        List<Integer> categoryIdList = new ArrayList<Integer>();

        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        //构造模糊查询匹配串
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assambleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);

    }


    private ProductDetailVo assemableProductVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCatrgoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.perfix","http://47.93.241.68/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getId());

        //商品详情中得到父类别的信息
        productDetailVo.setParentCategoryId(Objects.isNull(category)?0:category.getId());
        return productDetailVo;
    }


    private ProductListVo assambleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.perfix","http://47.93.241.68/"));
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        productListVo.setName(product.getName());
        return productListVo;
    }
}
