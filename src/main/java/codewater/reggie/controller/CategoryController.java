package codewater.reggie.controller;

import codewater.reggie.common.R;
import codewater.reggie.entity.Category;
import codewater.reggie.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-21:44
 * @Function Description ：分类管理
 */
/*是@controller和@ResponseBody 的结合
@ResponseBody 它的作用简短截说就是指该类中所有的API接口返回的数据，它会以Json字符串的形式返回给客户端*/
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类：
     * 查看前端src/main/resources/backend/page/food/add.html中的代码，可以发现添加菜品校验那里是只验证code==1
     *  所以返回值String
     * @param category
     * @return
     */
    @PostMapping() //设置请求方式是post
    public R<String> save( @RequestBody Category category ){
        log.info( "category:{}"  , category );
//        直接把前端发过来的json数据传进去，用mybatisplus自带的方法进行添加
        categoryService.save( category );
        return R.success( "新增分类成功。。" );
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page") //前端发送的是get
    public R<Page> page( int page , int pageSize ){
//        分页构造器 查询第page页、pageSize条数据
        Page<Category> pageInfo = new Page<>(page , pageSize ) ;
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        添加排序条件，根据菜品的属性sort进行排序
        queryWrapper.orderByAsc( Category::getSort );
        
//        分页查询
        categoryService.page( pageInfo , queryWrapper );
        return R.success( pageInfo );
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(  Long ids ){
        log.info( "删除分类： id为{}" , ids );
        
//        mybatisplus提供的方法  分类关联菜品和套餐时不能直接删除
//        categoryService.removeById( ids );
        
//        完善，(categoryServiceImpl中的的remove方法)
        categoryService.remove( ids );
        
        return R.success( "分类信息删除成功。。。" );
    }
    
    

    /**
     * 根据id修改分类信息：看前端的发送的请求url，请求方式，以及请求数据（reuqtest payload有效载荷），从而
     * 写出方法需要什么东西。@RequestBody就是因为前端发送的数据是一个json格式的，所以要加
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update( @RequestBody Category category ){
        log.info( "修改分类信息：{}" , category );
        
        categoryService.updateById( category );
        return R.success( "修改分类信息成功" );
    }
}
