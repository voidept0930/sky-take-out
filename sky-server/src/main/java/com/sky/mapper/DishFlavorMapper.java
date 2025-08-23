package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 新增菜品之菜品味道
     * @param dishFlavorList
     */
    void saveDishFlavour(List<DishFlavor> dishFlavorList);
    /**
     * 批量删除菜品口味
     * @param dishIds
     */
    void deleteDishFlavor(List<Long> dishIds);

    /**
     * 根据菜品id查询口味
     * @param dishId
     * @return
     */
    List<DishFlavor> getById(Long dishId);

}
