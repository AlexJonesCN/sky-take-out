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

import java.util.List;

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

	/**
	 * 批量删除菜品
	 * @param ids 菜品id数组
	 * @return 操作结果
	 */
	@DeleteMapping
	@Operation(summary = "批量删除菜品")
	public Result<Object> delete(@RequestParam List<Long> ids) {
		log.info("批量删除菜品: {}", ids);
		dishService.deleteBatch(ids);
		return  Result.success();
	}

	/**
	 * 根据id查询菜品信息和对应的口味信息
	 * @param id 菜品id
	 * @return 菜品信息
	 */
	@GetMapping("/{id}")
	@Operation(summary = "根据id查询菜品信息和对应的口味信息")
	public Result<DishVO> getById(@PathVariable Long id) {
		log.info("根据id查询菜品信息和对应的口味信息: {}", id);
		DishVO dishVO = dishService.getByIdWithFlavor(id);
		return Result.success(dishVO);
	}

	/**
	 * 修改菜品
	 * @param dishDTO 菜品信息
	 * @return 操作结果
	 */
	@PutMapping
	@Operation(summary = "修改菜品")
	public Result<Object> update(@RequestBody DishDTO dishDTO){
		log.info("修改菜品: {}", dishDTO);
		dishService.updateWithFlavor(dishDTO);
		return Result.success();
	}
}
