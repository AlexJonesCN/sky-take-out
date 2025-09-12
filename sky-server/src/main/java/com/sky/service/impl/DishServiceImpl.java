package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

	private final DishMapper dishMapper;
	private final DishFlavorMapper dishFlavorMapper;
	private final SetmealDishMapper setmealDishMapper;
	@Autowired
	public DishServiceImpl(DishMapper dishMapper, DishFlavorMapper dishFlavorMapper, SetmealDishMapper setmealDishMapper) {
		this.dishMapper = dishMapper;
		this.dishFlavorMapper = dishFlavorMapper;
		this.setmealDishMapper = setmealDishMapper;
	}

	/**
	 * 新增菜品和口味
	 * @param dishDTO 菜品信息
	 */
	@Transactional
	@Override
	public void saveWithFlavor(DishDTO dishDTO) {
		Dish dish = new Dish();
		// 将dishDTO中的属性值拷贝到dish中
		BeanUtils.copyProperties(dishDTO, dish);
		// 向菜品表插入一条数据
		dishMapper.insert(dish);
		Long dishId = dish.getId(); // 获取菜品id
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && !flavors.isEmpty()) {
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishId); // 设置口味的菜品id
			});
			// 向口味表插入多条数据
			dishFlavorMapper.insertBatch(flavors);
		}

	}

	/**
	 * 菜品分页查询
	 * @param dishPageQueryDTO 分页查询参数
	 * @return 分页结果
	 */
	@Override
	public PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
		try {
			PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
			try (Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO)) {
				return new PageResult<>(page.getTotal(), page.getResult());
			}
		} catch (Exception e) {
			log.error("分页查询菜品异常", e);
			throw new RuntimeException("查询菜品失败", e);
		} finally {
			PageHelper.clearPage();
		}
	}

	/**
	 * 批量删除菜品
	 * @param ids 菜品id数组
	 */
	@Transactional
	@Override
	public void deleteBatch(List<Long> ids) {
		// 判断当前菜品是否可删除：1 是否启售 2 是否关联了套餐
		for (Long id : ids) {
			Dish dish = dishMapper.getById(id);
			if(dish.getStatus().equals(StatusConstant.ENABLE)) {
				throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
			}
		}
		List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
		if(setmealIds != null && !setmealIds.isEmpty()) {
			throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
		}
		// 删除菜品表数据
		for (Long id : ids) {
			dishMapper.deleteById(id);
			// 删除口味表数据
			dishFlavorMapper.deleteByDishId(id);
		}
	}
}
