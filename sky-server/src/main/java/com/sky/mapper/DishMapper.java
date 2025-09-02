package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @Insert("INSERT INTO dish (name, category_id, price, image, description, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(OperationType.INSERT)
    void saveDish(Dish dish);

    /**
     * 菜品分页查询
     * @param categoryId
     * @param name
     * @param status
     * @return
     */
    List<DishVO> pageQuery(Long categoryId, String name, Integer status);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteDish(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    Dish getById(Long id);

    /**
     * 修改菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 菜品起售、停售
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void startOrStop(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> getByCategory(Long categoryId);

    /**
     * 根据菜品状态查询菜品数量
     * @param status
     * @return
     */
    Integer getNumberByStatus(Integer status);

}
