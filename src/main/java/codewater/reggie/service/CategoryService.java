package codewater.reggie.service;

import codewater.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-21:41
 * @Function Description ：
 */

public interface CategoryService extends IService<Category> {
    public void remove( Long id );
}
