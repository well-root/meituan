package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")//key: setmealCache::100
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }
    //分页查询的方法

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult= setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);

    }


    //删除的方法

    //@RequestParam 是 Spring 框架中用于处理 HTTP 请求参数的注解。
    // 它允许你将请求参数绑定到控制器的方法参数上，无论是查询参数、表单参数还是多个参数。
    // 这个注解非常灵活，提供了多种属性来定制参数的处理方式。
    @DeleteMapping
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids){

        setmealService.deleteBatch(ids);
        return Result.success();

    }

    //查询菜单

    @GetMapping("/{id}")
    @ApiOperation("根据id查询")
    public  Result<SetmealVO> getById(@PathVariable Long id ){

        SetmealVO setmealVO=setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);

    }


    //修改菜单
    @PutMapping
    @ApiOperation("修改菜单")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public  Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("起售还是停售")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status,Long id){

        setmealService.startOrStop(status,id);
        return Result.success();
    }



}