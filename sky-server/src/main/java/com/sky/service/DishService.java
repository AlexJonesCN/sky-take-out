package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

	/**
	 * 新增菜品和口味
	 * @param dishDTO 菜品信息
	 */
	void saveWithFlavor(DishDTO dishDTO);

	/**
	 * 菜品分页查询
	 * @param dishPageQueryDTO 分页查询参数
	 * @return 分页结果
	 */
	PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

	/**
	 * 批量删除菜品
	 * @param ids 菜品id数组
	 */
	void deleteBatch(List<Long> ids);

	/**
	 * 根据id查询菜品信息和口味信息
	 * @param id 菜品id
	 * @return 菜品和口味信息
	 */
	DishVO getByIdWithFlavor(Long id);

	/**
	 * 修改菜品和口味信息
	 * @param dishDTO 菜品信息
	 */
	void updateWithFlavor(DishDTO dishDTO);
}
