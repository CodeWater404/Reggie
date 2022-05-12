package codewater.reggie.service.impl;

import codewater.reggie.dto.DishDto;
import codewater.reggie.entity.Dish;
import codewater.reggie.entity.DishFlavor;
import codewater.reggie.mapper.DishMapper;
import codewater.reggie.service.DishFlavorService;
import codewater.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-23:21
 * @Function Description ：
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService ;
    
    //    新增菜品，同时传入菜品对应的口味数据， 需要操作两张表，dish，dish_flavor
    @Transactional  //设计多张表的操作，所以加上事务注解（记得要在boot启动类那开启）
    public void saveWithFlavor(DishDto dishDto){
//        保存基本菜品的基本信息到菜品表dish
        this.save( dishDto );
        
//        菜品id
        Long dishId = dishDto.getId();
        
//        为菜品口味表上的数据附上菜品的id  然后重新变回到集合中
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map( (item) -> {
            item.setDishId( dishId );
            return item;
        }).collect( Collectors.toList() );
        
//        还需要保存口味到口味表dish_flavor   batch保存的是一个集合
        dishFlavorService.saveBatch( flavors );
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor( Long id ){
       //查询菜品基本信息，dish
       Dish dish = this.getById( id );
       
       DishDto dishDto = new DishDto();
       BeanUtils.copyProperties( dish , dishDto );
       
       
//        查询口味信息， dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq( DishFlavor:: getDishId , dish.getId() );
        List<DishFlavor> flavors = dishFlavorService.list( queryWrapper );
        dishDto.setFlavors( flavors );
        
        return dishDto;
    }

    /**
     * //    更新菜品和口味信息
     * @param dishDto
     */
    public void updateWithFlavor( DishDto dishDto ){
//        先更新dish表
        this.updateById( dishDto );
//        清理当前菜品对应口味数据--dish flavor表的de1ete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq( DishFlavor::getDishId , dishDto.getId() );
        
        dishFlavorService.remove( queryWrapper );
        
//        在更新dish_flavors表，insert
        List<DishFlavor> flavors = dishDto.getFlavors();
//        从dishDto中获取口味id ，
        flavors = flavors.stream().map( (item) -> {
            item.setDishId( dishDto.getId() );
            return item;
        }).collect( Collectors.toList() );
        
//        保存口味
        dishFlavorService.saveBatch( flavors );
        
    }
}
