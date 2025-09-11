package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     * @param categoryDTO 前端传递过来的分类信息
     * @param currentUserId 当前操作用户id
     */
    void save(CategoryDTO categoryDTO, Long currentUserId);

    /**
     * 分页查询
     * @param categoryPageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id 分类id
     */
    void deleteById(Long id);

    /**
     * 修改分类
     * @param categoryDTO 分类信息
	 * @param currentUserId 当前操作用户id
     */
    void update(CategoryDTO categoryDTO, Long currentUserId);

    /**
     * 启用、禁用分类
     * @param status 1启用，0禁用
     * @param id 分类id
     * @param currentUserId 当前操作用户id
     */
    void startOrStop(Integer status, Long id, Long currentUserId);

    /**
     * 根据类型查询分类
     * @param type 类型
     * @return 分类列表
     */
    List<Category> list(Integer type);
}
