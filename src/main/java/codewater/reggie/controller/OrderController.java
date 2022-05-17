package codewater.reggie.controller;

import codewater.reggie.common.R;
import codewater.reggie.entity.Orders;
import codewater.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ： CodeWater
 * @create ：2022-05-16-23:57
 * @Function Description ：
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单： 前端只传了{remark: "", payMethod: 1, addressBookId: "1526445853153288193"}
     * 原因：因为用户是登录的，所以可以查到用户的id（BaseContext，service层处理），然后再根据用户的id去查对应的
     *      购物车数据，进而生成订单数据
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders ){
        
        log.info("订单数据：{}" , orders );
        
        orderService.submit( orders );
        
        return R.success("下单成功！");
        
    }
    
    // todo 下完单之后，点击“查看订单”没有开发！！！-----标记一下，后面有空尝试自己开发一下
}
