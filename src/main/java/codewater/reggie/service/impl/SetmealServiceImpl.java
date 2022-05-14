package codewater.reggie.service.impl;

import codewater.reggie.common.CustomException;
import codewater.reggie.dto.SetmealDto;
import codewater.reggie.entity.Setmeal;
import codewater.reggie.entity.SetmealDish;
import codewater.reggie.mapper.SetmealMapper;
import codewater.reggie.service.SetmealDishService;
import codewater.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-23:23
 * @Function Description ：
 */
@Service 
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper , Setmeal> implements SetmealService{
    
    @Autowired
    private SetmealDishService setmealDishService ; 

    //    新增套餐，同时需要保存套餐和菜品的关联关系  设计两张表的操作，用事务
    @Transactional
    public void saveWithDish(SetmealDto setmealDto){
        //保存套餐的基本信息， 操作setmeal 执行insert
        this.save( setmealDto );

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map( (item) -> {
//            把setmeal的id赋给setmealDish的id ， 这样setmealdish的id就不会空
            item.setSetmealId( setmealDto.getId() );
            return item;
        }).collect( Collectors.toList() );
        
        //保存套餐和菜品的关联信息，操作setmeal_dish表  执行insert
        setmealDishService.saveBatch( setmealDishes );
        
    }


    //删除套餐，同时删除关联的菜品数据  起售的套餐不能删除，所以需要判断
    @Transactional
    public void removeWithDish( List<Long> ids ){
        //大致的sql：select count (*）from setmeal where id in (1,2,3) and status = I
        //查询套餐的状态， 确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in( Setmeal::getId , ids );
        queryWrapper.eq( Setmeal::getStatus , 1 );
        
        //起售状态中，不能删除，抛出一个业务异常
        int count = this.count( queryWrapper );
        if( count > 0 ) {
            throw new CustomException( "套餐正咋爱售卖中, 不能删除！" );
        }
        
        //如果可以删除， 先删除套餐表中的数据---setmeal
        this.removeByIds( ids );
        
        //大致的sql：delete from setmeal dish where setmeal id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //这里还是用setmeal的id是因为在套餐菜品中使用套餐id标记的；用菜品id删除，而菜品id根本不好获取
        lambdaQueryWrapper.in( SetmealDish::getSetmealId , ids );
        
        //再删除关系表中的数据----setmeal_dish
        setmealDishService.remove( lambdaQueryWrapper );
    }
}
