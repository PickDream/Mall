package com.mmall.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Maoxin
 * @ClassName OrderItemVo
 * @date 3/9/2019
 */
public class OrderItemVo {
    private Long orderNo;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private BigDecimal quatity;

    private BigDecimal totalPrice;

    private String createTime;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public BigDecimal getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
        this.currentUnitPrice = currentUnitPrice;
    }

    public BigDecimal getQuatity() {
        return quatity;
    }

    public void setQuatity(BigDecimal quatity) {
        this.quatity = quatity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
