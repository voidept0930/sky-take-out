package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;
    @Autowired
    public SetmealServiceImpl(SetmealMapper setmealMapper, SetmealDishMapper setmealDishMapper) {
        this.setmealMapper = setmealMapper;
        this.setmealDishMapper = setmealDishMapper;
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(
                setmealPageQueryDTO.getPage(),
                setmealPageQueryDTO.getPageSize()
        );
        List<Setmeal> setmealList = setmealMapper.pageQuery(
                setmealPageQueryDTO.getCategoryId(),
                setmealPageQueryDTO.getName(),
                setmealPageQueryDTO.getStatus()
        );
        PageInfo<Setmeal> pageInfo = new PageInfo<>(setmealList);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.save(setmeal);

        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        for(SetmealDish setmealDish: setmealDishList) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.save(setmealDishList);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        setmealMapper.delete(ids);
        setmealDishMapper.delete(ids);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        setmealDishMapper.delete(Collections.singletonList(setmealDTO.getId()));
        setmealDishMapper.save(setmealDTO.getSetmealDishes());
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        List<SetmealDish> setmealDishList = setmealDishMapper.getById(id);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO;

    }

    /**
     * 套餐起售、停售
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        setmealMapper.startOrStop(status, id);
    }

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> getByCategoryId(Long categoryId) {
        return setmealMapper.getByCategoryId(categoryId);
    }

    /**
     * 根据套餐id查询包含的菜品
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getContainedDishById(Long id) {
        return setmealDishMapper.getContainedDishById(id);
    }
}
