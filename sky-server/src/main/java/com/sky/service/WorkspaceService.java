package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {

    /**
     * 查询今日运营数据
     * @return
     */
    BusinessDataVO getBusinessData();

    /**
     * 查询套餐总览
     * @return
     */
    SetmealOverViewVO getOverviewSetmeals();

    /**
     * 查询菜品总览
     * @return
     */
    DishOverViewVO getOverviewDishes();

    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO getOverviewOrders();
}
