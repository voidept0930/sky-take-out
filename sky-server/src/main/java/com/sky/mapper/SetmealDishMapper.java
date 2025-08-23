package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 新增套餐菜品
     * @param setmealDishList
     */
    void save(List<SetmealDish> setmealDishList);

    /**
     * 批量删除套餐
     * @param setmealIds
     */
    void delete(List<Long> setmealIds);

    /**
     * 根据id查询套餐
     * @param setmealId
     * @return
     */
    List<SetmealDish> getById(Long setmealId);
}
