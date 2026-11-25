package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清除当前分类下的菜品缓存
        cleanCache("dish_" + dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品批量删除
     */
    @DeleteMapping("/{id}")
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);

        //清除所有菜品缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 修改菜品
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //清除所有菜品缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 菜品起售停售
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);

        //清除所有菜品缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询菜品
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id来查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品:{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 根据分类id查询菜品
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 清理redis缓存
     *
     * @param pattern 要匹配的key模式，例如 dish_*
     */
    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
