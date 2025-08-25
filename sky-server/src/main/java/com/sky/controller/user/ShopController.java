package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@Slf4j
@RequestMapping("/user/shop")
public class ShopController {

    private final ValueOperations<String, Object> valueOperations;
    @Autowired
    public ShopController(RedisTemplate<String, Object> redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }

    /**
     * 查询营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        log.info("获取营业状态");
        return Result.success((Integer) valueOperations.get("status"));
    }
}
