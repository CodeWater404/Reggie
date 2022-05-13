package codewater.reggie.controller;

import codewater.reggie.common.R;
import codewater.reggie.dto.SetmealDto;
import codewater.reggie.service.SetmealDishService;
import codewater.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ： CodeWater
 * @create ：2022-05-13-22:34
 * @Function Description ：套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    
    @Autowired
    private SetmealService setmealService;
    
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping()
    public R<String> save( @RequestBody SetmealDto setmealDto){
        log.info( "套餐信息：{}" , setmealDto );
        
        setmealService.saveWithDish( setmealDto );
        
        return R.success( "新增菜品信息成功！" );
    } 
    
}
