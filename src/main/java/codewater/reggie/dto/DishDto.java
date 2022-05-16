package codewater.reggie.dto;

import codewater.reggie.entity.Dish;
import codewater.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ： CodeWater
 * @create ：2022/5/12-21:24
 * @Function Description ：用于封装接受前端过于复杂的数据  
 * 当没有直接的实体类对应前端数据时
 * 这个是前端新增菜品时用的
*/

@Data
public class DishDto extends Dish {

    /**
     * 菜品对应的口味数据
     */
    private List<DishFlavor> flavors = new ArrayList<>();

    /**
     * 分类
     */
    private String categoryName;

    private Integer copies;
}
