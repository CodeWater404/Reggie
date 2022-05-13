package codewater.reggie.dto;

import codewater.reggie.entity.Setmeal;
import codewater.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;


@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
