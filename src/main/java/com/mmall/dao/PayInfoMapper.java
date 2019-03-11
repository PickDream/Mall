package com.mmall.dao;

import com.github.pagehelper.PageInfo;
import com.mmall.pojo.PayInfo;
import org.apache.ibatis.annotations.Param;

public interface PayInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    PayInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayInfo record);

    int updateByPrimaryKey(PayInfo record);

    PayInfo selectByUserIdAndOrderNo(@Param("userId") Integer userId,
                                      @Param("orderNo") Long orderNo);
}