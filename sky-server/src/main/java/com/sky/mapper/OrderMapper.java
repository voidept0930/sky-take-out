package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 将用户下单信息保存到数据库
     * @param orders
     */
    void saveOrder(Orders orders);

}
