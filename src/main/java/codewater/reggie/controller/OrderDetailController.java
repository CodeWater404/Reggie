package codewater.reggie.controller;

import codewater.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ： CodeWater
 * @create ：2022-05-16-23:58
 * @Function Description ：
 */
@RestController
@Slf4j
@RequestMapping("/orderDetail")
public class OrderDetailController {
    
    @Autowired
    private OrderDetailService orderDetailService;
    
    
}
