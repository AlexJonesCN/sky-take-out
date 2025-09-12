package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {

	/**
	 * 新增菜品和口味
	 * @param dishDTO 菜品信息
	 */
	void saveWithFlavor(DishDTO dishDTO);
}
