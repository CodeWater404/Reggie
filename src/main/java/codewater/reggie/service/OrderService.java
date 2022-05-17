package codewater.reggie.service;

import codewater.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author ： CodeWater
 * @create ：2022-05-16-23:53
 * @Function Description ：
 */
public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit( Orders orders );
}
