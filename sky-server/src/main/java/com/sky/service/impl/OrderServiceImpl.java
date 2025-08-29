package com.sky.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserMapper userMapper;
    private final WeChatPayUtil weChatPayUtil;
    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper,
                            OrderDetailMapper orderDetailMapper,
                            AddressBookMapper addressBookMapper,
                            ShoppingCartMapper shoppingCartMapper,
                            UserMapper userMapper,
                            WeChatPayUtil weChatPayUtil) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.userMapper = userMapper;
        this.weChatPayUtil = weChatPayUtil;
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
        // 订单编号格式：当前时间戳+userId（确保唯一性）
        String number = System.currentTimeMillis() + BaseContext.getCurrentUserId();
        orders.setNumber(number);
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
        orderSubmitVO.setOrderNumber(number);
        orderSubmitVO.setOrderTime(orders.getOrderTime());

        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        String userId = BaseContext.getCurrentUserId();
        User user = userMapper.getByOpenid(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal("0.01"), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        // 跳过支付功能，直接成功
        paySuccess(ordersPaymentDTO.getOrderNumber());

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 前端无法渲染分页按钮，疑似前端问题
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentUserId());
        PageHelper.startPage(
                ordersPageQueryDTO.getPage(),
                ordersPageQueryDTO.getPageSize()
        );
        List<OrderVO> orderVOList = new ArrayList<>();

        // 查询订单基本信息
        List<Orders> ordersList = orderMapper.pageQuery(ordersPageQueryDTO);
        for (Orders orders: ordersList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            // 对每一个订单查询detail
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDetailList);
            orderVOList.add(orderVO);
        }

        PageInfo<OrderVO> pageInfo = new PageInfo<>(orderVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getById(Long id) {
        OrderVO orderVO = new OrderVO();
        Orders orders = orderMapper.getById(id);
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailMapper.getByOrderId(id));
        return orderVO;
    }

    /**
     * 取消订单（用户端）
     * @param id
     */
    @Override
    public void cancel(Long id) {
        Orders orders = new Orders();
        orders.setCancelTime(LocalDateTime.now());
        orders.setId(id);
        orders.setStatus(Orders.CANCELLED);

        // 若订单处于已支付状态，需要进行退款
        Orders orders1 = orderMapper.getById(id);
        if (orders1.getPayStatus().equals(Orders.PAID)) {
            // 调用微信支付退款接口
            orders.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(orders);
    }

    /**
     * 取消订单（管理端）
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());

        // 若订单处于已支付状态，需要进行退款
        Orders orders1 = orderMapper.getById(ordersCancelDTO.getId());
        if (orders1.getPayStatus().equals(Orders.PAID)) {
            // 调用微信支付退款接口
            orders.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     * <p>就是把id对应的菜品重新放入购物车中</p>
     * @param id
     */
    @Override
    public void repetition(Long id) {
        // 拿到这个id有什么菜品/套餐
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        // 清空购物车
        shoppingCartMapper.clean(BaseContext.getCurrentUserId());

        // 将订单信息转换成购物车信息，并添加至购物车
        for (OrderDetail orderDetail: orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentUserId());
            shoppingCartMapper.save(shoppingCart);
        }




    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        Integer toBeConfirmed = orderMapper.getByStatus(Orders.TO_BE_CONFIRMED);
        if (toBeConfirmed != null) {
            orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        }
        Integer confirmed = orderMapper.getByStatus(Orders.CONFIRMED);
        if (confirmed != null) {
            orderStatisticsVO.setConfirmed(confirmed);
        }
        Integer deliveryInProgress = orderMapper.getByStatus(Orders.DELIVERY_IN_PROGRESS);
        if (deliveryInProgress != null) {
            orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        }

        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO, orders);
        orderMapper.update(orders);
    }

    /**
     * 拒单（拒绝+退款）
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersRejectionDTO, orders);
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        // 调用微信支付退款接口
        orders.setPayStatus(Orders.REFUND);
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }
}
