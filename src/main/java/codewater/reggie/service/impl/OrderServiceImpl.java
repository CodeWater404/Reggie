package codewater.reggie.service.impl;

import codewater.reggie.common.BaseContext;
import codewater.reggie.common.CustomException;
import codewater.reggie.entity.*;
import codewater.reggie.mapper.OrderMapper;
import codewater.reggie.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ： CodeWater
 * @create ：2022-05-16-23:54
 * @Function Description ：
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders>  implements OrderService {
    
    @Autowired
    private ShoppingCartService shoppingCartService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AddressBookService addressBookService;
    
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();
        
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq( ShoppingCart::getUserId , userId );
        List<ShoppingCart> shoppingCarts = shoppingCartService.list( queryWrapper );
        
        if( shoppingCarts == null || shoppingCarts.size() == 0 ){
            throw new CustomException( "购物车为空，不能下单！" );
        }

        //查询用户数据
        User user = userService.getById( userId ); 

        //查询地址数据
        long addressBookId =  orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById( addressBookId );
        if( addressBook == null ){
            throw new CustomException( "用户地址信息有误 ， 不能下单 " ) ; 
        }

        //订单号， 为订单随机生成一个唯一的id
        long orderId = IdWorker.getId();
        
        //java.util.concurrent.atomic 的包,主要用于在高并发环境下的高效程序处理,来帮助我们简化同步处理.
        AtomicInteger amount = new AtomicInteger(0);
        
        List<OrderDetail> orderDetails = shoppingCarts.stream().map( (item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId( orderId );
            orderDetail.setNumber( item.getNumber() );
            orderDetail.setDishFlavor( item.getDishFlavor() );
            orderDetail.setDishId( item.getDishId() );
            orderDetail.setSetmealId( item.getSetmealId() );
            orderDetail.setName( item.getName() );
            orderDetail.setImage( item.getImage() );
            orderDetail.setAmount( item.getAmount() );
            amount.addAndGet( item.getAmount().multiply(new BigDecimal(item.getNumber() ) ).intValue() );
            return orderDetail;
        }).collect( Collectors.toList() );
        
        orders.setId( orderId );
        orders.setOrderTime( LocalDateTime.now() );
        //支付时间，因为没有支付功能，所以设置当前时间
        orders.setCheckoutTime( LocalDateTime.now() );
        //订单的状态： 1待付款，2待派送，3已派送，4已完成，5已取消
        orders.setStatus( 2 );
        //总金额
        orders.setAmount( new BigDecimal( amount.get() ) );
        orders.setUserId( userId );
        orders.setNumber( String.valueOf( orderId ) );
        orders.setUserName( user.getName() );
        //收货人
        orders.setConsignee( addressBook.getConsignee() );
        orders.setPhone( addressBook.getPhone() );
        orders.setAddress( (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName() )
                        + ( addressBook.getCityName() == null ? "" : addressBook.getCityName() ) 
                        + ( addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName() )
                        + ( addressBook.getDetail() == null ? "" : addressBook.getDetail() ) );
        
        //向订单表插入数据，一条数据
        this.save( orders );

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch( orderDetails );
        
        //清空购物车数据
        shoppingCartService.remove( queryWrapper );
        
    }
}
