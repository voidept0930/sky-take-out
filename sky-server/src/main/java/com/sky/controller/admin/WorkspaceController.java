package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/admin/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    @Autowired
    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    /**
     * 查询今日运营数据
     * @return
     */
    @GetMapping("/businessData")
    public Result<BusinessDataVO> getBusinessData() {
        log.info("查询今日运营数据");
        return Result.success(workspaceService.getBusinessData());
    }

    /**
     * 查询套餐总览
     * @return
     */
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> getOverviewSetmeals() {
        log.info("查询套餐总览");
        return Result.success(workspaceService.getOverviewSetmeals());
    }


    /**
     * 查询菜品总览
     * @return
     */
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> getOverviewDishes() {
        log.info("查询菜品总览");
        return Result.success(workspaceService.getOverviewDishes());
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> getOverviewOrders() {
        log.info("查询订单管理数据");
        return Result.success(workspaceService.getOverviewOrders());
    }

}
