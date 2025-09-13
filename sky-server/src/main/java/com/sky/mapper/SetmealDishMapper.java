package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

	/**
	 * 根据菜品id查询对应的套餐id
	 * @param dishIds 菜品id列表
	 * @return 套餐id列表
	 */
	List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

	/**
	 * 批量插入套餐和菜品的关联关系
	 * @param setmealDishes 套餐和菜品的关联关系列表
	 */
	void insertBatch(List<SetmealDish> setmealDishes);

	/**
	 * 根据套餐id删除对应的套餐和菜品的关联关系
	 * @param setmealId 套餐id
	 */
	@Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
	void deleteBySetmealId(Long setmealId);

	/**
	 * 根据套餐id查询对应的菜品信息
	 * @param setmealId 套餐id
	 * @return 菜品信息列表
	 */
	@Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
	List<SetmealDish> getBySetmealId(Long setmealId);

	/**
	 * 根据菜品id查询对应的套餐id
	 * @param id 菜品id
	 * @return 套餐id列表
	 */
	@Select("select setmeal_id from setmeal_dish where dish_id = #{id}")
	List<Long> getSetmealIdsByDishId(Long id);
}
