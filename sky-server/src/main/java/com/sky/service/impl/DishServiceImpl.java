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
import com.sky.exception.DishDisableFailedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
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
	private final SetmealMapper setmealMapper;

	@Autowired
	public DishServiceImpl(DishMapper dishMapper, DishFlavorMapper dishFlavorMapper, SetmealDishMapper setmealDishMapper, SetmealMapper setmealMapper) {
		this.dishMapper = dishMapper;
		this.dishFlavorMapper = dishFlavorMapper;
		this.setmealDishMapper = setmealDishMapper;
		this.setmealMapper = setmealMapper;
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

	/**
	 * 根据id查询菜品信息和口味信息
	 * @param id 菜品id
	 * @return 菜品和口味信息
	 */
	@Override
	public DishVO getByIdWithFlavor(Long id) {
		// 查询菜品基本信息
		Dish dish = dishMapper.getById(id);
		// 查询菜品口味信息
		List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
		// 组装数据并返回
		DishVO dishVO = new DishVO();
		BeanUtils.copyProperties(dish, dishVO);
		dishVO.setFlavors(dishFlavors);
		return dishVO;
	}

	/**
	 * 修改菜品和口味信息
	 * @param dishDTO 菜品信息
	 */
	@Transactional
	@Override
	public void updateWithFlavor(DishDTO dishDTO) {
		Dish dish = new Dish();
		BeanUtils.copyProperties(dishDTO, dish);
		// 更新菜品表基本信息
		dishMapper.update(dish);
		// 删除原有口味数据
		dishFlavorMapper.deleteByDishId(dishDTO.getId());
		// 新增当前口味数据
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && !flavors.isEmpty()) {
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishDTO.getId()); // 设置口味的菜品id
			});
			// 向口味表插入多条数据
			dishFlavorMapper.insertBatch(flavors);
		}
	}

	/**
	 * 根据分类id查询对应的菜品列表
	 * @param categoryId 分类id
	 * @return 菜品列表
	 */
	@Override
	public List<Dish> list(Long categoryId) {
		Dish dish = Dish.builder()
				.categoryId(categoryId)
				.status(StatusConstant.ENABLE) // 只查询启售的菜品
				.build();
		return dishMapper.list(dish);
	}

	/**
	 * 启售或停售菜品
	 * @param status 菜品状态
	 * @param id 菜品id
	 */
	@Override
	public void startOrStop(Integer status, Long id) {
		// 如果当前是停售状态，判断菜品是否关联了套餐，且套餐是否启售，如果是启售则不能停售
		if(status.equals(StatusConstant.DISABLE)) {
			List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(id);
			if(setmealIds != null && !setmealIds.isEmpty()) {
				setmealIds.forEach(setmealId -> {
					Integer setmealStatus = setmealMapper.getById(setmealId).getStatus();
					if(setmealStatus != null && setmealStatus.equals(StatusConstant.ENABLE)) {
						throw new DishDisableFailedException(MessageConstant.DISH_RELATED_BY_SETMEAL_WHICH_IS_ON_SALE);
					}
				});
			}
		}
		Dish dish = Dish.builder()
				.id(id)
				.status(status)
				.build();
		dishMapper.update(dish);
	}
}
