package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.*;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final UserMapper userMapper;
    private final SetmealMapper setmealMapper;
    private final DishMapper dishMapper;
    private final OrderMapper orderMapper;
    @Autowired
    public WorkspaceServiceImpl(UserMapper userMapper,
                                SetmealMapper setmealMapper,
                                DishMapper dishMapper,
                                OrderMapper orderMapper
                                ) {
        this.userMapper = userMapper;
        this.setmealMapper = setmealMapper;
        this.dishMapper = dishMapper;
        this.orderMapper = orderMapper;
    }


    /**
     * 查询今日运营数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {

        // 当日新增用户查询
        LocalDateTime zero = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime twentyFour = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Integer newUsers = userMapper.getNewUserByDate(zero, twentyFour);

        // 订单完成率查询
        Integer validOrderCount = orderMapper.getOrderCountByDate(zero, twentyFour, Orders.COMPLETED);
        Integer allOrderCount = orderMapper.getOrderCountByDate(zero, twentyFour, null);
        Double orderCompletionRate = validOrderCount.doubleValue() / allOrderCount.doubleValue();

        // 营业额查询
        Double turnover = orderMapper.getTurnOverByDate(zero, twentyFour);

        // 平均客单价查询
        Double unitPrice = turnover / validOrderCount;

        return new BusinessDataVO(turnover, validOrderCount, orderCompletionRate, unitPrice, newUsers);
    }

    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO getOverviewSetmeals() {
        Integer discontinued = setmealMapper.getNumberByStatus(0);
        Integer sold = setmealMapper.getNumberByStatus(1);

        return new SetmealOverViewVO(sold, discontinued);
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO getOverviewDishes() {
        Integer discontinued = dishMapper.getNumberByStatus(0);
        Integer sold = dishMapper.getNumberByStatus(1);

        return new DishOverViewVO(sold, discontinued);
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO getOverviewOrders() {
        Integer allOrders = orderMapper.getNumberByStatus(null);
        Integer cancelledOrders = orderMapper.getNumberByStatus(Orders.CANCELLED);
        Integer completedOrders = orderMapper.getNumberByStatus(Orders.COMPLETED);
        Integer deliveredOrders = orderMapper.getNumberByStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer waitingOrders = orderMapper.getNumberByStatus(Orders.TO_BE_CONFIRMED);

        return new OrderOverViewVO(
                waitingOrders, deliveredOrders, completedOrders, cancelledOrders, allOrders
        );
    }
}
