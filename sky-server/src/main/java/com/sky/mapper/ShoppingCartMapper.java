package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 查询购物车数量
     * @param shoppingCart
     * @return
     */
    Integer getNumber(ShoppingCart shoppingCart);

    /**
     * 添加至购物车
     * @param shoppingCart
     */
    void save(ShoppingCart shoppingCart);

    /**
     * 更新商品数量+1
     * @param shoppingCart
     */
    void updateNumberIncr(ShoppingCart shoppingCart);

    /**
     * 更新商品数量-1
     * @param shoppingCart
     */
    void updateNumberDecr(ShoppingCart shoppingCart);

    /**
     * 查看购物车
     * @param userId
     * @return
     */
    List<ShoppingCart> get(String userId);

    /**
     * 删除购物车的一行
     * @param shoppingCart
     */
    void deleteOne(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     * @param userId
     */
    void clean(String userId);


}
