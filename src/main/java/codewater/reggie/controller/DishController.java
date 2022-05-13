package codewater.reggie.controller;

import codewater.reggie.common.R;
import codewater.reggie.dto.DishDto;
import codewater.reggie.entity.Category;
import codewater.reggie.entity.Dish;
import codewater.reggie.service.CategoryService;
import codewater.reggie.service.DishFlavorService;
import codewater.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ： CodeWater
 * @create ：2022-05-12-20:36
 * @Function Description ：菜品管理
 * 对于菜品dish的操作和菜品口味dishflavor的操作都在这个里面
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    
    @Autowired
    private DishService dishService ;
    
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    
    
    /**
     * 新增菜品
     * @param dishDto :提交的json数据，所以加个注解requestbody
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info( dishDto.toString() );
        
        dishService.saveWithFlavor( dishDto );
        
        return R.success("新增菜品成功！");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page( int page , int pageSize , String name ){
//        构造分页构造器对象
        Page<Dish> pageInfo = new Page<>( page , pageSize );
        Page<DishDto> dishDtoPage = new Page<>();//为了后端能够返回categoryName属性，用dishDto
        
//        条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        添加过滤条件
        queryWrapper.like( name != null , Dish::getName , name );
//        添加排序天剑
        queryWrapper.orderByDesc( Dish::getUpdateTime );
        
//        执行分页查询
        dishService.page( pageInfo , queryWrapper );
        
//        这么写，前端会有些信息展示不出来（菜品分类 ， 后端返回的是一个category_id）
//        return R.success( pageInfo );
        
//        pageInfo中的信息拷贝到dishDtoPage中去（除了records不拷贝)
        BeanUtils.copyProperties( pageInfo , dishDtoPage , "records" );

        List<Dish> records = pageInfo.getRecords();
        
        List<DishDto> list = records.stream().map( (item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties( item , dishDto );
            
            Long categoryId = item.getCategoryId();//分类id
            
//            根据id查到对应的category分类对象
            Category category = categoryService.getById( categoryId );
            if( category != null ){  //有些数据是直接导入的，可能查不到，所以判个空
                String categoryName = category.getName();
    //          把分类的名字赋到dishDto中对应的属性上去
                dishDto.setCategoryName( categoryName );
            }
            
//            返回最终赋好值的对象
            return dishDto;
        }).collect( Collectors.toList() ); //转成集合
        
        dishDtoPage.setRecords( list );
        
        return R.success( dishDtoPage );
    }

    /**
     * 根据id查询菜品信息和口味信息
     * 要回显菜品，口味，所以用dishDto
     * 用@PathVariable，是因为参数在url中,不是像rest风格中在？的后面
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id ){
//        DishService中查询方法已经写好
        DishDto dishDto = dishService.getByIdWithFlavor( id );
        
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto :提交的json数据，所以加个注解requestbody
     *                设计到两个表
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info( dishDto.toString() );

        dishService.updateWithFlavor( dishDto );

        return R.success("新增菜品成功！");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping( "/list" )
    public R<List<Dish>> list( Dish dish ){
        
//        构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //相当于sql： WHERE (category_id = ?)
        queryWrapper.eq( dish.getCategoryId() != null , Dish::getCategoryId , dish.getCategoryId() );
//        只查询状态为1（起售）的
        queryWrapper.eq( Dish::getStatus , 1 );
        
//        添加排序条件
        queryWrapper.orderByAsc( Dish::getSort ).orderByDesc( Dish::getUpdateTime );

        
        List<Dish> list = dishService.list( queryWrapper );
        return R.success( list );
        
    }
}
