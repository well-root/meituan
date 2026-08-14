package com.sky.mapper;

import org.apache.ibatis.annotations.Delete;

public interface DishFlavorMapper {
    /**
     * 根据菜品id删除对应的口味数据
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);
}
