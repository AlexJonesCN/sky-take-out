package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id 分类id
     * @return 套餐的数量
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

	/**
	 * 插入套餐
	 * @param setmeal 套餐信息
	 */
	@AutoFill(value = OperationType.INSERT)
	void insert(Setmeal setmeal);

	/**
	 * 套餐分页查询
	 * @param setmealPageQueryDTO 分页查询参数
	 * @return 分页结果
	 */
	Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

	/**
	 * 根据id查询套餐信息
	 * @param id 套餐id
	 * @return 套餐信息
	 */
	@Select("select * from setmeal where id = #{id}")
	Setmeal getById(Long id);

	/**
	 * 根据id删除套餐
	 * @param setmealId 套餐id
	 */
	@Delete("delete from setmeal where id = #{setmealId}")
	void deleteById(Long setmealId);

	/**
	 * 修改套餐信息
	 * @param setmeal 套餐信息
	 */
	@AutoFill(value = OperationType.UPDATE)
	void update(Setmeal setmeal);
}
