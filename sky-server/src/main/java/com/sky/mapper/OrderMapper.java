package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 将用户下单信息保存到数据库
     * @param orders
     */
    void saveOrder(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     */
    List<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    Orders getById(Long id);

    /**
     * 根据状态查询订单数量
     * @param status
     * @return
     */
    Integer getByStatus(Integer status);

    /**
     * 根据状态和时间查询订单
     * @return
     */
    List<Orders> getByStatusAndTime(Integer status, LocalDateTime orderTime);

    /**
     * 根据状态查订单数量
     * @param status
     * @return
     */
    Integer getNumberByStatus(Integer status);

}
