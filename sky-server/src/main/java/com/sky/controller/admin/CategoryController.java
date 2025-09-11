package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@Tag(name = "分类相关接口")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

	@Autowired
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
     * 新增分类
     * @param categoryDTO 前端传递过来的分类信息
	 * @param currentUserId 当前操作用户id
     * @return 新增分类结果
     */
    @PostMapping
    @Operation(summary = "新增分类")
    public Result<Object> save(@RequestBody CategoryDTO categoryDTO, @RequestAttribute Long currentUserId){
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO, currentUserId);
        return Result.success();
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @Operation(summary = "分类分页查询")
    public Result<PageResult<Category>> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：{}", categoryPageQueryDTO);
        PageResult<Category> pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类
     * @param id 分类id
     * @return 删除分类结果
     */
    @DeleteMapping
    @Operation(summary = "删除分类")
    public Result<Object> deleteById(Long id){
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     * @param categoryDTO 分类信息
     * @param currentUserId 当前操作用户id
     * @return 修改分类结果
     */
    @PutMapping
    @Operation(summary = "修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO, @RequestAttribute Long currentUserId){
		log.info("修改分类：{}", categoryDTO);
        categoryService.update(categoryDTO, currentUserId);
        return Result.success();
    }

    /**
     * 启用、禁用分类
     * @param status 1启用，0禁用
     * @param id 分类id
     * @param currentUserId 当前操作用户id
     * @return 启用禁用分类结果
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "启用禁用分类")
    public Result<Object> startOrStop(@PathVariable Integer status, Long id, @RequestAttribute Long currentUserId){
		log.info("启用禁用分类：{}，{}", status, id);
        categoryService.startOrStop(status, id, currentUserId);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @param type 类型
     * @return 分类列表
     */
    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类")
    public Result<List<Category>> list(Integer type){
		log.info("根据类型查询分类：{}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
