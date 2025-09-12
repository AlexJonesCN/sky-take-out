package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
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
	@Autowired
	public DishServiceImpl(DishMapper dishMapper, DishFlavorMapper dishFlavorMapper) {
		this.dishMapper = dishMapper;
		this.dishFlavorMapper = dishFlavorMapper;
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
}
