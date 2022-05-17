package codewater.reggie.controller;

import codewater.reggie.common.BaseContext;
import codewater.reggie.common.R;
import codewater.reggie.entity.ShoppingCart;
import codewater.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ： CodeWater
 * @create ：2022-05-16-22:09
 * @Function Description ：购物车
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart ){
        log.info( "购物车数据：{}" , shoppingCart );

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId( currentId );
        
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq( ShoppingCart::getUserId , currentId );
        
        if( dishId != null ){
        //添加到购物车的是菜品
            queryWrapper.eq( ShoppingCart::getDishId , dishId );
        }else{
        //添加到购物车的是套餐
            queryWrapper.eq( ShoppingCart::getSetmealId , shoppingCart.getSetmealId() );
        }
        
        //查询当前菜品或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne( queryWrapper ); 
        
        if( cartServiceOne != null ){
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber( number + 1 );
            shoppingCartService.updateById( cartServiceOne );
            
        }else{
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber( 1 );
            shoppingCart.setCreateTime( LocalDateTime.now() );
            shoppingCartService.save( shoppingCart );
            //更新好后的shoppingCart赋给cartServiceOne
            cartServiceOne = shoppingCart;
        }
        
        
        return R.success( cartServiceOne );
        
    }

    /**
     * 查看购物车：最新加入购物车的在最上面(根据用户id查，这样不同的用户就能查看不同的订单)
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info( "查看购物车......." );
        
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq( ShoppingCart::getUserId , BaseContext.getCurrentId() );
        queryWrapper.orderByAsc( ShoppingCart::getCreateTime );
        
        List<ShoppingCart> list = shoppingCartService.list( queryWrapper ); 
        
        return R.success( list );
    }

    /**
     * 清空购物车，还是根据用户id来删！！！这样多个菜品信息才能删除
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        log.info( "清空购物车");
        
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq( ShoppingCart::getUserId , BaseContext.getCurrentId() );
        
        shoppingCartService.remove( queryWrapper );
        
        return R.success("清空购物车成功");
    } 
}
