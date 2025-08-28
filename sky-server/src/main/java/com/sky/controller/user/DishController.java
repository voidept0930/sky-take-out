package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@Slf4j
@RequestMapping("/user/dish")
public class DishController {

    private final DishService dishService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOperations;
    @Autowired
    public DishController(DishService dishService, RedisTemplate<String, Object> redisTemplate) {
        this.dishService = dishService;
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> gerByCategory(Long categoryId) {
        log.info("根据分类id{}查询菜品（C端）", categoryId);

        // 查询Redis中是否存在菜品数据
        String key = "dish_" + categoryId;
        List<DishVO> dishVOList = (List<DishVO>) valueOperations.get(key);

        // 如果存在，直接返回查询数据
        if (dishVOList != null && !dishVOList.isEmpty()) {
            return Result.success(dishVOList);
        }
        // 如果不存在，则查询数据库，并将数据存到Redis中
        dishVOList = dishService.getByCategoryWithFlavors(categoryId);
        valueOperations.set(key, dishVOList);
        return Result.success(dishVOList);
    }
}
