package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrdersTask {

    private final OrderMapper orderMapper;
    @Autowired
    public OrdersTask(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }


    /**
     * 处理超时未支付订单
     * <p>cron: 每天7-23时，每隔1分钟检查一次</p>
     */
    @Scheduled(cron = "0 * 7-23 * * ?")
    public void processTimeoutOrder() {
        log.info("支付超时订单处理{}", LocalDateTime.now());

        Integer status = Orders.PENDING_PAYMENT;
        LocalDateTime orderTime = LocalDateTime.now().minusMinutes(15);

        List<Orders> timeoutOrdersList = orderMapper.getByStatusAndTime(status, orderTime);
        if (timeoutOrdersList != null && !timeoutOrdersList.isEmpty()) {
            for (Orders orders : timeoutOrdersList) {
                // 取消
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }


    }

    /**
     * 处理闭店未完成的订单
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void processDeliveryOrder() {
        log.info("闭店未完成订单处理");

        Integer status = Orders.DELIVERY_IN_PROGRESS;
        LocalDateTime orderTime = LocalDateTime.now();

        List<Orders> deliveryOrdersList = orderMapper.getByStatusAndTime(status, orderTime);
        if (deliveryOrdersList != null && !deliveryOrdersList.isEmpty()) {
            for (Orders orders : deliveryOrdersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }

}
