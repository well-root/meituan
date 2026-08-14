package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.ApiOperation;
import jdk.jpackage.internal.Log;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class DishController {

    @DeleteMapping
    @ApiOperation("菜品删除")
    public Result delete(@RequestParam List<Long> ids){
        Log.info("菜品删除:{}");
        DishService.deleteBatch(ids);
        return Result.success();

    }

//    新增的方法
//根据id来查询响应的菜品

    @GetMapping("/{id}")
    ApiOperation("根据id来查询菜品")
    Public Result<DishVO> getById(@PathVariable Long id){
        Log.info("根据id查询菜品",id);
        DishVO dishVO =DishService.getByIdWithFlavor(id);
        return Result.success(dishVO);

    }
}
