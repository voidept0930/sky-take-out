package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 将用户下单的细节保存到数据库
     * @param orderDetailList
     */
    void saveOrderDetail(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查订单detail
     * @param orderId
     * @return
     */
    List<OrderDetail> getByOrderId(Long orderId);

}
