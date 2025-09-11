package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类业务层
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

	@Autowired
	public CategoryServiceImpl(CategoryMapper categoryMapper, DishMapper dishMapper, SetmealMapper setmealMapper) {
		this.categoryMapper = categoryMapper;
		this.dishMapper = dishMapper;
		this.setmealMapper = setmealMapper;
	}

	/**
     * 新增分类
     * @param categoryDTO 前端传递过来的分类信息
	 * @param currentUserId 当前操作用户id
     */
    public void save(CategoryDTO categoryDTO, Long currentUserId) {
        Category category = new Category();
        //属性拷贝
        BeanUtils.copyProperties(categoryDTO, category);

        //分类状态默认为禁用状态0
        category.setStatus(StatusConstant.DISABLE);

        //设置创建时间、修改时间、创建人、修改人
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(currentUserId);
        category.setUpdateUser(currentUserId);

        categoryMapper.insert(category);
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    public PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
	    try {
		    PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
		    try (Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO)) {
			    return new PageResult<>(page.getTotal(), page.getResult());
		    }
	    } catch (Exception e) {
		    log.error("分页查询分类异常", e);
		    throw new RuntimeException("查询分类失败", e);
	    } finally {
			PageHelper.clearPage();
	    }
    }

    /**
     * 根据id删除分类
     * @param id 分类id
     */
    public void deleteById(Long id) {
        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除分类数据
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     * @param categoryDTO 分类信息
	 * @param currentUserId 当前操作用户id
     */
    public void update(CategoryDTO categoryDTO, Long currentUserId) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        //设置修改时间、修改人
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(currentUserId);

        categoryMapper.update(category);
    }

    /**
     * 启用、禁用分类
     * @param status 1启用，0禁用
     * @param id 分类id
     * @param currentUserId 当前操作用户id
     */
    public void startOrStop(Integer status, Long id, Long currentUserId) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(currentUserId)
                .build();
        categoryMapper.update(category);
    }

    /**
     * 根据类型查询分类
     * @param type 类型
     * @return 分类列表
     */
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}
