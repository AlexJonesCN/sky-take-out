package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

	private final SetmealMapper setmealMapper;
	private final SetmealDishMapper setmealDishMapper;
	private final DishMapper dishMapper;

	@Autowired
	public SetmealServiceImpl(SetmealMapper setmealMapper, SetmealDishMapper setmealDishMapper, DishMapper dishMapper) {
		this.setmealMapper = setmealMapper;
		this.setmealDishMapper = setmealDishMapper;
		this.dishMapper = dishMapper;
	}

	/**
	 * 新增套餐，同时保存套餐和菜品的关联关系
	 * @param setmealDTO 套餐信息
	 */
	@Transactional
	@Override
	public void saveWithDish(SetmealDTO setmealDTO) {
		Setmeal setmeal = new Setmeal();
		BeanUtils.copyProperties(setmealDTO, setmeal);
		setmealMapper.insert(setmeal);
		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
		Long setmealId = setmeal.getId();
		if(setmealDishes!=null && !setmealDishes.isEmpty()) {
			setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
			setmealDishMapper.insertBatch(setmealDishes);
		}
	}

	/**
	 * 套餐分页查询
	 * @param setmealPageQueryDTO 分页查询参数
	 * @return 分页查询结果
	 */
	@Override
	public PageResult<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
		try {
			PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
			try (Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO)) {
				return new PageResult<>(page.getTotal(), page.getResult());
			}
		} catch (Exception e) {
			log.error("套餐分页查询失败", e);
			throw new RuntimeException("套餐分页查询失败", e);
		} finally {
			PageHelper.clearPage();
		}
	}

	/**
	 * 批量删除套餐
	 * @param ids 套餐id数组
	 */
	@Transactional
	@Override
	public void deleteBatch(List<Long> ids) {
		ids.forEach(id -> {
			Setmeal setmeal = setmealMapper.getById(id);
			if(setmeal.getStatus().equals(StatusConstant.ENABLE)) {
				throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
			}
		});
		ids.forEach(setmealId -> {
			setmealMapper.deleteById(setmealId);
			setmealDishMapper.deleteBySetmealId(setmealId);
		});
	}

	/**
	 * 根据id查询套餐信息和对应的菜品信息
	 * @param id 套餐id
	 * @return 套餐信息
	 */
	@Override
	public SetmealVO getByIdWithDish(Long id) {
		Setmeal setmeal = setmealMapper.getById(id);
		if(setmeal == null) {
			return null;
		}
		SetmealVO setmealVO = new SetmealVO();
		BeanUtils.copyProperties(setmeal, setmealVO);
		List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
		setmealVO.setSetmealDishes(setmealDishes);
		return setmealVO;
	}

	/**
	 * 修改套餐信息，同时更新套餐和菜品的关联关系
	 * @param setmealDTO 套餐信息
	 */
	@Override
	public void update(SetmealDTO setmealDTO) {
		Setmeal setmeal = new Setmeal();
		BeanUtils.copyProperties(setmealDTO, setmeal);
		setmealMapper.update(setmeal);
		Long setmealId = setmeal.getId();
		setmealDishMapper.deleteBySetmealId(setmealId);
		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
		if(setmealDishes!=null && !setmealDishes.isEmpty()) {
			setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
			setmealDishMapper.insertBatch(setmealDishes);
		}
	}

	/**
	 * 启售或停售套餐
	 * @param status 套餐状态
	 * @param id 套餐id
	 */
	@Override
	public void startOrStop(Integer status, Long id) {
		if(status.equals(StatusConstant.ENABLE)) {
			List<Dish> dishes = dishMapper.getBySetmealId(id);
			if(dishes!=null && !dishes.isEmpty()) {
				dishes.forEach(dish -> {
					if(dish.getStatus().equals(StatusConstant.DISABLE)) {
						throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
					}
				});
			}
		}
		Setmeal setmeal = Setmeal.builder()
				.id(id)
				.status(status)
				.build();
		setmealMapper.update(setmeal);
	}
}
