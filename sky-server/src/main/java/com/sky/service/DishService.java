package com.sky.service;

import com.sky.entity.Dish;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {


    static void deleteBatch(List<Long> ids) {
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);


}
