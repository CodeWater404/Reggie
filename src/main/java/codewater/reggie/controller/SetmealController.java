package codewater.reggie.controller;

import codewater.reggie.common.R;
import codewater.reggie.dto.SetmealDto;
import codewater.reggie.entity.Category;
import codewater.reggie.entity.Setmeal;
import codewater.reggie.service.CategoryService;
import codewater.reggie.service.SetmealDishService;
import codewater.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ： CodeWater
 * @create ：2022-05-13-22:34
 * @Function Description ：套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    
    @Autowired
    private SetmealService setmealService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐: @RequestBody是接受来自请求体的数据
     *        而@RequestParam是接受来自请求头的数据
     * @param setmealDto
     * @return
     */
    @PostMapping()
    public R<String> save( @RequestBody SetmealDto setmealDto){
        log.info( "套餐信息：{}" , setmealDto );
        
        setmealService.saveWithDish( setmealDto );
        
        return R.success( "新增菜品信息成功！" );
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page( int page , int pageSize , String name ){
        //分页构造器对象  查询第page页、pageSize条数据
        Page<Setmeal> pageInfo = new Page<>( page , pageSize );
        //因为页面上需要分类的名称，所以采用setmealDto对象，这个里面正好封装了套餐的分类名称
        Page<SetmealDto> dtoPage = new Page<>();  
        
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        
        //添加查询条件，根据name进行like查询   name!=null条件成立时才执行
        queryWrapper.like( name != null , Setmeal::getName , name );
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc( Setmeal::getUpdateTime );
        
        //执行 结束之后page就有了属性值
        setmealService.page( pageInfo  , queryWrapper );
        
        //拷贝封装到dtoPage中  第一个参数是数据源，第二个是目标对象，第三个是忽略的字段records是page类的
        BeanUtils.copyProperties( pageInfo , dtoPage , "records" );
        List<Setmeal> records = pageInfo.getRecords();
        
        //赋值
        List<SetmealDto> list = records.stream().map( (item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //先把普通属性拷贝过去
            BeanUtils.copyProperties( item , setmealDto );
            //拿到分类id
            Long id = item.getCategoryId();
            //根据拿到的分类id获取分类名称
            Category category = categoryService.getById( id );
            if( category != null ){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName( categoryName );
            }
            return setmealDto;
        }).collect( Collectors.toList() );
        
        //records数据列表 这里给上完整的值
        dtoPage.setRecords( list );
        return R.success( dtoPage );
        
    }

    /**
     * 删除套餐：@RequestBody是接受来自请求体的数据
     *        而@RequestParam是接受来自请求头的数据
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids ){
        log.info("ids:{}" , ids );
        
        setmealService.removeWithDish( ids );
        
        return R.success( "套餐数据删除成功" );
    }
}
