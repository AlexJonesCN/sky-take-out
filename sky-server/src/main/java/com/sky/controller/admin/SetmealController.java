package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Tag(name = "套餐管理接口")
public class SetmealController {

	private final SetmealService setmealService;

	@Autowired
	public SetmealController(SetmealService setmealService) {
		this.setmealService = setmealService;
	}

	/**
	 * 新增套餐
	 * @param setmealDTO 接收套餐信息参数
	 * @return 操作结果
	 */
	@PostMapping
	@Operation(summary = "新增套餐")
	public Result<Object> save(@RequestBody SetmealDTO setmealDTO) {
		log.info("新增套餐: {}", setmealDTO);
		setmealService.saveWithDish(setmealDTO);
		return Result.success();
	}

	/**
	 * 套餐分页查询
	 * @param setmealPageQueryDTO 接收分页查询参数
	 * @return 分页查询结果
	 */
	@GetMapping("/page")
	@Operation(summary = "套餐分页查询")
	public Result<PageResult<SetmealVO>> page(SetmealPageQueryDTO setmealPageQueryDTO) {
		log.info("套餐分页查询: {}", setmealPageQueryDTO);
		PageResult<SetmealVO> pageResult = setmealService.pageQuery(setmealPageQueryDTO);
		return Result.success(pageResult);
	}

	/**
	 * 批量删除菜品
	 * @param ids 菜品id数组
	 * @return 操作结果
	 */
	@DeleteMapping
	@Operation(summary = "批量删除套餐")
	public Result<Object> delete(@RequestParam List<Long> ids) {
		log.info("批量删除菜品: {}", ids);
		setmealService.deleteBatch(ids);
		return Result.success();
	}

	/**
	 * 根据id查询套餐信息
	 * @param id 套餐id
	 * @return 套餐信息
	 */
	@GetMapping("/{id}")
	@Operation(summary = "根据id查询套餐信息")
	public Result<SetmealVO> getById(@PathVariable Long id) {
		log.info("根据id查询套餐信息: {}", id);
		SetmealVO setmealVO = setmealService.getByIdWithDish(id);
		return Result.success(setmealVO);
	}

	/**
	 * 修改套餐信息
	 * @param setmealDTO 接收套餐信息参数
	 * @return  操作结果
	 */
	@PutMapping
	@Operation(summary = "修改套餐信息")
	public Result<Object> update(@RequestBody SetmealDTO setmealDTO) {
		log.info("修改套餐信息: {}", setmealDTO);
		setmealService.update(setmealDTO);
		return Result.success();
	}

	/**
	 * 套餐启售/停售
	 * @param status 状态
	 * @param id 套餐id
	 * @return  操作结果
	 */
	@PostMapping("/status/{status}")
	@Operation(summary = "套餐启售/停售")
	public Result<Object> startOrStop(@PathVariable Integer status, Long id) {
		log.info("套餐启售/停售: {}, {}", status, id);
		setmealService.startOrStop(status, id);
		return Result.success();
	}
}
