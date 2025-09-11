package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 登录信息
     * @return 登录成功的员工信息
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

	/**
	 * 新增员工
	 * @param employeeDTO 员工信息
	 */
	void save(EmployeeDTO employeeDTO);

	/**
	 * 员工信息分页查询
	 * @param employeePageQueryDTO 分页查询参数
	 * @return 分页查询结果
	 */
	PageResult<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

	/**
	 * 启用禁用员工账号
	 * @param status 员工状态
	 * @param id 员工ID
	 */
	void startOrStop(Integer status, Long id);

	/**
	 * 根据ID查询员工信息
	 * @param id 员工ID
	 * @return 员工信息
	 */
	Employee getById(Long id);

	/**
	 * 更新员工信息
	 * @param employeeDTO 员工信息
	 */
	void update(EmployeeDTO employeeDTO);
}
