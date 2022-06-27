package codewater.reggie.controller;

import codewater.reggie.common.R;
import codewater.reggie.dto.DishDto;
import codewater.reggie.entity.Category;
import codewater.reggie.entity.Dish;
import codewater.reggie.entity.DishFlavor;
import codewater.reggie.service.CategoryService;
import codewater.reggie.service.DishFlavorService;
import codewater.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
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
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 新增菜品
     * @param dishDto :提交的json数据，所以加个注解requestbody
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info( dishDto.toString() );
        
        dishService.saveWithFlavor( dishDto );
        
        //todo: 清除redis的缓存。防止更新时，redis还保存之前的数据
        
        
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

        //清理所有菜品的缓存数据:把所有前缀是dish_的在redis中都清除
//        Set keys = redisTemplate.keys( "dish_*" );
//        redisTemplate.delete( keys );

        //清理某个分类下面的菜品缓存数据,拼接出在redis中的缓存key
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete( key );
        
        return R.success("新增菜品成功！");
    }

    /**
     * 根据条件查询对应的菜品数据（浏览器后端，但不适应手机端）
     * @param dish
     * @return
     */
//    @GetMapping( "/list" )
//    public R<List<Dish>> list( Dish dish ){
//        
////        构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //相当于sql： WHERE (category_id = ?)
//        queryWrapper.eq( dish.getCategoryId() != null , Dish::getCategoryId , dish.getCategoryId() );
////        只查询状态为1（起售）的
//        queryWrapper.eq( Dish::getStatus , 1 );
//        
////        添加排序条件
//        queryWrapper.orderByAsc( Dish::getSort ).orderByDesc( Dish::getUpdateTime );
//
//        
//        List<Dish> list = dishService.list( queryWrapper );
//        return R.success( list );
//        
//    }


    /**
     * 根据条件查询对应的菜品数据（浏览器后端，适应手机端） ; 改进
     * @param dish
     * @return
     */
    @GetMapping( "/list" )
    public R<List<DishDto>> list( Dish dish ){
        List<DishDto> dishDtoList = null;

        //动态构造key  dish_1397844391040167938_1
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus(); 
        
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if( dishDtoList != null ){
        //如果存在，直接返回，无需查询数据库
            return R.success( dishDtoList );
        }
        
        //==========================175-186:新增redis优化！============================

//        构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //相当于sql： WHERE (category_id = ?)
        queryWrapper.eq( dish.getCategoryId() != null , Dish::getCategoryId , dish.getCategoryId() );
//        只查询状态为1（起售）的
        queryWrapper.eq( Dish::getStatus , 1 );

//        添加排序条件
        queryWrapper.orderByAsc( Dish::getSort ).orderByDesc( Dish::getUpdateTime );

        List<Dish> list = dishService.list( queryWrapper );
        
        dishDtoList = list.stream().map( (item) -> {
            DishDto dishDto = new DishDto();
            
            BeanUtils.copyProperties( item , dishDto );
            
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById( categoryId );
            
            if( category != null ){
                String categoryName = category.getName();
                dishDto.setCategoryName( categoryName );
            }
            
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq( DishFlavor::getDishId , dishId );
            List<DishFlavor> dishFlavorList = dishFlavorService.list( lambdaQueryWrapper );
            dishDto.setFlavors( dishFlavorList );
            
            return dishDto;
        }).collect( Collectors.toList() );

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set( key , dishDtoList , 60 , TimeUnit.MINUTES );
        
        
        return R.success( dishDtoList );

    }
}
