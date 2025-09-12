package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Tag(name = "菜品管理")
public class DishController {

	private final DishService dishService;

	@Autowired
	public DishController(DishService dishService) {
		this.dishService = dishService;
	}

	/**
	 * 新增菜品
	 * @param dishDTO 菜品信息
	 * @return 操作结果
	 */
	@PostMapping
	@Operation(summary = "新增菜品")
	public Result<Object> save(@RequestBody DishDTO dishDTO){
		log.info("新增菜品: {}", dishDTO);
		dishService.saveWithFlavor(dishDTO);
		return Result.success();
	}

	/**
	 * 菜品分页查询
	 * @param dishPageQueryDTO 分页查询参数
	 * @return 分页结果
	 */
	@GetMapping("/page")
	@Operation(summary = "菜品分页查询")
	public Result<PageResult<DishVO>> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
		log.info("菜品分页查询: {}", dishPageQueryDTO);
		PageResult<DishVO> pageResult = dishService.pageQuery(dishPageQueryDTO);
		return Result.success(pageResult);
	}
}
