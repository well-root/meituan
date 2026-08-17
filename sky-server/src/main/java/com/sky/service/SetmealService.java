package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询包含的菜品列表
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    //套餐删除的方法
    void deleteBatch(List<Long> ids);

//    修改哦套餐的方法
    SetmealVO getByIdWithDish(Long id);

    void update(SetmealDTO setmealDTO);

    //起售或停
    void startOrStop(Integer status, Long id);
}
