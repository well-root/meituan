package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {


    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品和关联的口味
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

//    修改菜品
    void updateWithFlavor(DishDTO dishDTO);

    List<Dish> list(long categoryId);
}
