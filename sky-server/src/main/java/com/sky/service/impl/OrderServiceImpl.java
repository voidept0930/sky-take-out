package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    // 订单号计数
    private static Long number = 0L;
    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper,
                            OrderDetailMapper orderDetailMapper,
                            AddressBookMapper addressBookMapper,
                            ShoppingCartMapper shoppingCartMapper) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
    }

    /**
     * 用户下单，涉及两张表的添加以及两张表的查询
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {

        // 设置订单基本信息
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        number++;
        orders.setNumber(String.valueOf(number));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(BaseContext.getCurrentUserId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() +
                addressBook.getDistrictName() + addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orderMapper.saveOrder(orders);

        // 设置订单detail信息
        List<OrderDetail> orderDetailList = new ArrayList<>();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.get(BaseContext.getCurrentUserId());
        for (ShoppingCart shoppingCart: shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setOrderId(orders.getId());
            if (shoppingCart.getDishId() != null) {
                orderDetail.setDishId(shoppingCart.getDishId());
            }
            if (shoppingCart.getSetmealId() != null) {
                orderDetail.setSetmealId(shoppingCart.getSetmealId());
            }
            if (shoppingCart.getDishFlavor() != null) {
                orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            }
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.saveOrderDetail(orderDetailList);

        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(orders.getId());
        orderSubmitVO.setOrderAmount(orders.getAmount());
        orderSubmitVO.setOrderNumber(String.valueOf(number));
        orderSubmitVO.setOrderTime(orders.getOrderTime());

        return orderSubmitVO;
    }
}
