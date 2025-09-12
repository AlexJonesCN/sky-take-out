package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

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
}
