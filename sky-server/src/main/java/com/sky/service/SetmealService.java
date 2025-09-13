package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
	/**
	 * 新增套餐，同时保存套餐和菜品的关联关系
	 * @param setmealDTO 套餐信息
	 */
	void saveWithDish(SetmealDTO setmealDTO);

	/**
	 * 套餐分页查询
	 * @param setmealPageQueryDTO 分页查询参数
	 * @return 分页查询结果
	 */
	PageResult<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

	/**
	 * 批量删除套餐
	 * @param ids 套餐id数组
	 */
	void deleteBatch(List<Long> ids);

	/**
	 * 根据id查询套餐信息和对应的菜品信息
	 * @param id 套餐id
	 * @return 套餐信息
	 */
	SetmealVO getByIdWithDish(Long id);

	/**
	 * 修改套餐信息，同时更新套餐和菜品的关联关系
	 * @param setmealDTO 套餐信息
	 */
	void update(SetmealDTO setmealDTO);

	/**
	 * 启售或停售套餐
	 * @param status 套餐状态
	 * @param id 套餐id
	 */
	void startOrStop(Integer status, Long id);
}
