package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;
    private final DishService dishService;
    private final SetmealService setmealService;
    @Autowired
    public ShoppingCartServiceImpl(ShoppingCartMapper shoppingCartMapper, DishService dishService, SetmealService setmealService) {
        this.shoppingCartMapper = shoppingCartMapper;
        this.dishService = dishService;
        this.setmealService = setmealService;
    }

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void save(ShoppingCartDTO shoppingCartDTO) {

        ShoppingCart shoppingCart = new ShoppingCart();

        if (shoppingCartDTO.getDishFlavor() != null && !shoppingCartDTO.getDishFlavor().isEmpty()) {
            shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
        }
        if (shoppingCartDTO.getDishId() != null && shoppingCartDTO.getDishId() != 0) {
            // 加的是菜品
            DishVO dishVO = dishService.getById(shoppingCartDTO.getDishId());
            shoppingCart.setName(dishVO.getName());
            shoppingCart.setImage(dishVO.getImage());
            shoppingCart.setUserId(BaseContext.getCurrentUserId());
            shoppingCart.setDishId(shoppingCartDTO.getDishId());
            shoppingCart.setAmount(dishVO.getPrice());
            shoppingCart.setCreateTime(LocalDateTime.now());
        }
        if (shoppingCartDTO.getSetmealId() != null && shoppingCartDTO.getSetmealId() != 0) {
            // 加的是套餐
            SetmealVO setmealVO = setmealService.getById(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(setmealVO.getName());
            shoppingCart.setImage(setmealVO.getImage());
            shoppingCart.setUserId(BaseContext.getCurrentUserId());
            shoppingCart.setSetmealId(shoppingCartDTO.getSetmealId());
            shoppingCart.setAmount(setmealVO.getPrice());
            shoppingCart.setCreateTime(LocalDateTime.now());
        }

        Integer number = shoppingCartMapper.getNumber(shoppingCart);
        if (number == null) {
            // 购物车中没有该菜品，需添加
            shoppingCart.setNumber(0);
            shoppingCartMapper.save(shoppingCart);
        } else {
            // 购物车中有菜品，数量加 1
            shoppingCartMapper.updateNumberIncr(shoppingCart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> get() {
        return shoppingCartMapper.get(BaseContext.getCurrentUserId());
    }

    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void deleteOne(ShoppingCartDTO shoppingCartDTO) {

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentUserId());
        if (shoppingCartDTO.getDishFlavor() != null && !shoppingCartDTO.getDishFlavor().isEmpty()) {
            shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
        }
        if (shoppingCartDTO.getDishId() != null && shoppingCartDTO.getDishId() != 0) {
            shoppingCart.setDishId(shoppingCartDTO.getDishId());
        }
        if (shoppingCartDTO.getSetmealId() != null && shoppingCartDTO.getSetmealId() != 0) {
            shoppingCart.setSetmealId(shoppingCartDTO.getSetmealId());
        }

        Integer number = shoppingCartMapper.getNumber(shoppingCart);
        if (number == 1) {
            // 只有一件则删除
            shoppingCartMapper.deleteOne(shoppingCart);
        } else {
            // 一件以上则更新数据
            shoppingCartMapper.updateNumberDecr(shoppingCart);
        }


    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        shoppingCartMapper.clean(BaseContext.getCurrentUserId());
    }
}
