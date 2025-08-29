package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@Slf4j
@RequestMapping("/admin/order")
public class OrderController {

    private final OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索{}", ordersPageQueryDTO);
        return Result.success(orderService.pageQuery(ordersPageQueryDTO));
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        log.info("各个状态的订单数量统计");
        return Result.success(orderService.statistics());
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        log.info("查询订单{}详情", id);
        return Result.success(orderService.getById(id));
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    public Result<String> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单{}", ordersConfirmDTO.getId());
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    public Result<String> rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单{}", ordersRejectionDTO.getId());
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }


    @PutMapping("/cancel")
    public Result<String> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单{}", ordersCancelDTO.getId());
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result<String> delivery(@PathVariable Long id) {
        log.info("派送订单{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result<String> complete(@PathVariable Long id) {
        log.info("完成订单{}", id);
        orderService.complete(id);
        return Result.success();
    }

}
